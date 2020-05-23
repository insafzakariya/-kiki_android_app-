package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.LatestPlaylistAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryHomePlaylistListAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryKikiPlaylistAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryPlaylistAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.databinding.FragmentGenreWisePlaylistBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryPlaylistListFragment extends Fragment implements LibraryPlaylistAdapter.OnKikiPlaylistItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentGenreWisePlaylistBinding binding;
    LibraryPlaylistAdapter mAdapter;
    List<PlayList> playlistArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;

    public LibraryPlaylistListFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_genre_wise_playlist, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);


        int genreId = getArguments().getInt("genreId");

        System.out.println("Check artist 1414141414 " + genreId);

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.genrePlaylistRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new LibraryPlaylistAdapter(getActivity(), playlistArrayList, LibraryPlaylistListFragment.this);
        binding.genrePlaylistRecyclerview.setHasFixedSize(true);
        binding.genrePlaylistRecyclerview.setItemViewCacheSize(50);
        binding.genrePlaylistRecyclerview.setDrawingCacheEnabled(true);
        binding.genrePlaylistRecyclerview.setAdapter(mAdapter);
        setDataToPlaylists();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }


    private void setDataToPlaylists() {

        tvManager.getDailyMixNew(0,100,new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                playlistArrayList = result;
                binding.genrePlaylistRecyclerview.setAdapter(new LibraryPlaylistAdapter(getActivity(),
                        playlistArrayList, LibraryPlaylistListFragment.this));
                if (result.size() <= 0) {
                    binding.genrePlaylistRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.genrePlaylistRecyclerview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }

    @Override
    public void onKikiPlaylistItemClick(PlayList song, int position, List<PlayList> songs) {
        Bundle bundle=new Bundle();
        bundle.putInt("playlistID", song.getId());
        bundle.putString("playlistName", song.getName());
        bundle.putString("songCount", song.getSongCount());
        bundle.putString("playlistImage", song.getImage());
        bundle.putString("playlistYear", song.getDate());
        LibraryPlaylistDetailFragment libraryPlaylistDetailFragment = new LibraryPlaylistDetailFragment();
        libraryPlaylistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.Library_playlist_detail_container, libraryPlaylistDetailFragment, "playlistID")
                .addToBackStack(null)
                .commit();
    }


    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

    }


}