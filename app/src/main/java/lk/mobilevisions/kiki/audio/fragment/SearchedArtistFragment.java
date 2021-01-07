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
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentArtistListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchedArtistFragment extends Fragment implements ArtistListAdapter.OnArtistListItemActionListener {
    @Inject
    TvManager tvManager;

    FragmentArtistListBinding binding;
    List<Artist> artistsArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    ArtistListAdapter artistListAdapter;
    LinearLayoutManager channelsLayoutManager;

    public SearchedArtistFragment() {
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

        String searchKey = getArguments().getString("searchKey");

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.artistListRecycle.setLayoutManager(channelsLayoutManager);
        artistListAdapter = new ArtistListAdapter(getActivity(), artistsArrayList,SearchedArtistFragment.this);
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setItemViewCacheSize(50);
        binding.artistListRecycle.setDrawingCacheEnabled(true);
        binding.artistListRecycle.setAdapter(artistListAdapter);

        setupArtistList();
        setDatatoArtistList(searchKey);

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


    private void setDatatoArtistList(String searchKey) {

        tvManager.getSearchArtistbyType(searchKey,0,50, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.artistListRecycle.setVisibility(View.GONE);

                } else {
                    binding.artistListRecycle.setVisibility(View.VISIBLE);
                    artistsArrayList = result;
                    artistListAdapter = new ArtistListAdapter(getActivity(), artistsArrayList,SearchedArtistFragment.this);
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

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
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
                .replace(R.id.library_artist_searchToDetail, artistDetailFragment, "SearchedArtistDetail")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onAddArtistItemClick(Artist artist) {

        int artistID = artist.getId();
        addArtistToLibrary(artistID);
    }
}
