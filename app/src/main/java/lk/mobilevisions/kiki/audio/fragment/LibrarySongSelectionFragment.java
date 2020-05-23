package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.adapter.LibraryArtistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryPlaylistSongAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryPlaylistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibrarySongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistDetailAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.audio.widgets.PersistentSearchView;
import lk.mobilevisions.kiki.databinding.FragmentGenreSongsBinding;
import lk.mobilevisions.kiki.databinding.FragmentLibrarySongSelectionBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import timber.log.Timber;

public class LibrarySongSelectionFragment extends Fragment implements  LibrarySongsVerticalAdapter.OnGenreSongsItemActionListener, LibraryArtistVerticalAdapter.OnArtistsItemActionListener, LibraryPlaylistVerticalAdapter.OnPlaylistItemActionListener, LibraryPlaylistSongAdapter.OnLibraryPlaylistItemClickListener {

    @Inject
    TvManager tvManager;

    FragmentLibrarySongSelectionBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    LibraryPlaylistSongAdapter searchAdapter;
    List<Song> searchSongsList = new ArrayList<>();
    List<PlayList> genrePlaylistArrayList = new ArrayList<>();
    List<Song> genreSongsArrayList = new ArrayList<>();
    List<Artist> genreArtistsArrayList = new ArrayList<>();
    LinearLayoutManager channelsLayoutManager;

    @Inject
    SubscriptionsManager subscriptionsManager;
    //    DailyMixAdapter dailyMixAdapter;
    LibrarySongsVerticalAdapter genreSongsVerticalAdapter;
    LibraryArtistVerticalAdapter artistsVerticalAdapter;
    LibraryPlaylistVerticalAdapter playlistVerticalAdapter;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_song_selection, container, false);

        ((Application) getActivity().getApplication()).getInjector().inject(this);


        setDataToPlaylists();
        setupPlaylists();
        setupPopularSongs();
        setupArtists();
//        setDataToDailyMix();
        setDataToArtists();
        getGenreSongs();
        setupSearchSongs();
        binding.seeAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LibrarySongListFragment librarySongListFragment = new LibrarySongListFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_song_selection, librarySongListFragment, "Songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
//                bundle.putInt("genreId", genreId);
//                System.out.println("check genre id " + genreId);
                LibraryArtistListFragment libraryArtistListFragment = new LibraryArtistListFragment();
                libraryArtistListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_artist_selection, libraryArtistListFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
//                bundle.putInt("genreId", genreId);
//                System.out.println("check genre id " + genreId);
                LibraryPlaylistListFragment libraryPlaylistListFragment = new LibraryPlaylistListFragment();
                libraryPlaylistListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_playlist_selection, libraryPlaylistListFragment, "Playlists")
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

        binding.confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        binding.searchText.setActivated(true);
        binding.searchText.onActionViewExpanded();
//        binding.searchText.setIconified(false);
        binding.searchText.clearFocus();
        binding.searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSongs(query);
                System.out.println("sdjfnsdjvn " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                binding.searchLayout.setVisibility(View.GONE);
                System.out.println("sdjfnsdjvn 2222 " + newText);
//                searchSongs(newText);
                return false;
            }
        });

        return binding.getRoot();
    }


    private void setupPopularSongs() {
        binding.librarySongsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.librarySongsRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.librarySongsRecyclerview.setHasFixedSize(true);
        binding.librarySongsRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setupArtists() {
        binding.libraryArtistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.libraryArtistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.libraryArtistRecyclerview.setHasFixedSize(true);
        binding.libraryArtistRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setupPlaylists() {
        binding.libraryPlaylistRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.libraryPlaylistRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.libraryPlaylistRecycleview.setHasFixedSize(true);
        binding.libraryPlaylistRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupSearchSongs() {
        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.searchSongsRecyclerview.setLayoutManager(channelsLayoutManager);
        binding.searchSongsRecyclerview.setHasFixedSize(true);
        binding.searchSongsRecyclerview.setItemViewCacheSize(50);
        binding.searchSongsRecyclerview.setDrawingCacheEnabled(true);
        binding.searchSongsRecyclerview.setAdapter(searchAdapter);

    }



    //
private void searchSongs(String text) {
    System.out.println("searchsize 222 " + text);
    binding.aviProgress.setVisibility(View.VISIBLE);
//    binding.noSongsTextView.setVisibility(View.GONE);
    tvManager.getSearchedSongs(0, 100, text, new APIListener<List<Song>>() {
        @Override
        public void onSuccess(List<Song> result, List<Object> params) {
//            searchSongsList.clear();
            if (result.size() == 0) {
                binding.searchLayout.setVisibility(View.GONE);
                System.out.println("searchsize " + result.size());

            } else {
                binding.searchLayout.setVisibility(View.VISIBLE);
                searchSongsList = result;
                System.out.println("searchsize 111 " + searchSongsList.size());
                searchAdapter = new LibraryPlaylistSongAdapter(getActivity(), searchSongsList,LibrarySongSelectionFragment.this);
                binding.searchSongsRecyclerview.setAdapter(searchAdapter);
            }
            binding.aviProgress.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Throwable t) {
            binding.aviProgress.setVisibility(View.GONE);
            System.out.println("dhdhdhdhdhdh 222  " + t.toString());

        }
    });
}


    private void setDataToArtists() {

        System.out.println("Check artist 123123");

        tvManager.getAllArtists(0,10, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                System.out.println("Check artist  " + result.size());
                if (result.size() == 0) {
                    binding.libraryArtistLayout.setVisibility(View.GONE);

                } else {
                    binding.libraryArtistLayout.setVisibility(View.VISIBLE);
                    genreArtistsArrayList = result;
                    artistsVerticalAdapter = new LibraryArtistVerticalAdapter(getActivity(), genreArtistsArrayList,LibrarySongSelectionFragment.this);
                    binding.libraryArtistRecyclerview.setAdapter(artistsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToPlaylists() {

//        System.out.println("Check artist " + genreId);

        tvManager.getDailyMixNew(0,10, new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                System.out.println("Check playlist  " + result.size());
                if (result.size() == 0) {
                    binding.libraryPlaylistLayout.setVisibility(View.GONE);

                } else {
                    binding.libraryPlaylistLayout.setVisibility(View.VISIBLE);
                    genrePlaylistArrayList = result;
                    playlistVerticalAdapter = new LibraryPlaylistVerticalAdapter(getActivity(), genrePlaylistArrayList,LibrarySongSelectionFragment.this);
                    binding.libraryPlaylistRecycleview.setAdapter(playlistVerticalAdapter);
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


    private void getGenreSongs() {
        tvManager.getAllSongs(0, 10,new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                System.out.println("djdjdjdjddjd  111 " + songs.size());
                if (songs.size() == 0) {
                    binding.librarySongsLayout.setVisibility(View.GONE);

                } else {
                    binding.librarySongsLayout.setVisibility(View.VISIBLE);
                    genreSongsArrayList = songs;
                    System.out.println("djdjdjdjddjd 222 " + genreSongsArrayList.size());
                    genreSongsVerticalAdapter = new LibrarySongsVerticalAdapter(getActivity(), genreSongsArrayList,LibrarySongSelectionFragment.this);
                    binding.librarySongsRecyclerview.setAdapter(genreSongsVerticalAdapter);

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

        if(!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
            Application.getInstance().addSongToPlayList(song.getId());

        }else{
            Application.getInstance().removeSongFromPlayList(song.getId());
        }
        addSongsToPlaylist(song.getId());
        genreSongsVerticalAdapter.notifyDataSetChanged();

    }

    @Override
    public void onArtistsPlayAction(Artist artist, int position, List<Artist> artistList) {

        Bundle bundle = new Bundle();
        bundle.putInt("artistID", artist.getId());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());

        LibraryArtistDetailFragment libraryArtistDetailFragment = new LibraryArtistDetailFragment();
        libraryArtistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.library_container_artist_toDetail, libraryArtistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onPlaylistPlayAction(PlayList playList, int position, List<PlayList> playLists) {

        Bundle bundle=new Bundle();
        bundle.putInt("playlistID", playList.getId());
        bundle.putString("playlistName", playList.getName());
        bundle.putString("songCount", playList.getSongCount());
        bundle.putString("playlistImage", playList.getImage());
        bundle.putString("playlistYear", playList.getDate());

        LibraryPlaylistDetailFragment libraryPlaylistDetailFragment = new LibraryPlaylistDetailFragment();
        libraryPlaylistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.library_container_playlist_toDetail, libraryPlaylistDetailFragment, "playlistID")
                .addToBackStack(null)
                .commit();
    }

    private  void addSongsToPlaylist(int id) {
        Application.getInstance().getInjector().inject(this);

        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
        }
        System.out.println("sessionnnn 11111 " + sessionId);
        tvManager.addSongsToTempTable(sessionId, id,"S", new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
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
        searchAdapter.notifyDataSetChanged();
    }
}