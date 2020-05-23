package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.ArtistAdapter;
import lk.mobilevisions.kiki.audio.model.ArtistModel;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class ArtistFragment extends Fragment implements ArtistAdapter.OnAudioFavouriteArtistItemClickListener {


    View view;
    RecyclerView recyclerView;
    ArtistAdapter mAdapter;
   List<ArtistModel> mArrayList = new ArrayList<>();
    public ArtistFragment() {
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

        view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.songs_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ArtistAdapter(getActivity(), mArrayList,ArtistFragment.this);

        recyclerView.setAdapter(mAdapter);

        setDataOnVerticalRecyclerView();

        return view;
    }

    private void setDataOnVerticalRecyclerView() {
        for (int i = 1; i <= 22; i++) {

            ArtistModel mVerticalModel = new ArtistModel();

            mVerticalModel.setName("Title ");
            mVerticalModel.setDescription("hahaha ");

            mArrayList.add(mVerticalModel);

        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAudioFavouriteArtistItemClick(View view, Song program, int position) {

    }
}
