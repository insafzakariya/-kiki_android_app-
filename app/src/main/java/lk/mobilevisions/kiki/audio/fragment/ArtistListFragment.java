package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentArtistListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class ArtistListFragment extends Fragment implements ArtistListAdapter.OnArtistListItemActionListener {
    @Inject
    TvManager tvManager;

    FragmentArtistListBinding binding;
    List<Artist> artistsArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    ArtistListAdapter artistListAdapter;
    LinearLayoutManager channelsLayoutManager;

    public ArtistListFragment() {
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
        artistListAdapter = new ArtistListAdapter(getActivity(), artistsArrayList,ArtistListFragment.this);
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setItemViewCacheSize(50);
        binding.artistListRecycle.setDrawingCacheEnabled(true);
        binding.artistListRecycle.setAdapter(artistListAdapter);

        setupArtistList();
        setDatatoArtistList();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }




    private void setupArtistList() {
        binding.artistListRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.artistListRecycle.addItemDecoration(new SpacesItemDecoration(15));
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setNestedScrollingEnabled(false);

    }


    private void setDatatoArtistList() {

        tvManager.getPopularArtists( new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.artistListRecycle.setVisibility(View.GONE);

                } else {
                    binding.artistListRecycle.setVisibility(View.VISIBLE);
                    artistsArrayList = result;
                    artistListAdapter = new ArtistListAdapter(getActivity(), artistsArrayList,ArtistListFragment.this);
                    binding.artistListRecycle.setAdapter(artistListAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });
    }

    private void addArtistToLibrary(int artistID){

        ArrayList <Integer> artistId = new ArrayList<>();
        artistId.add(artistID);
        tvManager.addDataToLibraryHash("A",artistId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getActivity(), "Added to library.", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }


    @Override
    public void onArtistListItemClick(Artist artist, int position, List<Artist> artistList) {

        Bundle bundle=new Bundle();
        bundle.putInt("artistID", artist.getId());
        System.out.println("ssdffjfnjffjfjfj" + artist.getId());
        bundle.putString("artistName", artist.getName());
        System.out.println("ssdffjfnjffjfjfj" + artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());
        System.out.println("songCount " + artist.getSongsCount());

        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.home_artist_list_to_detail_container, artistDetailFragment, "Home Artist Detail")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onAddArtistItemClick(Artist artist) {

        int artistID = artist.getId();
        addArtistToLibrary(artistID);
    }


//    @Override
//    public void OnBrowseAllItemClick(Genre genre, int position, List<Genre> genres) {
//        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//        hhh.browseAllClickedEvent(genre, position, genres);
//    }

//    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
////        @Override
////        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
////            int position = parent.getChildAdapterPosition(view); // item position
////            int column = position % spanCount; // item column
////
////            if (includeEdge) {
////                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
////                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
////
////                if (position < spanCount) { // top edge
////                    outRect.top = spacing;
////                }
////                outRect.bottom = spacing; // item bottom
////            } else  {
////                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
////                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
////                if (position >= spanCount) {
////                    outRect.top = spacing; // item top
////                }
////            }
////        }
//    }


}