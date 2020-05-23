package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.PlaylistAdapter;

import lk.mobilevisions.kiki.audio.model.AlbumModel;

public class MyListFragment extends Fragment {


    View view;
    RecyclerView recyclerView;
    PlaylistAdapter adapter;
    ArrayList<AlbumModel> mArrayList = new ArrayList<>();

    public MyListFragment() {
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

        view = inflater.inflate(R.layout.fragment_mylist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.songs_recyclerview);

//        adapter_program_item = new PlaylistAdapter(getActivity());
//        adapter_program_item.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter_program_item);

        return view;
    }




}
