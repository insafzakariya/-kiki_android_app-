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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.GlobalPlayer;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.LayoutListEpisodesBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import lk.mobilevisions.kiki.ui.main.MainActivity;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import lk.mobilevisions.kiki.ui.main.home.EpisodesAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveVideosFragment extends ViewVideosFragment {

    @Inject
    TvManager tvManager;


    RecyclerView.Adapter episodesAdapter;
    RecyclerView.LayoutManager episodesLayoutManager;
    LayoutListEpisodesBinding binding;
    private int channelId;

    public LiveVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.PLAYER.setVideoType(GlobalPlayer.VideoType.LIVE_VIDEOS);
        channelId = ((Application) getActivity().getApplication()).getSelectedChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((Application) getActivity().getApplication()).getInjector().inject(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_list_episodes, container, false);
        Application.BUS.register(this);
        episodesLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewEpisodes.setLayoutManager(episodesLayoutManager);

        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.LIVE_VIDEOS);
        }
    }

    @Override
    public void onEpisodeItemClick(View view, Episode episode, int position) {
        Intent intent = new Intent(getActivity(), FullScreenActivity.class);
        Application.PLAYER.setCurrentVideo(position);
        this.startActivityForResult(intent, 1003);
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onTabChanged(MainActivity.TabChangedEvent event) {
        if (event.getPosition() == VideoTabs.LIVE_VIDEOS.getValue()) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.LIVE_VIDEOS);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            tvManager.getLiveVideos(channelId, new APIListener<List<Program>>() {
                @Override
                public void onSuccess(List<Program> programs, List<Object> params) {
                    List<Episode> episodes = new ArrayList<Episode>();
                    for(Program program: programs){
                        program.getEpisode().setName(program.getName() + " - " + program.getEpisode().getName());
                        episodes.add(program.getEpisode());
                    }

                    episodesAdapter = new EpisodesAdapter(getContext(), episodes, LiveVideosFragment.this);
                    Application.PLAYER.setEpisodes(episodes);
                    Application.PLAYER.setCurrentVideoPosition(0);
                    binding.recycleViewEpisodes.setAdapter(episodesAdapter);
                    binding.aviProgress.setVisibility(View.GONE);
                    if(episodes.size() <= 0){
                        binding.recycleViewEpisodes.setVisibility(View.GONE);
                        binding.textViewEmptyVideos.setVisibility(View.VISIBLE);
                    }else{
                        binding.recycleViewEpisodes.setVisibility(View.VISIBLE);
                        binding.textViewEmptyVideos.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.Error.onServiceCallFail(getActivity(), t);
                }
            });
        }
    }

}
