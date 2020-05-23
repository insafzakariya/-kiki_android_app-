package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.BrowseAllSongsAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.databinding.FragmentSeeAllGenreBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class SeeAllGenreFragment extends Fragment implements BrowseAllSongsAdapter.OnBrowseAllItemClickListener {
    @Inject
    TvManager tvManager;

    FragmentSeeAllGenreBinding binding;

    BrowseAllSongsAdapter mAdapter;
    List<Genre> browseGenreList = new ArrayList<>();

    private Animation animShow, animHide;
    GridLayoutManager channelsLayoutManager;

    Context context;
    private ArtistsVerticalAdapter.OnArtistsItemActionListener artistsItemActionListener;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_see_all_genre, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);


        channelsLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.seeAllGenreRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new BrowseAllSongsAdapter(getActivity(), browseGenreList, SeeAllGenreFragment.this);
        binding.seeAllGenreRecyclerview.setHasFixedSize(true);
        binding.seeAllGenreRecyclerview.setItemViewCacheSize(50);
        binding.seeAllGenreRecyclerview.setDrawingCacheEnabled(true);
        binding.seeAllGenreRecyclerview.setAdapter(mAdapter);
        getGenreSongs();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }

    private void getGenreSongs() {
        tvManager.getAllAudioGenre(20,new APIListener<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> genres, List<Object> params) {
                System.out.println("checking genres 111111 " + genres.size());
                browseGenreList = genres;
                System.out.println("checking genres 222222 " + browseGenreList.size());
                mAdapter.setList(browseGenreList);
                if (genres.size() <= 0) {
                    binding.seeAllGenreRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {
                    binding.seeAllGenreRecyclerview.setVisibility(View.VISIBLE);
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
        Bundle bundle=new Bundle();
        bundle.putString("genre", genre.getName());
        bundle.putInt("genreId", genre.getId());
        GenreSongsFragment genreSongsFragment = new GenreSongsFragment();
        genreSongsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.see_all_genre_songs_container, genreSongsFragment, "genre")
                .addToBackStack(null)
                .commit();

    }

}