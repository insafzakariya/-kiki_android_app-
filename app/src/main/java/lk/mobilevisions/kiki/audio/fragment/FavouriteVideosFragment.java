package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.FavouriteVideosAdapter;
import lk.mobilevisions.kiki.audio.model.FavouriteVideosModel;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class FavouriteVideosFragment extends Fragment {

    @Inject
    TvManager tvManager;
    View view;
    RecyclerView recyclerView;
    FavouriteVideosAdapter mAdapter;
    ArrayList<FavouriteVideosModel> mArrayList = new ArrayList<>();
    public FavouriteVideosFragment() {
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

        view = inflater.inflate(R.layout.fragment_fav_videos, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.songs_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new FavouriteVideosAdapter(getActivity(), mArrayList);

        recyclerView.setAdapter(mAdapter);

        setDataOnVerticalRecyclerView();

        return view;
    }

    private void setDataOnVerticalRecyclerView() {
        for (int i = 1; i <= 22; i++) {

            FavouriteVideosModel mVerticalModel = new FavouriteVideosModel();

            mVerticalModel.setName("Title ");
            mVerticalModel.setDescription("hahaha ");

            mArrayList.add(mVerticalModel);

        }
        mAdapter.notifyDataSetChanged();
    }


}
