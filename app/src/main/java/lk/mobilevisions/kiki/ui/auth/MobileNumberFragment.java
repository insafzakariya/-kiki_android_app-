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
import lk.mobilevisions.kiki.databinding.FragmentMobileNumberBinding;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.ui.widgets.countryselect.IntlPhoneInput;

public class MobileNumberFragment extends Fragment implements IntlPhoneInput.IntlPhoneInputListener, View.OnClickListener {


    FragmentMobileNumberBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HeaderResponse headerResponse = ((Application) getActivity().getApplication()).getSession()
                .getHeaderResponse();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_number, container, false);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "MobileNumberFragment", null);
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
        User user = new User();
        Phonenumber.PhoneNumber phoneNumber = binding.editTextMobileNumber.getPhoneNumber();
        user.setMobileNumber("+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber());
        user.setCountryCode(binding.editTextMobileNumber.getSelectedCountry().getIso().toUpperCase());
        user.setSession(((Application) getActivity().getApplication()).getSession());
        Application.BUS.post(new UserMobileNumberEvent(user, phoneNumber));
    }

    @Override
    public void done(View view, boolean isValid) {
        if (isValid) {
            confirmMobileNumber();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_layout:
                confirmMobileNumber();
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            default:
        }
    }

    public class UserMobileNumberEvent {

        private User user;

        private Phonenumber.PhoneNumber phoneNumber;

        public UserMobileNumberEvent(User user, Phonenumber.PhoneNumber phoneNumber) {
            this.user = user;
            this.phoneNumber = phoneNumber;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Phonenumber.PhoneNumber getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public class UserNavigatesBack {
    }

}
