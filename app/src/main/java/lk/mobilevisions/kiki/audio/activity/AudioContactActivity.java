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
import lk.mobilevisions.kiki.databinding.ActivityAudioContactBinding;


public class AudioContactActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAudioContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_contact);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoContactActivity", null);
        binding.includedToolbar.titleTextview.setText("Support");
        binding.includedToolbar.backImageview.setOnClickListener(this);
        binding.callLayout.setOnClickListener(this);
        binding.emailLayout.setOnClickListener(this);



        binding.contactNumberTextView.setText(Utils.App.getConfig(getApplication()).getContactTelephone());
        binding.contactEmailTextView.setText(Utils.App.getConfig(getApplication()).getContactEmail());


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            case R.id.call_layout:
                callSupport();
                break;
            case R.id.email_layout:
                emailSupport();
                break;
            default:
        }
    }

    private void callSupport() {
        Uri number = Uri.parse("tel:" + Utils.App.getConfig(getApplication()).getContactTelephone());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    private void emailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Utils.App.getConfig(getApplication()).getContactEmail(), null));
        startActivity(Intent.createChooser(emailIntent, "Contact KiKi"));
    }
}
