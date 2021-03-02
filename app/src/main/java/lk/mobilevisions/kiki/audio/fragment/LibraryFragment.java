package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.endless.Endless;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.DailyMixAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreArtistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PopularSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.event.UserNavigateBackEvent;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentLibraryBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class LibraryFragment extends Fragment implements PlaylistAdapter.OnUserPlaylistItemActionListener, PopularSongsVerticalAdapter.OnPopularSongsItemActionListener, PlaylistVerticalAdapter.OnPlaylistItemActionListener, GenreArtistVerticalAdapter.OnArtistsItemActionListener {

    @Inject
    TvManager tvManager;

    FragmentLibraryBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    private Endless endless;
    List<PlayList> playlistArrayList = new ArrayList<>();
    List<PlayList> kikiPlaylistArrayList = new ArrayList<>();
    List<Song> genreSongsArrayList = new ArrayList<>();
    List<Artist> genreArtistsArrayList = new ArrayList<>();

    @Inject
    SubscriptionsManager subscriptionsManager;
    PlaylistAdapter playListAdapter;
    PopularSongsVerticalAdapter popularSongsVerticalAdapter;
    GenreArtistVerticalAdapter genreArtistVerticalAdapter;
    PlaylistVerticalAdapter playlistVerticalAdapter;

    private PlaylistAdapter playListAdapterActionListener;
    private PopularSongsVerticalAdapter.OnPopularSongsItemActionListener popularSongsItemActionListener;
    private ArtistsVerticalAdapter.OnArtistsItemActionListener artistsItemActionListener;

    private int lastRandomNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false);

        ((Application) getActivity().getApplication()).getInjector().inject(this);

        setupPlaylist();
        setupPopularSongs();
        setupArtists();
        setupKikiPlaylist();
        setDataToPlaylist();
        setDataLibrarySongs();
        setDataLibraryArtists();
        setDataToKikiPlaylist();

        try {
            Application.BUS.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.seeAllLibrarySongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryHomeSongListFragment libraryHomeSongListFragment = new LibraryHomeSongListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_library_seeall_songs, libraryHomeSongListFragment, "popular songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllLibraryArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryHomeArtistListFragment libraryHomeArtistListFragment = new LibraryHomeArtistListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_library_seeall_artist, libraryHomeArtistListFragment, "latest songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllLibraryPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryHomePlaylistListFragment libraryHomePlaylistListFragment = new LibraryHomePlaylistListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_library_seeall_playlist, libraryHomePlaylistListFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllLibraryKikiPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryKikiPLaylistListFragment kikiPLaylistListFragment = new LibraryKikiPLaylistListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_library_seeall_kiki, kikiPLaylistListFragment, "Kiki")
                        .addToBackStack(null)
                        .commit();
            }
        });


        return binding.getRoot();
    }


    private void setupPlaylist() {
        binding.libraryPlaylistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.libraryPlaylistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.libraryPlaylistRecyclerview.setNestedScrollingEnabled(false);


    }

    private void setupKikiPlaylist() {
        binding.libraryKikiPlaylistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.libraryKikiPlaylistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.libraryKikiPlaylistRecyclerview.setNestedScrollingEnabled(false);


    }

    private void setupPopularSongs() {
        binding.librarySongsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.librarySongsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.librarySongsRecycleview.setHasFixedSize(true);
        binding.librarySongsRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupArtists() {
        binding.libraryArtistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.libraryArtistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.libraryArtistRecyclerview.setHasFixedSize(true);
        binding.libraryArtistRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setDataToPlaylist() {
        playlistArrayList.clear();

        tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {
                PlayList playList = new PlayList();
                playList.setId(0);
                playList.setArtistName("");
                playList.setDate("");
                playList.setImage("");
                playList.setName("");
                playList.setSongCount("");
                playList.setSongsList(null);
                playList.setDefault(true);
                playlistArrayList.add(playList);
                playlistArrayList.addAll(playLists);
                playListAdapter = new PlaylistAdapter(getActivity(), playlistArrayList, LibraryFragment.this);
                binding.libraryPlaylistRecyclerview.setAdapter(playListAdapter);
                playListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    private void setDataToKikiPlaylist() {
        tvManager.getLibraryPlaylists(10, 1, new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                System.out.println("Check playlist  " + result.size());
                if (result.size() == 0) {
                    binding.libraryKikiPlaylistLayout.setVisibility(View.GONE);

                } else {
                    binding.libraryKikiPlaylistLayout.setVisibility(View.VISIBLE);
                    kikiPlaylistArrayList = result;
                    playlistVerticalAdapter = new PlaylistVerticalAdapter(getActivity(), kikiPlaylistArrayList, LibraryFragment.this);
                    binding.libraryKikiPlaylistRecyclerview.setAdapter(playlistVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });


    }


    private void setDataLibrarySongs() {

        tvManager.getLibrarySongs(10, 1, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.librarySongsLayout.setVisibility(View.GONE);

                } else {
                    binding.librarySongsLayout.setVisibility(View.VISIBLE);
                    genreSongsArrayList = result;
                    popularSongsVerticalAdapter = new PopularSongsVerticalAdapter(getActivity(), genreSongsArrayList);
                    binding.librarySongsRecycleview.setAdapter(popularSongsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataLibraryArtists() {

        tvManager.getLibraryArtists(10, 1, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.libraryArtistLayout.setVisibility(View.GONE);

                } else {
                    binding.libraryArtistLayout.setVisibility(View.VISIBLE);
                    genreArtistsArrayList = result;
                    genreArtistVerticalAdapter = new GenreArtistVerticalAdapter(getActivity(), genreArtistsArrayList, LibraryFragment.this);
                    binding.libraryArtistRecyclerview.setAdapter(genreArtistVerticalAdapter);
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


    @Override
    public void onPopularSongsPlayAction(Song song, int position, List<Song> songs) {

    }

    @Override
    public void onUserPlaylistPlayAction(PlayList playList, int position, List<PlayList> playLists) {

        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("editPlaylist", false);
            PlaylistCreationFragment playlistCreationFragment = new PlaylistCreationFragment();
            playlistCreationFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_library_playlist_creation, playlistCreationFragment, "Library to Playlist")
                    .addToBackStack(null)
                    .commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("playlistID", playList.getId());
            bundle.putString("playlistName", playList.getName());
            bundle.putString("songCount", playList.getSongCount());
            bundle.putString("playlistImage", playList.getImage());
            bundle.putString("playlistYear", playList.getDate());
            LibraryHomePlaylistDetailFragment libraryHomePlaylistDetailFragment = new LibraryHomePlaylistDetailFragment();
            libraryHomePlaylistDetailFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_library_playlistTodetail, libraryHomePlaylistDetailFragment, "Home to PlaylistDetail")
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void onPlaylistPlayAction(PlayList playList, int position, List<PlayList> playLists) {

        Bundle bundle = new Bundle();
        bundle.putInt("playlistID", playList.getId());
        bundle.putString("playlistName", playList.getName());
        bundle.putString("songCount", playList.getSongCount());
        bundle.putString("playlistImage", playList.getImage());
        bundle.putString("playlistYear", playList.getDate());
        LibraryKikiPlaylistDetailFragment kikiPlaylistDetailFragment = new LibraryKikiPlaylistDetailFragment();
        kikiPlaylistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container_kiki_playlistTodetail, kikiPlaylistDetailFragment, "Home to PlaylistDetail")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onArtistsPlayAction(Artist artist, int position, List<Artist> artistList) {
        Bundle bundle = new Bundle();
        bundle.putInt("artistID", artist.getId());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());

        LibraryHomeArtistDetailFragment artistDetailFragment = new LibraryHomeArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container_library_home_artist_detail, artistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();
    }

    @Subscribe
    public void onUserNavigateBack(UserNavigateBackEvent event) {

        setDataToPlaylist();
        setDataLibrarySongs();
        setDataLibraryArtists();
        setDataToKikiPlaylist();

    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

}
