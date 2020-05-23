package lk.mobilevisions.kiki.audio.fragment;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;
import lk.mobilevisions.kiki.audio.adapter.RecentlyPlayedAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;

import lk.mobilevisions.kiki.databinding.FragmentRecentlyPlayedBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class RecentlyPlayedFragment extends Fragment implements RecentlyPlayedAdapter.OnRecentlyPlayedItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentRecentlyPlayedBinding binding;

    RecentlyPlayedAdapter mAdapter;
    List<Song> recentlyPlayedList = new ArrayList<>();
    private Animation animShow, animHide;
    GridLayoutManager channelsLayoutManager;

    public RecentlyPlayedFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recently_played, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

//        AudioDashboardActivity audioDashboardActivity = (AudioDashboardActivity) getActivity();
//        audioDashboardActivity.changeToolbarName("Recently Played");


        channelsLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.recenlyPlayedRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new RecentlyPlayedAdapter(getActivity(), recentlyPlayedList, RecentlyPlayedFragment.this);
        binding.recenlyPlayedRecyclerview.setHasFixedSize(true);
        binding.recenlyPlayedRecyclerview.setItemViewCacheSize(50);
        binding.recenlyPlayedRecyclerview.setDrawingCacheEnabled(true);
        binding.recenlyPlayedRecyclerview.setAdapter(mAdapter);
        getRecentlyPlayedSongs();

        return binding.getRoot();
    }


    private void getRecentlyPlayedSongs() {


        tvManager.getAudioRecentlyPlayed(new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                System.out.println("dbskfbksfb " + result.size());
                recentlyPlayedList = result;
                binding.recenlyPlayedRecyclerview.setAdapter(new RecentlyPlayedAdapter(getContext(),
                        recentlyPlayedList, RecentlyPlayedFragment.this));
                if (result.size() <= 0) {
                    binding.recenlyPlayedRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.recenlyPlayedRecyclerview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }


    @Override
    public void onRecentlyPlayedItemClick(View view, Song song, int position) {
        Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
        i.putExtra("type", "song");
        i.putExtra("songId", song.getId());
        i.putExtra("songName", song.getName());
        getActivity().startActivity(i);
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
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}