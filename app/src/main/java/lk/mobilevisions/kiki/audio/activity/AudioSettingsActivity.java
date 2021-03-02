package lk.mobilevisions.kiki.audio.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityAudioSettingsBinding;
import lk.mobilevisions.kiki.video.activity.VideoProfileActivity;

public class AudioSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAudioSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_settings);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "AudioSettingsActivity", null);
        binding.includedToolbar.titleTextview.setText("Settings");
        binding.includedToolbar.backImageview.setOnClickListener(this);
        binding.profileTextView.setOnClickListener(this);
        binding.paymentMethodsTextView.setOnClickListener(this);
        binding.licenseAgreementTextView.setOnClickListener(this);



    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            case R.id.profile_text_view:
                Intent profileIntent = new Intent(this, VideoProfileActivity.class);
                startActivity(profileIntent);
                finish();
                break;
            case R.id.payment_methods_text_view:
                Intent intentPackages = new Intent(this, AudioPaymentActivity.class);
                startActivity(intentPackages);
                break;
            case R.id.license_agreement_text_view:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.App.getConfig(getApplication()).getUserAgreementPage()));
                startActivity(browserIntent);
                break;
            default:
        }
    }
}
