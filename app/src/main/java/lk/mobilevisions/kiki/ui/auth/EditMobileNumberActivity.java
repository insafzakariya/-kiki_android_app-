package lk.mobilevisions.kiki.ui.auth;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityEditMobileNumberBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.ui.base.BaseActivity;


public class EditMobileNumberActivity extends BaseActivity {

    @Inject
    AuthManager authManager;
    ActivityEditMobileNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_mobile_number);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoProfileActivity", null);
        authManager.getUserDetails(new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser user, List<Object> params) {
                System.out.println("fgkwsgfkgfgfk " + user.getMobileNumber());
                binding.editTextMobileNumber.setNumberEdit(user.getMobileNumber());
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(EditMobileNumberActivity.this, t);
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateMobileNumber();
            }
        });

    }

    private MaterialDialog createProgressDialog() {
        MaterialDialog dialog = Utils.Dialog.createDialog(this, getString(R.string.please_wait))
                .cancelable(false)
                .backgroundColorRes(R.color.dialogProgressBackground)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .build();
        return dialog;
    }

    private void updateMobileNumber() {
        if (binding.editTextMobileNumber.getPhoneNumber() == null
                || "".equals(binding.editTextMobileNumber.getPhoneNumber())) {
            Utils.Dialog.showOKDialog(EditMobileNumberActivity.this,
                    getString(R.string.invalid_mobile_number));
            return;
        }

        final User user = new User();
        Phonenumber.PhoneNumber phoneNumber = binding.editTextMobileNumber.getPhoneNumber();
        user.setMobileNumber("+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber());
        user.setCountryCode(binding.editTextMobileNumber.getSelectedCountry().getIso().toUpperCase());
        String selectedLanguage = Utils.SharedPrefUtil.getStringFromSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, null);
        user.setLanguage(AuthOptions.Language.valueOf
                (selectedLanguage.toUpperCase()));

        if (!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
            Utils.Dialog.createOKDialog(EditMobileNumberActivity.this,
                    getString(R.string.invalid_mobile_number), null);
            return;
        }

        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) EditMobileNumberActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        authManager.updateUserMobileNumber(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser result, List<Object> params) {
                        AuthUser authUser = ((Application) getApplication()).getAuthUser();
                        authUser.setMobileNumber(result.getMobileNumber());
                        authUser.setMobileNumberVerified(result.isMobileNumberVerified());
                        progressDialog.dismiss();
                      finish();
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Utils.Error.onServiceCallFail(EditMobileNumberActivity.this, t);
            }
        });
    }
}
