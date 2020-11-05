package lk.mobilevisions.kiki.video.activity;


import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioProfileActivity;
import lk.mobilevisions.kiki.databinding.ActivityVideoProfileBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.ui.auth.EditMobileNumberActivity;


public class VideoProfileActivity extends AppCompatActivity implements View.OnClickListener {
    @Inject
    AuthManager authManager;
    ActivityVideoProfileBinding binding;
    private String selectedLanguage = "English";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_profile);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoProfileActivity", null);
        binding.includedToolbar.backImageview.setOnClickListener(this);
        binding.sinhalaTextView.setOnClickListener(this);
        binding.tamilTextView.setOnClickListener(this);
        binding.englishTextView.setOnClickListener(this);
        binding.phoneNumberEdittext.setOnClickListener(this);
        binding.updateLayout.setOnClickListener(this);
        binding.updateLayout.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            case R.id.english_text_view:
                changeLanguageSelectStates("english");
                break;
            case R.id.sinhala_text_view:
                changeLanguageSelectStates("sinhala");
                break;
            case R.id.tamil_text_view:
                changeLanguageSelectStates("tamil");
                break;
            case R.id.phone_number_edittext:
                Intent profileIntent = new Intent(this, EditMobileNumberActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.update_layout:
                updateProfileDetails();
                break;
            default:
        }
    }

    private void changeLanguageSelectStates(String language) {

        if (language.equals("english")) {
            selectedLanguage = "English";
            binding.tamilSelectImageview.setVisibility(View.GONE);
            binding.sinhalaSelectImageview.setVisibility(View.GONE);
            binding.englishSelectImageview.setVisibility(View.VISIBLE);

        } else if (language.equals("tamil")) {
            selectedLanguage = "Tamil";
            binding.tamilSelectImageview.setVisibility(View.VISIBLE);
            binding.sinhalaSelectImageview.setVisibility(View.GONE);
            binding.englishSelectImageview.setVisibility(View.GONE);
        } else if (language.equals("sinhala")) {

            selectedLanguage = "Sinhala";
            binding.tamilSelectImageview.setVisibility(View.GONE);
            binding.sinhalaSelectImageview.setVisibility(View.VISIBLE);
            binding.englishSelectImageview.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        getUserDetails();
        super.onResume();
    }

    private void getUserDetails(){
        binding.aviProgress.setVisibility(View.VISIBLE);
        authManager.getUserDetails(new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser user, List<Object> params) {
                binding.phoneNumberEdittext.setText(user.getMobileNumber());
                binding.nameEdittext.setText(user.getName());
                binding.usernameField.setText(user.getUsername());
                changeLanguageSelectStates(user.getLanguage().toLowerCase());
                binding.updateLayout.setEnabled(true);
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(VideoProfileActivity.this, t);
            }
        });
    }

    private void updateProfileDetails(){
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) VideoProfileActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        final User user = new User();
        user.setName(binding.nameEdittext.getText().toString());
        user.setLanguage(AuthOptions.Language.valueOf
                (selectedLanguage.toUpperCase()));
        authManager.updateUserDetails(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser result, List<Object> params) {
                ((Application) getApplication()).setAuthUser(result);
                progressDialog.dismiss();
                setUpAppLanguage();
                Intent intent = new Intent(VideoProfileActivity.this, VideoDashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT);

            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Utils.Error.onServiceCallFail(VideoProfileActivity.this, t);
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

    private void setUpAppLanguage(){
        if (selectedLanguage.equals("English")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_EN_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_EN_PHONE_FULL);

        } else if (selectedLanguage.equals("Tamil")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_TA_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_TA_PHONE_FULL);


        } else if (selectedLanguage.equals("Sinhala")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_SI_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_SI_PHONE_FULL);


        }
       finish();


    }
}
