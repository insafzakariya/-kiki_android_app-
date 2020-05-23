package lk.mobilevisions.kiki.ui.main.videos;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.GlobalPlayer;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.LayoutSubscribedProgramsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.episodes.EpisodesActivity;

import lk.mobilevisions.kiki.ui.main.MainActivity;
import lk.mobilevisions.kiki.ui.main.home.ProgramsAdapter;
import lk.mobilevisions.kiki.ui.widgets.EndlessRecyclerViewScrollListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribedVideosFragment extends Fragment implements ProgramsAdapter.OnProgramItemClickListener {

    @Inject
    TvManager tvManager;

    LayoutSubscribedProgramsBinding binding;

    ProgramsAdapter programsAdapter;
    LinearLayoutManager programsLayoutManager;

    private List<Program> currentPrograms;

    private int programsLimit = 200;
    private boolean hasPrograms;

    EndlessRecyclerViewScrollListener recyclerViewScrollListener;
    private int channelId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((Application) getActivity().getApplication()).getInjector().inject(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_subscribed_programs, container, false);
        Application.BUS.register(this);

        programsLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewPrograms.setLayoutManager(programsLayoutManager);

        binding.recycleViewPrograms.setHasFixedSize(false);
        recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(programsLayoutManager) {
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
                        programsAdapter.notifyDataSetChanged();
                        binding.recycleViewPrograms.setAdapter(programsAdapter);
                        binding.aviProgress.setVisibility(View.GONE);
                        if (programs.size() <= 0) {
                            binding.recycleViewPrograms.setVisibility(View.GONE);
                            binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                        } else {
                            binding.recycleViewPrograms.setVisibility(View.VISIBLE);
                            binding.TextViewEmptyVideos.setVisibility(View.GONE);
                        }

                        // binding.swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                },isKidsModeEnabled);
            }

        };
        binding.recycleViewPrograms.addOnScrollListener(recyclerViewScrollListener);

        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.SUBSCRIBE_VIDEOS);
        }
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }


    @Subscribe
    public void onTabChanged(MainActivity.TabChangedEvent event) {
        if (event.getPosition() == VideoTabs.SUBSCRIBED_VIDEOS.getValue()) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.SUBSCRIBE_VIDEOS);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            hasPrograms = false;
            if(programsAdapter != null){
                programsAdapter.notifyDataSetChanged();
            }
            if(recyclerViewScrollListener != null){
                recyclerViewScrollListener.resetState();
            }
            loadSubscribeList();
        }
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
                programsAdapter = new ProgramsAdapter(getActivity(), programs, SubscribedVideosFragment.this
                        , true, false, true, true);
                currentPrograms = programs;
                binding.recycleViewPrograms.setAdapter(programsAdapter);
                binding.aviProgress.setVisibility(View.GONE);
                if (programs.size() <= 0) {
                    binding.recycleViewPrograms.setVisibility(View.GONE);
                    binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                } else {
                    binding.recycleViewPrograms.setVisibility(View.VISIBLE);
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
    public void onProgramItemClick(View view, final Program program, int position, final ProgramsAdapter.ProgramItemClickCallback callback) {
        switch (view.getId()) {
            case R.id.imageButtonSubscribe:
//                if(program.isSubscribed()){
//                    new MaterialDialog.Builder(getActivity())
//                            .title(getString(R.string.app_name))
//                            .content("Are you sure you want to unsubscribe from this program?")
//                            .positiveText("Yes")
//                            .negativeText("No")
//                            .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                @Override
//                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    tvManager.unSubscribeProgram(Integer.valueOf(program.getId()), new APIListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void result, List<Object> params) {
//                                            Utils.Dialog.createOKDialog(getActivity(), "You have unsubscribed from the " + program.getName() + " program successfully!", null);
//                                            loadSubscribeList();
//                                        }
//
//                                        @Override
//                                        public void onFailure(Throwable t) {
//                                            Utils.Error.onServiceCallFail(getActivity(), t);
//                                        }
//                                    });
//                                }
//                            })
//                            .show();
//                }
                break;

            default:
                Intent intentEpisodes = new Intent(getActivity(), EpisodesActivity.class);
                intentEpisodes.putExtra("program", program);
                getActivity().startActivityForResult(intentEpisodes, 1005);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Application.PLAYER.setVideoType(GlobalPlayer.VideoType.SUBSCRIBE_VIDEOS);
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.PLAYER.setVideoType(GlobalPlayer.VideoType.SUBSCRIBE_VIDEOS);
        channelId = ((Application) getActivity().getApplication()).getSelectedChannel();
        loadSubscribeList();
    }
}
