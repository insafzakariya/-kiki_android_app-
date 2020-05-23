package lk.mobilevisions.kiki.audio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.databinding.FragmentComingSoonBinding;

public class ComingSoonFragment extends Fragment {

    FragmentComingSoonBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_coming_soon, container, false);


    }
}
