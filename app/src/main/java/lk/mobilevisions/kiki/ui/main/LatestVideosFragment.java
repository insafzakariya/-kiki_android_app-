package lk.mobilevisions.kiki.ui.main;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.mobilevisions.kiki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestVideosFragment extends Fragment {


    public LatestVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_videos, container, false);
    }

}
