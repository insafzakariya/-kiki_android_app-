package lk.mobilevisions.kiki.audio.fragment;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;
import lk.mobilevisions.kiki.audio.adapter.ChannelHorizontalAdapter;
import lk.mobilevisions.kiki.audio.adapter.ChannelVerticalAdapter;

import lk.mobilevisions.kiki.audio.model.dto.AudioChannel;
import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;

import lk.mobilevisions.kiki.databinding.FragmentChannelBinding;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class ChannelFragment extends Fragment implements ChannelVerticalAdapter.OnAudioChannelItemClickListener,ChannelHorizontalAdapter.OnAudioProgramItemClickListener{

    @Inject
    TvManager tvManager;

    RecyclerView recyclerView;
   private FrameLayout programeFrame;

    private List<AudioChannel> audioChannels = new ArrayList<>();

FragmentChannelBinding binding;
    ChannelVerticalAdapter mAdapter;
    public ChannelFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel, container, false);


        ((Application) getActivity().getApplication()).getInjector().inject(this);
//
//        binding.channelRecyclerview.addItemDecoration(new SpacesItemDecoration(30));
//        binding.channelRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//
//        mAdapter = new ChannelVerticalAdapter(getActivity(), audioChannels,ChannelFragment.this,ChannelFragment.this,ChannelFragment.this);
//        binding.channelRecyclerview.setAdapter(mAdapter);
//
//        getChannels();
        return binding.getRoot();
    }

    @Override
    public void onAudioChannelItemClick(View view, AudioChannel channel, int position) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("audioChannelId", channel.getId());
//        bundle.putString("audioChannelName", channel.getName());
//        ProgrammesFragment a2Fragment = new ProgrammesFragment();
//        a2Fragment.setArguments(bundle);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.frame_container_programe, a2Fragment,"programes")
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void onAudioProgramItemClick(View view, AudioProgram program, int position) {

        Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
        i.putExtra("type", "channel");
        i.putExtra("programId", program.getId());
        i.putExtra("programName", program.getName());
        getActivity().startActivity(i);
    }


    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            outRect.right = space;
            outRect.bottom = space;
            outRect.left = space;


            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = 0;
            } else {
                outRect.top = space;
            }
        }
    }



    @Override
    public void onResume() {
        System.out.println("dwifgfgfg onResume" );
        super.onResume();
    }

    @Override
    public void onStart() {
        System.out.println("dwifgfgfg onStart" );
        super.onStart();
    }


//    private void getChannels(){
//        System.out.println("checking getAllAudioChannels 4444  " );
//        tvManager.getAllAudioChannels(new APIListener<List<AudioChannel>>() {
//            @Override
//            public void onSuccess(List<AudioChannel> channels, List<Object> params) {
//                System.out.println("checking getAllAudioChannels 6666  " + channels.size());
//                audioChannels = channels;
//
//                binding.channelRecyclerview.setAdapter(new ChannelVerticalAdapter(getContext(),
//                        audioChannels, ChannelFragment.this,ChannelFragment.this,ChannelFragment.this));
//                if (channels.size() <= 0) {
//                    binding.channelRecyclerview.setVisibility(View.GONE);
//                    binding.frameContainerPrograme.setVisibility(View.GONE);
//                    binding.textView14.setVisibility(View.VISIBLE);
//                    binding.aviProgress.setVisibility(View.GONE);
//
//                } else {
//
//                    binding.channelRecyclerview.setVisibility(View.VISIBLE);
//                    binding.frameContainerPrograme.setVisibility(View.VISIBLE);
//                    binding.textView14.setVisibility(View.GONE);
//                    binding.aviProgress.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                System.out.println("checking getAllAudioChannels 5555  " + t.getLocalizedMessage());
//                Utils.Error.onServiceCallFail(getContext(), t);
//            }
//        });
//    }

}
