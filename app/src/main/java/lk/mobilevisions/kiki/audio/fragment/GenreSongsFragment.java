package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.endless.Endless;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.DailyMixAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreArtistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PopularSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentGenreSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class GenreSongsFragment extends Fragment implements  GenreSongsVerticalAdapter.OnGenreSongsItemActionListener,GenreArtistVerticalAdapter.OnArtistsItemActionListener, PlaylistVerticalAdapter.OnPlaylistItemActionListener {

    @Inject
    TvManager tvManager;

    FragmentGenreSongsBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    List<PlayList> genrePlaylistArrayList = new ArrayList<>();
    List<Song> genreSongsArrayList = new ArrayList<>();
    List<Artist> genreArtistsArrayList = new ArrayList<>();

    @Inject
    SubscriptionsManager subscriptionsManager;
//    DailyMixAdapter dailyMixAdapter;
    GenreSongsVerticalAdapter genreSongsVerticalAdapter;
    GenreArtistVerticalAdapter artistsVerticalAdapter;
    PlaylistVerticalAdapter playlistVerticalAdapter;

    private int artistID;
    private int lastRandomNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_genre_songs, container, false);

        ((Application) getActivity().getApplication()).getInjector().inject(this);

        String genre = getArguments().getString("genre");
        int genreId = getArguments().getInt("genreId");
        System.out.println("Check artist 856486546 " + genre);
        setDataToPlaylists(genreId);
        setupPlaylists();
        setupPopularSongs();
        setupArtists();
//        setDataToDailyMix();
        setDataToArtists(genreId);
        getGenreSongs(genre);
        binding.seeAllGenreSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("genre", genre);
                System.out.println("check genre id " + genreId);
                GenreWiseSongsFragment genreWiseSongsFragment = new GenreWiseSongsFragment();
                genreWiseSongsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_genrewise_songs, genreWiseSongsFragment, "Songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllGenreArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("genreId", genreId);
                System.out.println("check genre id " + genreId);
                GenreWiseArtistListFragment genreWiseArtistListFragment = new GenreWiseArtistListFragment();
                genreWiseArtistListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_artists_genre, genreWiseArtistListFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllGenrePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("genreId", genreId);
                System.out.println("check genre id " + genreId);
                GenreWisePlaylistfragment genreWisePlaylistfragment = new GenreWisePlaylistfragment();
                genreWisePlaylistfragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_playlist_genre, genreWisePlaylistfragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });



        return binding.getRoot();
    }


//    private void setupDailyMix() {
//        binding.genrePlaylistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),
//                LinearLayoutManager.HORIZONTAL, false));
//        binding.genrePlaylistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
//        binding.genrePlaylistRecyclerview.setNestedScrollingEnabled(false);
//
//
//    }

    private void setupPopularSongs() {
        binding.genreSongsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.genreSongsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.genreSongsRecycleview.setHasFixedSize(true);
        binding.genreSongsRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupArtists() {
        binding.genreArtistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.genreArtistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.genreArtistRecyclerview.setHasFixedSize(true);
        binding.genreArtistRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setupPlaylists() {
        binding.genrePlaylistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.genrePlaylistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.genrePlaylistRecyclerview.setHasFixedSize(true);
        binding.genrePlaylistRecyclerview.setNestedScrollingEnabled(false);

    }


//


    private void setDataToArtists(int genreId) {

        System.out.println("Check artist 123123");

        tvManager.getGenreArtists(genreId, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                System.out.println("Check artist  " + result.size());
                if (result.size() == 0) {
                    binding.genreArtistLayout.setVisibility(View.GONE);

                } else {
                    binding.genreArtistLayout.setVisibility(View.VISIBLE);
                    genreArtistsArrayList = result;
                    artistsVerticalAdapter = new GenreArtistVerticalAdapter(getActivity(), genreArtistsArrayList,GenreSongsFragment.this);
                    binding.genreArtistRecyclerview.setAdapter(artistsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToPlaylists(int genreId) {

        System.out.println("Check artist " + genreId);

        tvManager.getByGenrePlaylists(genreId, new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                System.out.println("Check playlist  " + result.size());
                if (result.size() == 0) {
                    binding.genrePlaylistLayout.setVisibility(View.GONE);

                } else {
                    binding.genrePlaylistLayout.setVisibility(View.VISIBLE);
                    genrePlaylistArrayList = result;
                    playlistVerticalAdapter = new PlaylistVerticalAdapter(getActivity(), genrePlaylistArrayList,GenreSongsFragment.this);
                    binding.genrePlaylistRecyclerview.setAdapter(playlistVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }



    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

    }



//    @Override
//    public void onDailyMixPlayAction(final DailyMix dailyMix, final int position, final List<DailyMix> dailyMixes) {
//        tvManager.getSongsOfDailymix(dailyMix.getId(), new APIListener<List<Song>>() {
//            @Override
//            public void onSuccess(List<Song> songs, List<Object> params) {
//                updateDatar(position, dailyMix);
//
//                if (songs.size() == 0) {
//                    Utils.Dialog.showOKDialog(getActivity(), "No Songs Available");
//                } else {
//                    AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//                    hhh.dailymixSongs(songs);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                updateDatar(position, dailyMix);
//                Utils.Error.onServiceCallFail(getActivity(), t);
//            }
//        });
//    }

//    private void updateDatar(int position, DailyMix dailyMix) {
//        DailyMix userData = new DailyMix();
//
//        userData.setName(dailyMix.getName());
//        userData.setId(dailyMix.getId());
//        userData.setOpened(false);
//        userData.setDate(dailyMix.getDate());
//        userData.setIndex(dailyMix.getIndex());
//
//        dailyMixAdapter.UpdateData(position, userData);
//    }

    private void subscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        RelativeLayout createTextView = (RelativeLayout) alertDialog.findViewById(R.id.subscribe_now_layout);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.go_back_textview);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intentPackages = new Intent(getActivity(), AudioPaymentActivity.class);
                startActivity(intentPackages);


            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }
    private void getGenreSongs(final String genre) {
        tvManager.getGenreSongs(0, 8, genre, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                System.out.println("djdjdjdjddjd  111 " + songs.size());
                if (songs.size() == 0) {
                    binding.genreSongsLayout.setVisibility(View.GONE);

                } else {
                    binding.genreSongsLayout.setVisibility(View.VISIBLE);
                    genreSongsArrayList = songs;
                    System.out.println("djdjdjdjddjd 222 " + genreSongsArrayList.size());
                    genreSongsVerticalAdapter = new GenreSongsVerticalAdapter(getActivity(), genreSongsArrayList,GenreSongsFragment.this);
                    binding.genreSongsRecycleview.setAdapter(genreSongsVerticalAdapter);

                }

                binding.aviProgress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public void onGenreSongsPlayAction(Song song, int position, List<Song> songs) {
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);
    }

    @Override
    public void onArtistsPlayAction(Artist artist, int position, List<Artist> artistList) {
        Bundle bundle=new Bundle();
        bundle.putInt("artistID", artist.getId());
        System.out.println("ssdffjfnjffjfjfj" + artist.getId());
        bundle.putString("artistName", artist.getName());
        System.out.println("ssdffjfnjffjfjfj" + artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());
        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container_artists_genre_detail, artistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();
        System.out.println("ssdffjfnjffjfjfj  ");
    }


    @Override
    public void onPlaylistPlayAction(PlayList playList, int position, List<PlayList> playLists) {
        Bundle bundle=new Bundle();
        bundle.putInt("playlistID", playList.getId());
        System.out.println("ssdffjfnjffjfjfj" + playList.getId());
        bundle.putString("playlistName", playList.getName());
        bundle.putString("songCount", playList.getSongCount());
        System.out.println("song count " + playList.getSongCount());
        bundle.putString("playlistImage", playList.getImage());
        bundle.putString("playlistYear", playList.getDate());
        PlaylistDetailFragment playlistDetailFragment = new PlaylistDetailFragment();
        playlistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.genre_playlist_detail_container, playlistDetailFragment, "playlistID")
                .addToBackStack(null)
                .commit();
    }
}

