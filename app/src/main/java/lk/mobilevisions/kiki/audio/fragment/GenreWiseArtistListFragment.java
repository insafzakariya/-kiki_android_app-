package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

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
import lk.mobilevisions.kiki.audio.adapter.GenreArtistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.databinding.FragmentArtistListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GenreWiseArtistListFragment extends Fragment implements GenreArtistListAdapter.OnArtistItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentArtistListBinding binding;
    GenreArtistListAdapter mAdapter;
    List<Artist> artistArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;

    public GenreWiseArtistListFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_list, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);


           int genreId = getArguments().getInt("genreId");

        System.out.println("Check artist 1414141414 " + genreId);

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.artistListRecycle.setLayoutManager(channelsLayoutManager);
        mAdapter = new GenreArtistListAdapter(getActivity(), artistArrayList, GenreWiseArtistListFragment.this);
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setItemViewCacheSize(50);
        binding.artistListRecycle.setDrawingCacheEnabled(true);
        binding.artistListRecycle.setAdapter(mAdapter);
        getGenreWiseArtists(genreId);

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }


    private void getGenreWiseArtists(int genreId) {


        tvManager.getGenreArtists(genreId, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                artistArrayList = result;
                binding.artistListRecycle.setAdapter(new GenreArtistListAdapter(getContext(),
                        artistArrayList, GenreWiseArtistListFragment.this));
                if (result.size() <= 0) {
                    binding.artistListRecycle.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.artistListRecycle.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }

    private void addArtistToLibrary(int artistID){

        ArrayList <Integer> artistId = new ArrayList<>();
        artistId.add(artistID);
        tvManager.addDataToLibraryHash("A",artistId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
                mAdapter.setEnbaled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                mAdapter.setEnbaled(true);
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    @Override
    public void onArtistItemClick(Artist artist, int position, List<Artist> artistList) {

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
                .replace(R.id.genre_artist_list_to_detail_container, artistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onAddArtistItemClick(Artist artist) {
        binding.aviProgress.setVisibility(View.VISIBLE);
        int artistID = artist.getId();
        addArtistToLibrary(artistID);
        mAdapter.setEnbaled(false);
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