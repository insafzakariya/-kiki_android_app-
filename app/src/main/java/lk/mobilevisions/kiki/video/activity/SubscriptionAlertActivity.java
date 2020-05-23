package lk.mobilevisions.kiki.video.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.databinding.ActivitySubscriptionAlertBinding;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.ui.base.BaseActivity;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;


public class SubscriptionAlertActivity extends BaseActivity implements View.OnClickListener {

    ActivitySubscriptionAlertBinding binding;
    Episode episode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_alert);

        int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        binding.goBackTextview.setOnClickListener(this);
        binding.subscribeNowLayout.setOnClickListener(this);
        episode = (Episode) getIntent().getSerializableExtra("Episode");
        if (episode != null) {
            binding.mediacontrollerProgress.setEnabled(false);
            binding.textViewTitle.setText(episode.getName());
            try {
                Picasso.with(SubscriptionAlertActivity.this).load(URLDecoder.decode(episode.getImage(), "UTF-8"))
                        .fit()
                        .placeholder(R.drawable.program)
                        .into(binding.episodeImageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.subscribe_now_layout:
                Intent intentPackages = new Intent(this, PaymentActivity.class);
                startActivity(intentPackages);
                finish();
                break;
            case R.id.go_back_textview:
                finish();
                break;
            default:
        }
    }
}
