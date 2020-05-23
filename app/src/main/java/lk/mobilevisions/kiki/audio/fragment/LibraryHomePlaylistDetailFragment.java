package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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
import lk.mobilevisions.kiki.audio.adapter.PlaylistLibraryDetailAdapter;
import lk.mobilevisions.kiki.audio.event.UserNavigateBackEvent;
import lk.mobilevisions.kiki.audio.model.PlaylistModel;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentLibraryHomePlaylistDetailBinding;
import lk.mobilevisions.kiki.databinding.FragmentLibraryUserPlaylistDetailBinding;
import lk.mobilevisions.kiki.databinding.FragmentPlaylistDetailBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryHomePlaylistDetailFragment extends Fragment implements PlaylistLibraryDetailAdapter.OnPlaylistLibraryItemClickListener {
    @Inject
    TvManager tvManager;

    FragmentLibraryUserPlaylistDetailBinding binding;
    PlaylistLibraryDetailAdapter playlistDetailAdapter;
    List<Song> playlistSongsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    Song song;

    int playlistID;
    String playlistName;
    String playlistImage;
    String updatedImage;

    public LibraryHomePlaylistDetailFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_user_playlist_detail, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        Application.BUS.register(this);
        binding.playAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.genreSongFragmentSongClickedEvent(song , 0,playlistSongsList);


            }
        });

        binding.editPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle playlistBundle=new Bundle();
                playlistBundle.putBoolean("editPlaylist",true);
                playlistBundle.putInt("editPlaylistID",playlistID);
                playlistBundle.putString("editPlaylistName",playlistName);
                playlistBundle.putString("editPlaylistImage",updatedImage);
                PlaylistCreationFragment playlistCreationFragment = new PlaylistCreationFragment();
                playlistCreationFragment.setArguments(playlistBundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_userEdit_playlistTodetail, playlistCreationFragment, "Library to Edit Playlist")
                        .addToBackStack(null)
                        .commit();
            }
        });

        playlistID = getArguments().getInt("playlistID");
        playlistName = getArguments().getString("playlistName");
        String songCount = getArguments().getString("songCount");
        playlistImage = getArguments().getString("playlistImage");
        String playlistYear = getArguments().getString("playlistYear");

        binding.playlistYear.setText(playlistYear.substring(0,4));

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
        playlistDetailAdapter = new PlaylistLibraryDetailAdapter(getActivity(), playlistSongsList, LibraryHomePlaylistDetailFragment.this);
        binding.playlistSongsRecyclerview.setHasFixedSize(true);
        binding.playlistSongsRecyclerview.setItemViewCacheSize(50);
        binding.playlistSongsRecyclerview.setDrawingCacheEnabled(true);
        binding.playlistSongsRecyclerview.setAdapter(playlistDetailAdapter);

        binding.playlistDetailName.setText(playlistName);
        binding.songCount.setText(songCount + " songs");

        getPlaylistData();

        if (updatedImage != null) {
            try {
                Picasso.with(getActivity()).load(URLDecoder.decode(updatedImage, "UTF-8"))
                        .placeholder(R.drawable.program)
//                        .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
//                        .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
                        .into(binding.kikiPlaylistImage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("sgfgdgdfbdfb " + updatedImage);
        }


        return binding.getRoot();

    }



    private void setDataToPlaylistSongs(int playlistID) {


        tvManager.getSongsOfUserPlaylist(playlistID,  new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                playlistSongsList = result;
                binding.playlistSongsRecyclerview.setAdapter(new PlaylistLibraryDetailAdapter(getContext(),
                        playlistSongsList, LibraryHomePlaylistDetailFragment.this));
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

    private void getPlaylistData() {

        tvManager.getPlaylistData(playlistID, new APIListener <PlayList>() {
            @Override
            public void onSuccess(PlayList playList, List<Object> params) {

                binding.playlistDetailName.setText(playList.getName());
                updatedImage = playList.getImage();

                if (updatedImage != null) {
                    try {
                        Picasso.with(getActivity()).load(URLDecoder.decode(updatedImage, "UTF-8"))
                                .placeholder(R.drawable.program)

                                .into(binding.kikiPlaylistImage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println("sgsfgdfg " + updatedImage);
                }


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }


    @Override
    public void onPlaylistLibraryItemClick(Song song, int position, List<Song> songs) {
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);
    }

    @Subscribe
    public void onUserNavigateBack(UserNavigateBackEvent event) {

        getPlaylistData();
        setDataToPlaylistSongs(playlistID);

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

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else  {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}