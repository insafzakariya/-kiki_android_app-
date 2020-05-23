package lk.mobilevisions.kiki.audio.fragment;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.CustomAdapter;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.widgets.AnimatedExpandableListView;
import lk.mobilevisions.kiki.databinding.FragmentPlaylistBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class PlaylistFragment extends Fragment implements CustomAdapter.OnAudioPlaylistItemClickListener {

    @Inject
    TvManager tvManager;

    private AnimatedExpandableListView listView;
    FragmentPlaylistBinding binding;
    List<PlayList> mArrayList = new ArrayList<>();
    private CustomAdapter listAdapter;
    private int currentPlaylistIndex;
    public PlaylistFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);


        listView = (AnimatedExpandableListView) binding.getRoot().findViewById(R.id.songs_recyclerview);
        if (tvManager != null) {
            System.out.println("dydhhdhdhdhdhdh 22222222 " );
            setDataToPlaylist();
        }
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, long id) {
                System.out.println("dhdhdhdhdhdhdhdhdhd 11111111 ");
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                    final PlayList playList = mArrayList.get(groupPosition);
                    if (playList.getSongsList().size() == 0) {
                        listView.collapseGroupWithAnimation(groupPosition);
                        Utils.Dialog.showOKDialog(getActivity(), "No Songs Available");

                    } else {
                        playList.setSongsList(playList.getSongsList());
                        listAdapter.notifyDataSetChanged();
                        listAdapter.notifyDataSetInvalidated();
                    }

                }
                return true;
            }

        });

        return binding.getRoot();


    }

    @Override
    public void onResume() {
        System.out.println("dydhhdhdhdhdhdh 111111 " );

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void setDataToPlaylist() {
        System.out.println("dydhhdhdhdhdhdh 3333333 " );
        currentPlaylistIndex = 0;
        tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {

                mArrayList = playLists;
                System.out.println("dydhhdhdhdhdhdh 44444444 " + mArrayList.size() );
                if (playLists.size() == 0) {
                    binding.noPlaylistTextview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {
                    // create the adapter_program_item by passing your ArrayList data
                    listAdapter = new CustomAdapter(getActivity(), PlaylistFragment.this);
                    // attach the adapter_program_item to the expandable list view
                    listView.setAdapter(listAdapter);
                    System.out.println("dydhhdhdhdhdhdh 5555555 ");
                    fetchSongsByPlaylistId(mArrayList.get(0));
                }

            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });


    }

    private void fetchSongsByPlaylistId(final PlayList playlist) {
        System.out.println("dydhhdhdhdhdhdh 666666666 ");
        System.out.println("dydhhdhdhdhdhdh 6555555556 ");
        tvManager.getSongsOfPlaylist(playlist.getId(), new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                System.out.println("dydhhdhdhdhdhdh 7777777   " + songs.size());
                binding.aviProgress.setVisibility(View.GONE);
                playlist.setSongsList(songs);
                listAdapter.addPlayList(playlist);

                currentPlaylistIndex = currentPlaylistIndex + 1;
                if (currentPlaylistIndex < mArrayList.size()) {
                    fetchSongsByPlaylistId(mArrayList.get(currentPlaylistIndex));
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onAudioSongsPlayListItemClick(PlayList playList, int position) {
        System.out.println("dhdhdhdhdhdhdhdhdhd 22222222 ");

        if(playList.getSongsList()!=null && playList.getSongsList().size() == 0){
            Utils.Dialog.showOKDialog(getActivity(), "No Songs Available");
        }else{
            AudioDashboardActivity audioDashboardActivity = (AudioDashboardActivity) getActivity();
//            audioDashboardActivity.allSongsItemClickEvent(playList.getSongsList());
        }

    }

    @Override
    public void onAudioSongsPlaySongItemClick(Song song, int position) {
        System.out.println("dhdhdhdhdhdhdhdhdhd 3333333 ");
        List<Song> songs = new ArrayList<Song>();
        songs.add(song);
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//        hhh.allSongsItemClickEvent(songs);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onAudioSongsPauseSongItemClick(Song song, int position) {
        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
        hhh.playAndPauseSong();
        listAdapter.notifyDataSetChanged();
    }
}
