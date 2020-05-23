package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.endless.Endless;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.SongsAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.FragmentAudioSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class AudioSongsFragment extends Fragment implements SongsAdapter.OnAudioSongsItemClickListener  {


    @Inject
    TvManager tvManager;
    FragmentAudioSongsBinding binding;
    SongsAdapter mAdapter;
    List<Song> mArrayList = new ArrayList<>();
    public static final String ARG_PAGE = "ARG_PAGE";
    Context context;
    private String genre;
    LinearLayoutManager songsLayoutManager;
    private Endless endless;
    List<Song> mArrayListGenre = new ArrayList<>();

    public static AudioSongsFragment newInstance(String genre) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, genre);
        AudioSongsFragment fragment = new AudioSongsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genre = getArguments().getString(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_songs, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        songsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.songsRecyclerview.setLayoutManager(songsLayoutManager);
        binding.songsRecyclerview.setHasFixedSize(true);
        binding.songsRecyclerview.setItemViewCacheSize(500);
        binding.songsRecyclerview.setDrawingCacheEnabled(true);
        mAdapter = new SongsAdapter(getActivity(), AudioSongsFragment.this);
        changeTab(genre);

        View loadingView = View.inflate(getActivity(), R.layout.layout_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(binding.songsRecyclerview,
                loadingView
        );
        endless.setAdapter(mAdapter);
        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                binding.spinKit.setVisibility(View.VISIBLE);
                if (genre.equals("All Songs")) {
                    getAudioAllSongs(page);
                } else {
                    getGenreSongs(page, genre);
                }

            }
        });

        binding.songsRecyclerview.setAdapter(mAdapter);


        binding.playAllTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (genre.equals("All Songs")) {
                    if (mArrayList.size() > 0) {
                        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                        hhh.allSongsItemClickEvent(mArrayList,genre);
                    }
                } else {
                    if (mArrayListGenre.size() > 0) {
                        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                        hhh.allSongsItemClickEvent(mArrayListGenre,genre);
                    }
                }


            }
        });
        binding.playAllImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (genre.equals("All Songs")) {
                    if (mArrayList.size() > 0) {
                        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                        hhh.allSongsItemClickEvent(mArrayList, genre);
                    }
                } else {
                    if (mArrayListGenre.size() > 0) {
                        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                        hhh.allSongsItemClickEvent(mArrayListGenre, genre);
                    }
                }
            }
        });
        return binding.getRoot();
    }

    private void getAudioAllSongs(final int page) {
        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = mArrayList.size();
        }
        tvManager.getAllSongs(offset, 8, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {

                mArrayList.addAll(songs);
                if (page == 0) {
                    mAdapter.setData(songs);
                } else {
                    mAdapter.addData(songs);
                    endless.loadMoreComplete();
                    binding.spinKit.setVisibility(View.GONE);

                }

                binding.spinKit.setVisibility(View.GONE);
                binding.aviProgress.setVisibility(View.GONE);

                if(mArrayList.size()==0){
                    binding.playAllTextview.setVisibility(View.GONE);
                    binding.playAllImageview.setVisibility(View.GONE);
                    binding.noSongsTextView.setVisibility(View.VISIBLE);
                }else{
                    binding.playAllTextview.setVisibility(View.VISIBLE);
                    binding.playAllImageview.setVisibility(View.VISIBLE);
                    binding.noSongsTextView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                binding.spinKit.setVisibility(View.GONE);
//                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }

    private void getGenreSongs(final int page, final String genre) {
       int arraySize = Utils.SharedPrefUtil.getIntFromSharedPref(getActivity(),
                genre, 0);
        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = arraySize;
        }
        tvManager.getGenreSongs(offset, 8, genre, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                mArrayListGenre.addAll(songs);
                if (page == 0) {
                    mAdapter.setData(songs);
                } else {
                    mAdapter.addData(songs);
                    endless.loadMoreComplete();
                    binding.spinKit.setVisibility(View.GONE);

                }

                binding.spinKit.setVisibility(View.GONE);
                binding.aviProgress.setVisibility(View.GONE);

                if(mArrayListGenre.size()==0){
                    binding.playAllTextview.setVisibility(View.GONE);
                    binding.playAllImageview.setVisibility(View.GONE);
                    binding.noSongsTextView.setVisibility(View.VISIBLE);
                }else{
                    binding.playAllTextview.setVisibility(View.VISIBLE);
                    binding.playAllImageview.setVisibility(View.VISIBLE);
                    binding.noSongsTextView.setVisibility(View.GONE);
                }
                Utils.SharedPrefUtil.saveIntToSharedPref(getActivity(),
                        genre, mArrayListGenre.size());

            }

            @Override
            public void onFailure(Throwable t) {
                binding.spinKit.setVisibility(View.GONE);

            }
        });
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

    }


    private void changeTab(String page) {
        if (page.equals("All Songs")) {
            getAudioAllSongs(0);
        } else  {
            getGenreSongs(0, page);
        }

    }


    @Override
    public void onAudioSongsMoreItemClick(View view, Song song, int position) {
        System.out.println("efjbdnfnff 4444");
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.onAudioSongsMoreItemClick(view, song, position);
    }

    @Override
    public void onAudioSongsPlayItemClick(Song song, int position) {
        System.out.println("efjbdnfnff 2222");
        List<Song> songs = new ArrayList<Song>();
        songs.add(song);
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.allSongsItemClickEvent(songs,null);
    }

    @Override
    public void onAddSongsItemClick(Song song) {

    }
}
