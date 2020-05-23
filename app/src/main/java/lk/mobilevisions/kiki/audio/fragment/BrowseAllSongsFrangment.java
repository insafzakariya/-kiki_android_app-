package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.BrowseAllSongsAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreArtistVerticalAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentBrowseSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class BrowseAllSongsFrangment extends Fragment implements BrowseAllSongsAdapter.OnBrowseAllItemClickListener,GenreArtistVerticalAdapter.OnArtistsItemActionListener {
    @Inject
    TvManager tvManager;
    Artist artist;
    FragmentBrowseSongsBinding binding;

    BrowseAllSongsAdapter mAdapter;
    List<Genre> browseGenreList = new ArrayList<>();
    List<Artist> artistsArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    GridLayoutManager channelsLayoutManager;
    GenreArtistVerticalAdapter artistsVerticalAdapter;
    Context context;
    private ArtistsVerticalAdapter.OnArtistsItemActionListener artistsItemActionListener;
    private int artistID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse_songs, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        setupArtists();
        setDataToArtists();


        channelsLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.genreRecycle.setLayoutManager(channelsLayoutManager);
        mAdapter = new BrowseAllSongsAdapter(getActivity(), browseGenreList, BrowseAllSongsFrangment.this);
        binding.genreRecycle.setHasFixedSize(true);
        binding.genreRecycle.setItemViewCacheSize(50);
        binding.genreRecycle.setDrawingCacheEnabled(true);
        binding.genreRecycle.setAdapter(mAdapter);
        getGenreSongs();

//        binding.genreWise.setSelected(true);
//        binding.artistWise.setSelected(true);

        binding.seeAllArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenreArtistListFragment genreArtistListFragment = new GenreArtistListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.genre_artist_container, genreArtistListFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeeAllGenreFragment seeAllGenreFragment = new SeeAllGenreFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.browse_genre_container, seeAllGenreFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });


        return binding.getRoot();

    }

    private void setupArtists() {
        binding.artistsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.artistsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.artistsRecycleview.setHasFixedSize(true);
        binding.artistsRecycleview.setNestedScrollingEnabled(false);

    }

    private void setDataToArtists() {

        System.out.println("Check artist 123123");

        tvManager.getAllArtists(0,10,new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                System.out.println("Check artist  " + result.size());
                if (result.size() == 0) {
                    binding.artistLayout.setVisibility(View.GONE);

                } else {
                    binding.artistLayout.setVisibility(View.VISIBLE);
                    artistsArrayList = result;
                    artistsVerticalAdapter = new GenreArtistVerticalAdapter(getActivity(), artistsArrayList,BrowseAllSongsFrangment.this);
                    binding.artistsRecycleview.setAdapter(artistsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }






    private void getGenreSongs() {
        tvManager.getAllAudioGenre(9,new APIListener<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> genres, List<Object> params) {
                System.out.println("checking genres 111111 " + genres.size());
                browseGenreList = genres;
                System.out.println("checking genres 222222 " + browseGenreList.size());
                mAdapter.setList(browseGenreList);
                if (genres.size() <= 0) {
                    binding.genreRecycle.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {
                    binding.genreRecycle.setVisibility(View.VISIBLE);
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
    public void OnBrowseAllItemClick(Genre genre, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("genre", genre.getName());
        bundle.putInt("genreId", genre.getId());
        GenreSongsFragment genreSongsFragment = new GenreSongsFragment();
        genreSongsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.genre_songs_container, genreSongsFragment, "genre")
                .addToBackStack(null)
                .commit();

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
        System.out.println("sdfsdfvsdv 1111 " + artist.getSongsCount());

        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.genre_artist_browse_container, artistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();
    }
}