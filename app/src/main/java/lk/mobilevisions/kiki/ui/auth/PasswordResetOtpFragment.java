package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentPasswordResetMobileBinding;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;

public class PasswordResetOtpFragment extends Fragment implements View.OnClickListener {

    FragmentPasswordResetMobileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_reset_mobile, container, false);
        binding.otpCode.setOnClickListener(this);
        binding.resetLayout.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.resendOtp.setOnClickListener(this);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "PasswordResetOtpFragment", null);

        try {
            Application.BUS.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return binding.getRoot();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_layout:
                if (binding.otpCode.getText().toString().length() != 4) {
                    Utils.Dialog.showOKDialog(getActivity(), getString(R.string.invalid_otp));
                    return;
                }
                String otp = binding.otpCode.getText().toString();
                Application.BUS.post(new UserOtpRequest(otp));
//                Application.BUS.unregister(this);
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            case R.id.resend_otp:
                Application.BUS.post(new UserResetPassword());
                break;
            default:

        }
    }

    public class UserNavigatesBack {
    }

    public class UserResetPassword {


    }

    public class UserOtpRequest {

        private String otp;

        public UserOtpRequest(String otp) {
            this.otp = otp;

        }

        public String getOtp() {
            return otp;
        }

    }

    @Override
    public void onResume() {
        binding.otpCode.setText("");
        super.onResume();
    }


    @Subscribe
    public void onSetUserNameEvent(LoginActivity.SetUserNameEvent event) {
        ForgotPasswordUser forgotPasswordUser = Application.getInstance().getForgotPasswordUser();
        if (forgotPasswordUser != null) {

            binding.showUsername.setText(getString(R.string.hi_user, forgotPasswordUser.getUserName()));

        }
    }
}
