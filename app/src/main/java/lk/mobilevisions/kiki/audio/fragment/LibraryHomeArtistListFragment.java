package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
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
import lk.mobilevisions.kiki.audio.adapter.LibraryArtistListAdapter;
import lk.mobilevisions.kiki.audio.adapter.LibraryHomeArtistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentArtistListBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LibraryHomeArtistListFragment extends Fragment implements LibraryHomeArtistListAdapter.OnArtistItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentArtistListBinding binding;
    LibraryHomeArtistListAdapter mAdapter;
    List<Artist> artistArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;

    public LibraryHomeArtistListFragment() {
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


//        int genreId = getArguments().getInt("genreId");

//        System.out.println("Check artist 1414141414 " + genreId);

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.artistListRecycle.setLayoutManager(channelsLayoutManager);
        mAdapter = new LibraryHomeArtistListAdapter(getActivity(), artistArrayList, LibraryHomeArtistListFragment.this);
        binding.artistListRecycle.setHasFixedSize(true);
        binding.artistListRecycle.setItemViewCacheSize(50);
        binding.artistListRecycle.setDrawingCacheEnabled(true);
        binding.artistListRecycle.setAdapter(mAdapter);
        getLibraryArtists();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }


    private void getLibraryArtists() {


        tvManager.getLibraryArtists(100,1, new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                artistArrayList = result;
                binding.artistListRecycle.setAdapter(new LibraryHomeArtistListAdapter(getContext(),
                        artistArrayList, LibraryHomeArtistListFragment.this));
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

    private void removeFromLibraryDialog(Artist artist) {

        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_remove_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();


//        TextView songTitle = (TextView) alertDialog.findViewById(R.id.add_song_title);
//        songTitle.setText(song.getName());
        TextView yesTextView = (TextView) alertDialog.findViewById(R.id.yesTextview);
        TextView noTextView = (TextView) alertDialog.findViewById(R.id.noTextview);


        yesTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int artistId = artist.getId();
                removeDatafromLibrary(artistId);
                alertDialog.dismiss();

            }
        });
        noTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

    }


    private void removeDatafromLibrary(int artistId){
        ArrayList <Integer> removeArtistId = new ArrayList<>();
        removeArtistId.add(artistId);
        System.out.println("sjdkbfsdjkbv " + removeArtistId);
        tvManager.removeDatafromLibrary("A",removeArtistId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
//                librarySongList.remove(removeSongId);
//                mAdapter.notifyDataSetChanged();
                getLibraryArtists();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.removed_from_lib), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
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
        LibraryHomeArtistDetailFragment artistDetailFragment = new LibraryHomeArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.library_artist_list_to_detail, artistDetailFragment, "artistID")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onRemoveArtistItemClick(Artist artist) {
        removeFromLibraryDialog(artist);
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