package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.endless.Endless;

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
import lk.mobilevisions.kiki.audio.adapter.AddSongToPlaylistDialogAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreWiseSongsAdapter;
import lk.mobilevisions.kiki.audio.model.PlaylistModel;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentGenreWiseSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GenreWiseSongsFragment extends Fragment implements GenreWiseSongsAdapter.OnYouMightLikeItemClickListener , AddSongToPlaylistDialogAdapter.OnPlaylistDialogItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentGenreWiseSongsBinding binding;

    GenreWiseSongsAdapter mAdapter;
    List<Song> genreSongsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    private Endless endless;

    int playlistID;
    List <Integer> songId = new ArrayList<>();
    private AlertDialog alertDialog;

    public GenreWiseSongsFragment() {
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

        String genre = getArguments().getString("genre");

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.genreWiseRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new GenreWiseSongsAdapter(getActivity(), genreSongsList, GenreWiseSongsFragment.this);
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
                    getGenreSongs(page, genre);

            }
        });
        binding.genreWiseRecyclerview.setAdapter(mAdapter);
        getGenreSongs(0,genre);

//        binding.backImageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//                hhh.onBackPressed();
//            }
//        });

        return binding.getRoot();

    }

    private void getGenreSongs(final int page, final String genre) {
        int arraySize = Utils.SharedPrefUtil.getIntFromSharedPref(getActivity(),
                genre, 0);
        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = arraySize;
        }
        tvManager.getGenreSongs(offset, 10, genre, new APIListener<List<Song>>() {
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


                Utils.SharedPrefUtil.saveIntToSharedPref(getActivity(),
                        genre, genreSongsList.size());

            }

            @Override
            public void onFailure(Throwable t) {
                binding.spinKit.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onYouMightLikeItemClick(Song song, int position, List<Song> songs) {

        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);

    }

    @Override
    public void onAddSongsItemClick(Song song) {

        songId.add(song.getId());
        addToLibraryDialog(song);
    }

    private void addToLibraryDialog(Song song) {
        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        TextView songTitle = (TextView) alertDialog.findViewById(R.id.add_song_title);
        songTitle.setText(song.getName());
        TextView addToLibrary = (TextView) alertDialog.findViewById(R.id.add_to_library);
        TextView addToPlaylist = (TextView) alertDialog.findViewById(R.id.add_to_playlist);
        ImageView cancelView = (ImageView) alertDialog.findViewById(R.id.cancel_view);


        addToLibrary.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                addDataToLibrary(song);
                alertDialog.dismiss();

            }
        });
        addToPlaylist.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                getPlaylists();

            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

    }

    private void addDataToLibrary(Song song){

        tvManager.addDataToLibraryHash("S",songId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    private void addToLibraryPlaylistDialog(List<PlayList> playLists) {

        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_playlist_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        RecyclerView recyclerView = (RecyclerView) alertDialog.findViewById(R.id.addSongToPlaylistRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        AddSongToPlaylistDialogAdapter adapter = new AddSongToPlaylistDialogAdapter(getActivity(),playLists,GenreWiseSongsFragment.this);
        recyclerView.setAdapter(adapter);

        TextView songTitle = (TextView) alertDialog.findViewById(R.id.add_to_playlist_text);
        ImageView cancelView = (ImageView) alertDialog.findViewById(R.id.cancel_view);

        cancelView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

    }

    private void getPlaylists() {
        tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                if (playLists.size() == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_playlist_found), Toast.LENGTH_SHORT).show();
                } else {
                    addToLibraryPlaylistDialog(playLists);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }

    @Override
    public void onPlaylistDialogItemClick(PlayList song, int position, List<PlayList> songs) {
            playlistID = song.getId();
            addSongToPlaylist(playlistID,songId);
            alertDialog.dismiss();
    }

    private void addSongToPlaylist(int playlistId, List<Integer> songIdList ) {

        tvManager.addSongsToPlaylist(playlistId, songIdList, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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