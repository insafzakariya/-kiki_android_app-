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

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import lk.mobilevisions.kiki.audio.adapter.PlaylistDetailAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentPlaylistDetailSearchBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchedPlaylistDetailFragment extends Fragment implements PlaylistDetailAdapter.OnPlaylistDetailItemClickListener , AddSongToPlaylistDialogAdapter.OnPlaylistDialogItemClickListener {
    @Inject
    TvManager tvManager;

    FragmentPlaylistDetailSearchBinding binding;
    PlaylistDetailAdapter playlistDetailAdapter;
    List<Song> playlistSongsList = new ArrayList<>();
    List <Integer> songIDs = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    Song song;
    AlertDialog alertDialog;

    int playlistID;
    int userPlaylistID;
    String playlistImage;


    public SearchedPlaylistDetailFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_detail_search, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        binding.playAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.genreSongFragmentSongClickedEvent(song , 0,playlistSongsList);

            }
        });


        playlistID = getArguments().getInt("playlistID");
        String playlistName = getArguments().getString("playlistName");
        String songCount = getArguments().getString("songCount");
        playlistImage = getArguments().getString("playlistImage");
        String playlistYear = getArguments().getString("playlistYear");

        binding.playlistYear.setText(playlistYear.substring(0, 4));

        setDataToPlaylistSongs(playlistID);
        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });
        binding.addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addPlaylistToLibrary(playlistID);
            }
        });


        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.playlistSongsRecyclerview.setLayoutManager(channelsLayoutManager);
        playlistDetailAdapter = new PlaylistDetailAdapter(getActivity(), playlistSongsList, SearchedPlaylistDetailFragment.this);
        binding.playlistSongsRecyclerview.setHasFixedSize(true);
        binding.playlistSongsRecyclerview.setItemViewCacheSize(50);
        binding.playlistSongsRecyclerview.setDrawingCacheEnabled(true);
        binding.playlistSongsRecyclerview.setAdapter(playlistDetailAdapter);

        binding.playlistDetailName.setText(playlistName);
        binding.songCount.setText(songCount + " songs");

        if (playlistImage != null) {
            try {
                Picasso.with(getActivity()).load(URLDecoder.decode(playlistImage, "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(binding.playlistImageView);
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
                binding.playlistSongsRecyclerview.setAdapter(new PlaylistDetailAdapter(getContext(),
                        playlistSongsList, SearchedPlaylistDetailFragment.this));
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

    @Override
    public void onPlaylistItemClick(Song song, int position, List<Song> songs) {
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);

    }

    @Override
    public void onAddSongsItemClick(Song song) {
        songIDs.add(song.getId());
        addToLibraryDialog(song);
    }

    private void addToLibraryDialog(Song song) {
        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                addDataToLibrary();
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

    private void addDataToLibrary(){


        tvManager.addDataToLibraryHash("S",songIDs, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
                System.out.println("sdjkfbsjhdvfhidvfjh 1111111111111111");
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

        AddSongToPlaylistDialogAdapter adapter = new AddSongToPlaylistDialogAdapter(getActivity(),playLists,SearchedPlaylistDetailFragment.this);
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
        userPlaylistID = song.getId();
        addSongToPlaylist(userPlaylistID,songIDs);
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


    private void addPlaylistToLibrary(int playlistID){

        ArrayList<Integer> playlistId = new ArrayList<>();
        playlistId.add(playlistID);
        tvManager.addDataToLibraryHash("P",playlistId, new APIListener<Void>() {
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