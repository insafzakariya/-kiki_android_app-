/**
 * Created by Chatura Dilan Perera on 5/1/2017.
 */
package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentSmsVerifyBinding;


public class SMSVerifyFragment extends Fragment {

FragmentSmsVerifyBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sms_verify, container, false);

        binding.buttonViewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.editTextSMSVerification.getText().toString() == null
                        || "".equals(binding.editTextSMSVerification.getText().toString())) {
                    Utils.Dialog.createOKDialog(getActivity(), "SMS code cannot be empty", null);
                    return;

                }
                Application.BUS.post(new
                        SMSVerifyFragment.SMSVerifyEvent(
                        Integer.parseInt(binding.editTextSMSVerification.getText().toString())));

            }
        });


        return binding.getRoot();

    }


    public class SMSVerifyEvent {

        private int smsCode;

        public SMSVerifyEvent(int smsCode) {
            this.smsCode = smsCode;
        }

        public int getSmsCode() {
            return smsCode;
        }

        public void setSmsCode(int smsCode) {
            this.smsCode = smsCode;
        }
    }
}
