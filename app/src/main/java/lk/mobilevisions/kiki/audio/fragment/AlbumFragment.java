package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.AlbumAdapter;

import lk.mobilevisions.kiki.audio.model.AlbumModel;

public class AlbumFragment extends Fragment {


    View view;
    RecyclerView recyclerView;
    AlbumAdapter mAdapter;
    ArrayList<AlbumModel> mArrayList = new ArrayList<>();
    public AlbumFragment() {
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

        view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.songs_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new AlbumAdapter(getActivity(), mArrayList);

        recyclerView.setAdapter(mAdapter);

        setDataOnVerticalRecyclerView();

        return view;
    }

    private void setDataOnVerticalRecyclerView() {
        for (int i = 1; i <= 22; i++) {

            AlbumModel mVerticalModel = new AlbumModel();

            mVerticalModel.setName("Title ");
            mVerticalModel.setDescription("hahaha ");

            mArrayList.add(mVerticalModel);

        }
        mAdapter.notifyDataSetChanged();
    }
}
