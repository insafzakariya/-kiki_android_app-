package lk.mobilevisions.kiki.ui.auth;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.FragmentResetCompleteBinding;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;

public class ResetPasswordCompleteFragment extends Fragment implements View.OnClickListener {

    FragmentResetCompleteBinding binding;

    @SuppressLint("StringFormatInvalid")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_complete, container, false);
        binding.relative1.setOnClickListener(this);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "ResetPasswordCompleteFragment", null);

        ForgotPasswordUser forgotPasswordUser = Application.getInstance().getForgotPasswordUser();
        binding.textView14.setText(getString(R.string.activate_kiki_reset,forgotPasswordUser.getUserName()));

        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative1:
                Application.BUS.post(new UserNavigateToLogin());
                break;
            default:
        }

    }

    public class UserNavigateToLogin {
    }


}
