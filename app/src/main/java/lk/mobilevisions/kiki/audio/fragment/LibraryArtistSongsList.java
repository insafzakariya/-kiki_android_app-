package lk.mobilevisions.kiki.audio.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

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
import lk.mobilevisions.kiki.audio.adapter.LibrarySongsListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentArtistSongsListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryArtistSongsList extends Fragment implements LibrarySongsListAdapter.OnLibrarySongsItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentArtistSongsListBinding binding;

    LibrarySongsListAdapter mAdapter;
    List<Song> songsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    private int artistID;

    public LibraryArtistSongsList() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_songs_list, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        int artistID = getArguments().getInt("artistID");


        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.artistSongsRecyclerview.setLayoutManager(channelsLayoutManager);
        binding.artistSongsRecyclerview.setHasFixedSize(true);
        binding.artistSongsRecyclerview.setItemViewCacheSize(50);
        binding.artistSongsRecyclerview.setDrawingCacheEnabled(true);
        binding.artistSongsRecyclerview.setAdapter(mAdapter);
        getArtistSongs(artistID);

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });


        return binding.getRoot();

    }


    private void getArtistSongs(int artistID) {

        tvManager.getArtistSongs(artistID, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                songsList = result;
                mAdapter = new LibrarySongsListAdapter(getContext(),
                        songsList, LibraryArtistSongsList.this);
                binding.artistSongsRecyclerview.setAdapter(mAdapter);
                if (result.size() <= 0) {
                    binding.artistSongsRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.artistSongsRecyclerview.setVisibility(View.VISIBLE);
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
        tvManager.addSongsToTempTable(sessionId, songId,"S", new APIListener<Void>() {
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
