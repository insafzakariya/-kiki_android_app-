//package lk.mobilevisions.kiki.audio.fragment;
//
//import android.databinding.DataBindingUtil;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.RelativeLayout;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import lk.mobilevisions.kiki.R;
//import lk.mobilevisions.kiki.app.Application;
//import lk.mobilevisions.kiki.app.Utils;
//
//import lk.mobilevisions.kiki.audio.adapter_program_item.ProgramAdapter;
//import lk.mobilevisions.kiki.audio.adapter_program_item.SongsAdapter;
//
//import lk.mobilevisions.kiki.audio.model.HorizontalModel;
//import lk.mobilevisions.kiki.audio.model.SongsModel;
//import lk.mobilevisions.kiki.audio.model.VerticalModel;
//import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;
//import lk.mobilevisions.kiki.audio.model.dto.Song;
//import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
//
//import lk.mobilevisions.kiki.modules.api.APIListener;
//import lk.mobilevisions.kiki.modules.tv.TvManager;
//
//public class SongsFragment extends Fragment implements SongsAdapter.OnAudioFavouriteItemClickListener {
//
//
//    @Inject
//    TvManager tvManager;
//
//    RecyclerView recyclerView;
//    SongsAdapter mAdapter;
//    List<Song> mArrayList = new ArrayList<>();
//    private Animation animShow, animHide;
//    public SongsFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_songs, container, false);
////        ((Application) getActivity().getApplication()).getInjector().inject(this);
////
////
////        binding.songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
////        mAdapter = new SongsAdapter(getActivity(), mArrayList,SongsFragment.this,SongsFragment.this);
////
////        binding.songsRecyclerview.setAdapter(mAdapter);
////
////
////        getAudioFavouriteSongs();
//        return null;
//    }
//
//    private void getAudioFavouriteSongs(){
//
////        tvManager.getAudioFavouriteSongs( new APIListener<List<Song>>() {
////            @Override
////            public void onSuccess(List<Song> songs, List<Object> params) {
////                mArrayList = songs;
////                binding.songsRecyclerview.setAdapter(new SongsAdapter(getContext(),
////                        mArrayList, SongsFragment.this,SongsFragment.this));
////                if (songs.size() <= 0) {
////                    binding.songsRecyclerview.setVisibility(View.GONE);
////                    binding.aviProgress.setVisibility(View.GONE);
////
////                } else {
////
////                    binding.songsRecyclerview.setVisibility(View.VISIBLE);
////                    binding.aviProgress.setVisibility(View.GONE);
////                }
////            }
////
////            @Override
////            public void onFailure(Throwable t) {
////                Utils.Error.onServiceCallFail(getActivity(), t);
////            }
////        });
//    }
//    public void openAddtoPlayListWindow(int id){
//
////        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
////        hhh.openAddtoPlayListWindow(0);
////        if(addPlaylistLayout.getVisibility() == View.VISIBLE){
////            addPlaylistLayout.startAnimation(animHide);
////            addPlaylistLayout.setVisibility(View.GONE);
////        }else{
////            addPlaylistLayout.startAnimation(animShow);
////            addPlaylistLayout.setVisibility(View.VISIBLE);
////        }
//
//    }
//
//    @Override
//    public void onAudioFavouriteItemClick(View view, AudioProgram program, int position) {
//
//    }
//}
