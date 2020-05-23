package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.endless.Endless;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import lk.mobilevisions.kiki.audio.adapter.LibrarySongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentArtistDetailBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryArtistDetailFragment extends Fragment implements LibrarySongsVerticalAdapter.OnGenreSongsItemActionListener {

    @Inject
    TvManager tvManager;

    FragmentArtistDetailBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    private Endless endless;
    List<Song> genreSongsArrayList = new ArrayList<>();


    @Inject
    SubscriptionsManager subscriptionsManager;
    LibrarySongsVerticalAdapter librarySongsVerticalAdapter;


    private int lastRandomNumber;
    private int artistID;

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

        int artistID = getArguments().getInt("artistID");
        String artistName = getArguments().getString("artistName");
        String artistImage = getArguments().getString("artistImage");
        String artistSongCount = getArguments().getString("songCount");

        binding.soungCount.setText(artistSongCount + " Songs");

        setupArtistSongs();
        setDataToArtistSongs(artistID);


        binding.seeAllArtistSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("artistID", artistID);
                LibraryArtistSongsList libraryArtistSongsList = new LibraryArtistSongsList();
                libraryArtistSongsList.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.artist_container_song_to_list, libraryArtistSongsList, "artist songs")
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
                    librarySongsVerticalAdapter = new LibrarySongsVerticalAdapter(getActivity(), genreSongsArrayList,LibraryArtistDetailFragment.this);
                    binding.artistDetailSongsRecycleview.setAdapter(librarySongsVerticalAdapter);
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

        if(!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())){
            Application.getInstance().addSongToPlayList(song.getId());

        }else{
            Application.getInstance().removeSongFromPlayList(song.getId());
        }
        addSongsToPlaylist(song.getId());
        librarySongsVerticalAdapter.notifyDataSetChanged();

    }

    private void addSongsToPlaylist(int songId) {
        String sessionId = null;
        if(Application.getInstance().getSessionId()!=null){
            sessionId = Application.getInstance().getSessionId();
        }else{
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
        }
        System.out.println("sessionnnn 22222 " + sessionId);
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