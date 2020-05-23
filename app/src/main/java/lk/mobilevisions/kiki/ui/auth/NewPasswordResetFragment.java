package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.FragmentForgotPasswordResetBinding;

public class NewPasswordResetFragment extends Fragment implements View.OnClickListener {

    FragmentForgotPasswordResetBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password_reset, container, false);
        binding.signUpLayout.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "NewPasswordResetFragment", null);


        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_layout:
           String password = binding.passwordEdittext.getText().toString();
           String confirmPassword = binding.confirmPasswordEdittext.getText().toString();
                Application.BUS.post(new UserNewPasswordEvent(password,confirmPassword));
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            default:
        }
    }

    public class UserNewPasswordEvent {

        private String password;
        private String confirmPassword;

        public UserNewPasswordEvent(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String getPassword() {
            return password;
        }
        public String getConfirmPassword() {

            return confirmPassword;
        }

    }

    public class UserNavigatesBack {
    }

    @Override
    public void onResume() {
        binding.passwordEdittext.setText("");
        binding.confirmPasswordEdittext.setText("");
        super.onResume();
    }
}