package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

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
import lk.mobilevisions.kiki.audio.adapter.LibraryArtistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.databinding.FragmentArtistListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryArtistListFragment extends Fragment implements LibraryArtistListAdapter.OnArtistItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentArtistListBinding binding;

    LibraryArtistListAdapter mAdapter;
    List<Artist> artistArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;

    public LibraryArtistListFragment() {
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


        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.artistListRecycle.setLayoutManager(channelsLayoutManager);
        mAdapter = new LibraryArtistListAdapter(getActivity(), artistArrayList, LibraryArtistListFragment.this);
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setItemViewCacheSize(50);
        binding.artistListRecycle.setDrawingCacheEnabled(true);
        binding.artistListRecycle.setAdapter(mAdapter);
        getYouMightLikeSongs();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }


    private void getYouMightLikeSongs() {


        tvManager.getAllArtists(0,200, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                artistArrayList = result;
                binding.artistListRecycle.setAdapter(new LibraryArtistListAdapter(getContext(),
                        artistArrayList, LibraryArtistListFragment.this));
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


    @Override
    public void onArtistItemClick(Artist artist, int position, List<Artist> artistList) {

        Bundle bundle = new Bundle();
        bundle.putInt("artistID", artist.getId());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());

        LibraryArtistDetailFragment libraryArtistDetailFragment = new LibraryArtistDetailFragment();
        libraryArtistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.library_artist_list_to_detail_container, libraryArtistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();

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