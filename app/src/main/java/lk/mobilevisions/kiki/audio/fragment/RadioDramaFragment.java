package lk.mobilevisions.kiki.audio.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.LatestPlaylistAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.databinding.FragmentLatestPlaylistBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RadioDramaFragment extends Fragment implements LatestPlaylistAdapter.OnLatestPlaylistItemClickListener {
    @Inject
    TvManager tvManager;

    FragmentLatestPlaylistBinding binding;

    LatestPlaylistAdapter mAdapter;
    List<PlayList> radioDramaArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;
    private int lastRandomNumber;

    public RadioDramaFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_latest_playlist, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        binding.latestPlaylistText.setText(getResources().getString(R.string.radio_drama));

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.latestPlaylistRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new LatestPlaylistAdapter(getActivity(), radioDramaArrayList, RadioDramaFragment.this);
        binding.latestPlaylistRecyclerview.setHasFixedSize(true);
        binding.latestPlaylistRecyclerview.setItemViewCacheSize(50);
        binding.latestPlaylistRecyclerview.setDrawingCacheEnabled(true);
        binding.latestPlaylistRecyclerview.setAdapter(mAdapter);
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

        tvManager.getRadioDramas(0,250,new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                radioDramaArrayList = result;
                binding.latestPlaylistRecyclerview.setAdapter(new LatestPlaylistAdapter(getContext(),
                        radioDramaArrayList, RadioDramaFragment.this));
                if (result.size() <= 0) {
                    binding.latestPlaylistRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.latestPlaylistRecyclerview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }

    private void addPlaylistToLibrary(int playlistID){

        ArrayList<Integer> playlistId = new ArrayList<>();
        playlistId.add(playlistID);
        tvManager.addDataToLibraryHash("P",playlistId, new APIListener<Void>() {
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


    private int generateRandomIndex(int size) {
        while (true) {
            Random random = new Random();
            int randomNumber = random.nextInt(3);
            if (randomNumber != lastRandomNumber) {
                lastRandomNumber = randomNumber;
                return randomNumber;
            }
        }
    }

    @Override
    public void onLatestPlaylistItemClick(PlayList song, int position, List<PlayList> songs) {

        Bundle bundle=new Bundle();
        bundle.putInt("playlistID", song.getId());
        System.out.println("ssdffjfnjffjfjfj" + song.getId());
        bundle.putString("playlistName", song.getName());
        bundle.putString("songCount", song.getSongCount());
        System.out.println("song count " + song.getSongCount());
        bundle.putString("playlistImage", song.getImage());
        bundle.putString("playlistYear", song.getDate());
        PlaylistDetailFragment playlistDetailFragment = new PlaylistDetailFragment();
        playlistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.latest_playlist_detail_container, playlistDetailFragment, "Home Playlist Detail")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onAddPlaylistItemClick(PlayList playList) {
        int playlistID = playList.getId();
        addPlaylistToLibrary(playlistID);
    }


//    @Override
//    public void onLatestPlaylistItemClick(DailyMix song, int position, List<DailyMix> songs) {
//        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//        hhh.dailymixSongs(songs);
//
//    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else  {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}