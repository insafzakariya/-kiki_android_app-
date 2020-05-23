package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.github.ybq.endless.Endless;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.GenreWiseSongsAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibrarySongsListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentGenreWiseSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibrarySongListFragment extends Fragment implements LibrarySongsListAdapter.OnLibrarySongsItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentGenreWiseSongsBinding binding;

    LibrarySongsListAdapter mAdapter;
    List<Song> genreSongsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    private Endless endless;
    public LibrarySongListFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_genre_wise_songs, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

//        String genre = getArguments().getString("genre");

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.genreWiseRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new LibrarySongsListAdapter(getActivity(), genreSongsList, LibrarySongListFragment.this);
        binding.genreWiseRecyclerview.setHasFixedSize(true);
        binding.genreWiseRecyclerview.setItemViewCacheSize(50);
        binding.genreWiseRecyclerview.setDrawingCacheEnabled(true);

        View loadingView = View.inflate(getActivity(), R.layout.layout_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(binding.genreWiseRecyclerview,
                loadingView
        );
        endless.setAdapter(mAdapter);
        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                binding.spinKit.setVisibility(View.VISIBLE);
                getGenreSongs(page);

            }
        });
        binding.genreWiseRecyclerview.setAdapter(mAdapter);
        getGenreSongs(0);

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }

    private void getGenreSongs(final int page) {
//        int arraySize = Utils.SharedPrefUtil.getIntFromSharedPref(getActivity(), 0);
        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = genreSongsList.size();
        }
        tvManager.getAllSongs(offset, 10, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                genreSongsList.addAll(songs);
                if (page == 0) {
                    mAdapter.setData(songs);
                } else {
                    mAdapter.addData(songs);
                    endless.loadMoreComplete();
                    binding.spinKit.setVisibility(View.GONE);

                }
                if(songs.size()>10){
                    endless.setLoadMoreAvailable(false);
                }

                binding.spinKit.setVisibility(View.GONE);
                binding.aviProgress.setVisibility(View.GONE);


//                Utils.SharedPrefUtil.saveIntToSharedPref(getActivity(),
//                        genre, genreSongsList.size());

            }

            @Override
            public void onFailure(Throwable t) {
                binding.spinKit.setVisibility(View.GONE);

            }
        });
    }


    @Override
    public void onLibrarySongsItemClick(Song song, int position, List<Song> songs) {

        if(!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
            Application.getInstance().addSongToPlayList(song.getId());

        }else{
            Application.getInstance().removeSongFromPlayList(song.getId());
        }
        addSongsToPlaylist(song.getId());
        mAdapter.notifyDataSetChanged();

    }

    private void addSongsToPlaylist(int songId) {
        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
        }
        System.out.println("sessionnnn 11111 " + sessionId);
        tvManager.addSongsToTempTable(sessionId, songId, "S", new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }

}