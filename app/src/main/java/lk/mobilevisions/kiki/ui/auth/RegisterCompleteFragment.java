package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.audio.adapter.AlbumAdapter;
import lk.mobilevisions.kiki.audio.model.AlbumModel;
import lk.mobilevisions.kiki.databinding.FragmentRegisterCompleteBinding;

public class RegisterCompleteFragment extends Fragment {


    View view;
    RecyclerView recyclerView;
    AlbumAdapter mAdapter;
    ArrayList<AlbumModel> mArrayList = new ArrayList<>();


    FragmentRegisterCompleteBinding binding;
    public RegisterCompleteFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_complete, container, false);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(),"RegisterCompleteFragment",null);

binding.enterLayout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Application.BUS.post(new RegisterCompleteEvent());
    }
});


        return binding.getRoot();
    }

    public class RegisterCompleteEvent {




        public RegisterCompleteEvent() {

        }



    }
}