package lk.mobilevisions.kiki.video.fragment;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentMylistVideoBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.widgets.EndlessRecyclerViewScrollListener;
import lk.mobilevisions.kiki.video.activity.VideoChildModeActivity;
import lk.mobilevisions.kiki.video.activity.VideoEpisodeActivity;
import lk.mobilevisions.kiki.video.adapter.MylistAdapter;

public class VideoMylistFragment extends Fragment implements MylistAdapter.OnProgramItemClickListener {

    @Inject
    TvManager tvManager;

    FragmentMylistVideoBinding binding;
    private int programsLimit = 200;
    private int channelId = 0;
    private List<Program> currentPrograms;
   private MylistAdapter mylistAdapter;
    private boolean hasPrograms;
    GridLayoutManager channelsLayoutManager;
    EndlessRecyclerViewScrollListener recyclerViewScrollListener;
    public VideoMylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mylist_video, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        Application.BUS.register(this);
        channelsLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.mylistRecyclerview.setLayoutManager(channelsLayoutManager);
        binding.mylistRecyclerview.setHasFixedSize(false);
        recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(channelsLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(getContext(), "kids_mode_password", "");
                boolean isKidsModeEnabled = false;
                if (kidsModePassword != null && !"".equals(kidsModePassword)) {
                    isKidsModeEnabled = true;
                } else {
                    isKidsModeEnabled = false;
                }
                tvManager.getSubscribedPrograms(channelId, totalItemsCount, programsLimit, new APIListener<List<Program>>() {
                    @Override
                    public void onSuccess(List<Program> programs, List<Object> params) {
                        if(hasPrograms && (programs == null || programs.size() == 0)){
                            return;
                        }
                        currentPrograms.addAll(programs);
                        mylistAdapter.notifyDataSetChanged();
                        binding.mylistRecyclerview.setAdapter(mylistAdapter);
                        binding.aviProgress.setVisibility(View.GONE);
                        if (programs.size() <= 0) {
                            binding.mylistRecyclerview.setVisibility(View.GONE);
                            binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                        } else {
                            binding.mylistRecyclerview.setVisibility(View.VISIBLE);
                            binding.TextViewEmptyVideos.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                },isKidsModeEnabled);
            }

        };
        binding.mylistRecyclerview.addOnScrollListener(recyclerViewScrollListener);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscribeList();
    }

    private void loadSubscribeList(){
        String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(getContext(), "kids_mode_password", "");
        boolean isKidsModeEnabled = false;
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            isKidsModeEnabled = true;
        } else {
            isKidsModeEnabled = false;
        }
        tvManager.getSubscribedPrograms(channelId,0, programsLimit, new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> programs, List<Object> params) {
                mylistAdapter = new MylistAdapter(getActivity(), programs, VideoMylistFragment.this);
                currentPrograms = programs;
                binding.mylistRecyclerview.clearOnScrollListeners();
                binding.mylistRecyclerview.setAdapter(mylistAdapter);
                binding.aviProgress.setVisibility(View.GONE);
                if (programs.size() <= 0) {

                    binding.mylistRecyclerview.setVisibility(View.GONE);
                    binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                } else {
                    binding.mylistRecyclerview.setVisibility(View.VISIBLE);
                    binding.TextViewEmptyVideos.setVisibility(View.GONE);
                    hasPrograms = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        },isKidsModeEnabled);
    }



    @Override
    public void onProgramItemClick(View view, Program program, int position) {
        Intent intentEpisodes = new Intent(getActivity(), VideoEpisodeActivity.class);
        intentEpisodes.putExtra("program", program);
        getActivity().startActivityForResult(intentEpisodes, 1005);
        JSONObject props = new JSONObject();
        try {
            props.put("ProgramName", program.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((Application) getActivity().getApplication()).getMixpanelAPI().track("Program Selected", props);
    }

    @Subscribe
    public void onChildModeActivated(VideoEpisodeActivity.SubscribeEvent event) {
        loadSubscribeList();
    }
    @Subscribe
    public void onChildModeActivated(VideoChildModeActivity.ChildModeOnEvent event) {
        loadSubscribeList();
    }
    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }
}