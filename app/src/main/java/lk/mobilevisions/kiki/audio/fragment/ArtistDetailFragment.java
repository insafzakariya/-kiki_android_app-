package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.ybq.endless.Endless;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.DailyMixAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PopularSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.model.PlaylistModel;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentArtistDetailBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ArtistDetailFragment extends Fragment implements GenreSongsVerticalAdapter.OnGenreSongsItemActionListener  {

    @Inject
    TvManager tvManager;

    FragmentArtistDetailBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    private Endless endless;
    List<DailyMix> genrePlaylistArrayList = new ArrayList<>();
    List<Song> genreSongsArrayList = new ArrayList<>();
    @Inject
    SubscriptionsManager subscriptionsManager;
    GenreSongsVerticalAdapter genreSongsVerticalAdapter;


    private int lastRandomNumber;
    int artistID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_detail, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        artistID = getArguments().getInt("artistID");
        String artistName = getArguments().getString("artistName");
        String artistImage = getArguments().getString("artistImage");
        String artistSongCount = getArguments().getString("songCount");

        binding.soungCount.setText(artistSongCount + " Songs");
        System.out.println("sdfsdfvsdv 2222 " + artistSongCount);

        setupArtistSongs();
        setDataToArtistSongs(artistID);


        binding.seeAllArtistSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("artistID", artistID);
                ArtistSongsListFragment artistSongsListFragment = new ArtistSongsListFragment();
                artistSongsListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.artist_container_song_to_list, artistSongsListFragment, "popular songs")
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

        binding.addArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtistToLibrary(artistID);
            }
        });


        binding.artistDetailName.setText(artistName);

        if (artistImage != null) {
            try {
                Picasso.with(getActivity()).load(URLDecoder.decode(artistImage, "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(binding.artistImageMain);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }



        return binding.getRoot();
    }


    private void setupArtistSongs() {
        binding.artistDetailSongsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.artistDetailSongsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.artistDetailSongsRecycleview.setHasFixedSize(true);
        binding.artistDetailSongsRecycleview.setNestedScrollingEnabled(false);

    }


    private void setDataToArtistSongs(int artistID) {

        tvManager.getArtistSongs(artistID, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.artistDetailSongsLayout.setVisibility(View.GONE);

                } else {
                    binding.artistDetailSongsLayout.setVisibility(View.VISIBLE);
                    genreSongsArrayList = result;
                    genreSongsVerticalAdapter = new GenreSongsVerticalAdapter(getActivity(), genreSongsArrayList,ArtistDetailFragment.this);
                    binding.artistDetailSongsRecycleview.setAdapter(genreSongsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {

                binding.artistDetailSongsLayout.setVisibility(View.GONE);
            }
        });

    }



    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

    }



    @Override
    public void onGenreSongsPlayAction(Song song, int position, List<Song> songs) {
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.genreSongFragmentSongClickedEvent(song, position, songs);
    }

    private void addArtistToLibrary(int artistID){

        ArrayList <Integer> artistId = new ArrayList<>();
        artistId.add(artistID);
        tvManager.addDataToLibraryHash("A",artistId, new APIListener<Void>() {
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


}