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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;
import lk.mobilevisions.kiki.audio.adapter.ProgramAdapter;

import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;

import lk.mobilevisions.kiki.databinding.FragmentProgramesBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class ProgrammesFragment extends Fragment implements ProgramAdapter.OnAudioProgramItemClickListener {
    @Inject
    TvManager tvManager;
    ProgramAdapter mAdapter;
    FragmentProgramesBinding binding;
    private List<AudioProgram> audioPrograms = new ArrayList<>();
    private Animation animShow, animHide;
    private int channelId;
    private String channelName;
    Date selectedDate;

    public ProgrammesFragment() {
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


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_programes, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            channelId = bundle.getInt("audioChannelId", 0);
            channelName = bundle.getString("audioChannelName", null);
        }

        selectedDate = Utils.DateUtil.getDateOnly(new Date());
//        recyclerView.addItemDecoration(new SpacesItemDecoration(30));

//        AudioDashboardActivity audioDashboardActivity = (AudioDashboardActivity) getActivity();
//        audioDashboardActivity.changeToolbarName(channelName);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = false;
        binding.programesRecyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        binding.programesRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        mAdapter = new ProgramAdapter(getActivity(), audioPrograms, ProgrammesFragment.this, ProgrammesFragment.this);

        binding.programesRecyclerview.setAdapter(mAdapter);

        getAudioProgram();

        return binding.getRoot();
    }


    private void getAudioProgram() {
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getAudioPrograms(channelId, dateToSend, dateToSend, new APIListener<List<AudioProgram>>() {
            @Override
            public void onSuccess(List<AudioProgram> programs, List<Object> params) {
                audioPrograms = programs;
                binding.programesRecyclerview.setAdapter(new ProgramAdapter(getContext(),
                        audioPrograms, ProgrammesFragment.this, ProgrammesFragment.this));
                if (programs.size() <= 0) {
                    binding.programesRecyclerview.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.programesRecyclerview.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }

    @Override
    public void onAudioProgramItemClick(View view, AudioProgram program, int position) {
        Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
        i.putExtra("programId", program.getId());
        i.putExtra("programName", program.getName());
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
