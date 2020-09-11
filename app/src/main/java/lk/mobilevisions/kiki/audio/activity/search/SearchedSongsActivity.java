package lk.mobilevisions.kiki.audio.activity.search;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.endless.Endless;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.AddSongToPlaylistDialogAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreWiseSongsAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.ActivitySearchedsongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class SearchedSongsActivity extends AppCompatActivity implements GenreWiseSongsAdapter.OnYouMightLikeItemClickListener, AddSongToPlaylistDialogAdapter.OnPlaylistDialogItemClickListener, View.OnClickListener {
    @Inject
    TvManager tvManager;


    ActivitySearchedsongsBinding binding;

    GenreWiseSongsAdapter mAdapter;
    List<Song> searchedSongsList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    private Endless endless;

    int playlistID;
    List<Integer> songId = new ArrayList<>();
    private AlertDialog alertDialog;

    public SearchedSongsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_searchedsongs);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }

        String searchKey = getIntent().getStringExtra("searchKey");

        channelsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.searchedsongsRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new GenreWiseSongsAdapter(this, searchedSongsList, SearchedSongsActivity.this);
        binding.searchedsongsRecyclerview.setHasFixedSize(true);
        binding.searchedsongsRecyclerview.setItemViewCacheSize(50);
        binding.searchedsongsRecyclerview.setDrawingCacheEnabled(true);

        binding.includedToolbar.titleTextview.setText("Search");
        binding.includedToolbar.backImageview.setOnClickListener(this);

        View loadingView = View.inflate(this, R.layout.layout_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(binding.searchedsongsRecyclerview,
                loadingView
        );
        endless.setAdapter(mAdapter);
        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                binding.spinKit.setVisibility(View.VISIBLE);
                getGenreSongs(page, searchKey);

            }
        });
        binding.searchedsongsRecyclerview.setAdapter(mAdapter);
        getGenreSongs(0, searchKey);

//        binding.backImageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                onBackPressed();
//
//            }
//        });

    }

    private void getGenreSongs(final int page, final String genre) {
        int arraySize = Utils.SharedPrefUtil.getIntFromSharedPref(this,
                genre, 0);
        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = arraySize;
        }
        tvManager.getSearchSongsbyType(offset, 10, genre, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                System.out.println("sjhdbcdhscbh " + songs.size());
                System.out.println("sjhdbcdhscbh 0000" + songs.size());
                searchedSongsList.addAll(songs);
                if (page == 0) {
                    mAdapter.setData(songs);
                } else {
                    mAdapter.addData(songs);
                    endless.loadMoreComplete();
                    binding.spinKit.setVisibility(View.GONE);

                }
                if (songs.size() > 10) {
                    endless.setLoadMoreAvailable(false);
                }

                binding.spinKit.setVisibility(View.GONE);
                binding.aviProgress.setVisibility(View.GONE);


                Utils.SharedPrefUtil.saveIntToSharedPref(getApplicationContext(),
                        genre, searchedSongsList.size());

            }

            @Override
            public void onFailure(Throwable t) {
                binding.spinKit.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onYouMightLikeItemClick(Song song, int position, List<Song> songs) {

        AudioDashboardActivity hhh = (AudioDashboardActivity) getApplicationContext();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);

    }

    @Override
    public void onAddSongsItemClick(Song song) {

        songId.add(song.getId());
        addToLibraryDialog(song);
    }

    private void addToLibraryDialog(Song song) {
        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    private void addDataToLibrary(Song song) {

        tvManager.addDataToLibraryHash("S", songId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getApplicationContext(), t);
            }
        });

    }

    private void addToLibraryPlaylistDialog(List<PlayList> playLists) {

        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_playlist_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        RecyclerView recyclerView = (RecyclerView) alertDialog.findViewById(R.id.addSongToPlaylistRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AddSongToPlaylistDialogAdapter adapter = new AddSongToPlaylistDialogAdapter(this, playLists, SearchedSongsActivity.this);
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
                Utils.Error.onServiceCallFail(getApplicationContext(), t);
            }
        });
    }

    @Override
    public void onPlaylistDialogItemClick(PlayList song, int position, List<PlayList> songs) {
        playlistID = song.getId();
        addSongToPlaylist(playlistID, songId);
        alertDialog.dismiss();
    }

    private void addSongToPlaylist(int playlistId, List<Integer> songIdList) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            default:

        }
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


