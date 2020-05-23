package lk.mobilevisions.kiki.ui.auth;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.i18n.phonenumbers.Phonenumber;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentResetCodeConfirmationBinding;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.ui.widgets.countryselect.IntlPhoneInput;

public class ForgotPasswordMobileFragment extends Fragment implements IntlPhoneInput.IntlPhoneInputListener, View.OnClickListener {

FragmentResetCodeConfirmationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HeaderResponse headerResponse = ((Application) getActivity().getApplication()).getSession()
                .getHeaderResponse();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_code_confirmation, container, false);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "ForgotPasswordMobileFragment", null);
        binding.confirmLayout.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        String phoneNumber = headerResponse.getPhoneNumber();
        if (phoneNumber != null) {
            binding.editTextMobileNumber.setNumberDataConnection(phoneNumber);
            if (headerResponse.isMobileDataConnection()) {
                binding.editTextMobileNumber.setEnabled(false);
            } else {
                binding.editTextMobileNumber.setEnabled(true);
            }

        } else {
            binding.editTextMobileNumber.setNumber("+9407");
        }
        binding.editTextMobileNumber.setOnKeyboardDone(this);
        binding.editTextMobileNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    confirmMobileNumber();
                    return true;
                }
                return false;
            }
        });
        return binding.getRoot();
    }

    private void confirmMobileNumber() {
        if (binding.editTextMobileNumber.getPhoneNumber() == null
                || "".equals(binding.editTextMobileNumber.getPhoneNumber())) {
            Utils.Dialog.showOKDialog(getActivity(), getString(R.string.invalid_mobile_number));
            return;
        }

        System.out.println("en2ejkwnjkenjkernj 444444");
        Phonenumber.PhoneNumber phoneNumber = binding.editTextMobileNumber.getPhoneNumber();
        String mobileNumber = "" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber();

        Application.BUS.post(new UserMobileNumberEvent(mobileNumber));
    }

    @Override
    public void done(View view, boolean isValid) {
        if (isValid) {

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_layout:
                System.out.println("en2ejkwnjkenjkernj 33333");
                confirmMobileNumber();
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            default:
        }

    }

    public class UserMobileNumberEvent {

        private String mobileNumber;


        public UserMobileNumberEvent(String mobileNumber) {
            this.mobileNumber = mobileNumber;

        }

        public String getMobileNumber() {
            return mobileNumber;
        }


    }



    public class UserNavigatesBack {
    }
}



