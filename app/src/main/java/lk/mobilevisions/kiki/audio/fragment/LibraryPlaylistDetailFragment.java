package lk.mobilevisions.kiki.audio.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import lk.mobilevisions.kiki.audio.adapter.LibraryPlaylistSongAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentLibraryPlaylistDetailBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryPlaylistDetailFragment extends Fragment implements LibraryPlaylistSongAdapter.OnLibraryPlaylistItemClickListener {
    @Inject
    TvManager tvManager;

    FragmentLibraryPlaylistDetailBinding binding;
    LibraryPlaylistSongAdapter libraryPlaylistSongAdapter;
    List<Song> playlistSongsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    Song song;

   private int playlistID;

    public LibraryPlaylistDetailFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_playlist_detail, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        binding.playAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.genreSongFragmentSongClickedEvent(song , 0,playlistSongsList);

            }
        });

        binding.addAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.addAllSongs.getText().toString().contains("Add all")){
                    addAllSongs();
                }else{
                    removeAllSongs();
                }


            }
        });

        playlistID = getArguments().getInt("playlistID");
        String playlistName = getArguments().getString("playlistName");
        String songCount = getArguments().getString("songCount");
        String playlistImage = getArguments().getString("playlistImage");
        String playlistYear = getArguments().getString("playlistYear");

        binding.playlistYear.setText(playlistYear.substring(0,4));

        if(Application.getInstance().getPlaylistIds().contains(playlistID)){
            binding.addAllSongs.setText("Remove all");
        }else{
            binding.addAllSongs.setText("Add all");
        }


        setDataToPlaylistSongs(playlistID);
        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.playlistSongsRecyclerview.setLayoutManager(channelsLayoutManager);
        binding.playlistSongsRecyclerview.setHasFixedSize(true);
        binding.playlistSongsRecyclerview.setItemViewCacheSize(50);
        binding.playlistSongsRecyclerview.setDrawingCacheEnabled(true);
        binding.playlistSongsRecyclerview.setAdapter(libraryPlaylistSongAdapter);

        binding.playlistDetailName.setText(playlistName);
        binding.songCount.setText(songCount + " songs");

        if (playlistImage != null) {
            try {
                Picasso.with(getActivity()).load(URLDecoder.decode(playlistImage, "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(binding.playlistDetailImageview);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        return binding.getRoot();

    }



    private void setDataToPlaylistSongs(int playlistID) {

        tvManager.getSongsOfDailymix(playlistID,  new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                playlistSongsList = result;
                libraryPlaylistSongAdapter = new LibraryPlaylistSongAdapter(getActivity(), playlistSongsList,LibraryPlaylistDetailFragment.this);
                binding.playlistSongsRecyclerview.setAdapter(libraryPlaylistSongAdapter);
                if (result.size() <= 0) {
                    binding.playlistSongsRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.playlistSongsRecyclerview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }


    private void removeAllSongs(){

        for(Song song : playlistSongsList){
            if(Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
                Application.getInstance().removeSongFromPlayList(song.getId());
            }
        }

        if(Application.getInstance().getPlaylistIds().contains(playlistID)){
            Application.getInstance().removePlayListId(playlistID);
        }
        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
            System.out.println("playlistcheck 22222 ");
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
            System.out.println("playlistcheck 11111 ");
        }
        tvManager.addSongsToTempTable(sessionId, playlistID,"P", new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
        binding.addAllSongs.setText("Add all");
        libraryPlaylistSongAdapter.notifyDataSetChanged();

    }

    private void addAllSongs(){

        for(Song song : playlistSongsList){
            if(!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
                Application.getInstance().addSongToPlayList(song.getId());
            }
        }

        if(!Application.getInstance().getPlaylistIds().contains(playlistID)){
            Application.getInstance().addPlayListId(playlistID);
        }
        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
            System.out.println("playlistcheck 22222 ");
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
            System.out.println("playlistcheck 11111 ");
        }
        tvManager.addSongsToTempTable(sessionId, playlistID,"P", new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
        binding.addAllSongs.setText("Remove all");

        libraryPlaylistSongAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPlaylistItemClick(Song song, int position, List<Song> songs) {

        if(!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
            Application.getInstance().addSongToPlayList(song.getId());
            System.out.println("playlistcheck 44444 ");

        }else{
            Application.getInstance().removeSongFromPlayList(song.getId());
            System.out.println("playlistcheck 33333 ");
        }
        addSongsToPlaylist(song.getId());
        libraryPlaylistSongAdapter.notifyDataSetChanged();

    }

    private void addSongsToPlaylist(int songId) {
        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
            System.out.println("playlistcheck 2253434222 " + sessionId);
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
            System.out.println("playlistcheck 11111 ");
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