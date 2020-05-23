package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.JazzMusicAdapter;

public class JazzMusicFragment extends Fragment {


    View view;
    RecyclerView recyclerView;
    JazzMusicAdapter adapter;


    public JazzMusicFragment() {
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

        view = inflater.inflate(R.layout.fragment_jazz_music, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.songs_recyclerview);

        adapter = new JazzMusicAdapter(getActivity());
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        return view;
    }




}
