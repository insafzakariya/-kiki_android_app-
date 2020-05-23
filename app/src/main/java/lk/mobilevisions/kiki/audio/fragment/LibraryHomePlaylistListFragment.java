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
import lk.mobilevisions.kiki.audio.adapter.LibraryHomePlaylistListAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.databinding.FragmentGenreWisePlaylistBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class LibraryHomePlaylistListFragment extends Fragment implements LibraryHomePlaylistListAdapter.OnPlaylistItemClickListener {
    @Inject
    TvManager tvManager;


    FragmentGenreWisePlaylistBinding binding;
    LibraryHomePlaylistListAdapter mAdapter;
    List<PlayList> playlistArrayList = new ArrayList<>();
    private Animation animShow, animHide;
    LinearLayoutManager channelsLayoutManager;

    public LibraryHomePlaylistListFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_genre_wise_playlist, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);


//        int genreId = getArguments().getInt("genreId");

//        System.out.println("Check artist 1414141414 " + genreId);

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        binding.genrePlaylistRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new LibraryHomePlaylistListAdapter(getActivity(), playlistArrayList, LibraryHomePlaylistListFragment.this);
        binding.genrePlaylistRecyclerview.setHasFixedSize(true);
        binding.genrePlaylistRecyclerview.setItemViewCacheSize(50);
        binding.genrePlaylistRecyclerview.setDrawingCacheEnabled(true);
        binding.genrePlaylistRecyclerview.setAdapter(mAdapter);
        setDataToPlaylists();

        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        return binding.getRoot();

    }


    private void setDataToPlaylists() {

        tvManager.getAllPlaylist( new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {
                PlayList playList = new PlayList();
                playList.setId(0);
                playList.setArtistName("");
                playList.setDate("");
                playList.setImage("");
                playList.setName("");
                playList.setSongCount("");
                playList.setSongsList(null);
                playList.setDefault(true);
                playlistArrayList.add(playList);
                playlistArrayList.addAll(playLists);
                mAdapter = new LibraryHomePlaylistListAdapter(getActivity(), playlistArrayList, LibraryHomePlaylistListFragment.this);
                binding.genrePlaylistRecyclerview.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
//                playlistArrayList = playLists;
//                binding.genrePlaylistRecyclerview.setAdapter(new LibraryHomePlaylistListAdapter(getContext(),
//                        playlistArrayList, LibraryHomePlaylistListFragment.this));
//                if (playLists.size() <= 0) {
//                    binding.genrePlaylistRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);
//
//                } else {
//
//                    binding.genrePlaylistRecyclerview.setVisibility(View.VISIBLE);
//                    binding.aviProgress.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });
    }

    private void removePlaylistDialog(PlayList playList) {

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
                int playlistID = playList.getId();
                String str = Integer.toString(playlistID);
                System.out.println("afjbdjh " + str);
                removePlaylist(str);
                playlistArrayList.remove(playList);
                mAdapter.notifyDataSetChanged();
                alertDialog.dismiss();

            }
        });
        noTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

    }

    private void removePlaylist(String playlistID){

        tvManager.removePlaylist(playlistID, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
//                librarySongList.remove(removeSongId);
//                mAdapter.notifyDataSetChanged();
                System.out.println("successss 2121212");
//                setDataToPlaylists();
                Toast.makeText(getActivity(), "Removed from library.", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    @Override
    public void onPlaylistItemClick(PlayList playList, int position, List<PlayList> songs) {

        if(position==0){
            Bundle bundle=new Bundle();
            bundle.putBoolean("editPlaylist",false);
            PlaylistCreationFragment playlistCreationFragment = new PlaylistCreationFragment();
            playlistCreationFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.Library_playlist_list_creation, playlistCreationFragment, "Library to Playlist")
                    .addToBackStack(null)
                    .commit();
        }else{
            Bundle bundle=new Bundle();
            bundle.putInt("playlistID", playList.getId());
            bundle.putString("playlistName", playList.getName());
            bundle.putString("songCount", playList.getSongCount());
            bundle.putString("playlistImage", playList.getImage());
            bundle.putString("playlistYear", playList.getDate());
            LibraryHomePlaylistDetailFragment libraryHomePlaylistDetailFragment = new LibraryHomePlaylistDetailFragment();
            libraryHomePlaylistDetailFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.Library_user_playlist_detail, libraryHomePlaylistDetailFragment, "Home to PlaylistDetail")
                    .addToBackStack(null)
                    .commit();

        }

    }

    @Override
    public void onRemovePlaylistItemClick(PlayList playList) {

        removePlaylistDialog(playList);

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