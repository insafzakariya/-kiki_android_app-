package lk.mobilevisions.kiki.ui.auth;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityLanguageSelectionBinding;


public class LanguageSelectionActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LANGUAGE_SELECTION = "LANGUAGE_SELECTION";
    private String selectedLanguage = "English";
    ActivityLanguageSelectionBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_selection);
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "LanguageSelectionActivity", null);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }
        binding.sinhalaLayout.setOnClickListener(this);
        binding.tamilLayout.setOnClickListener(this);
        binding.englishLayout.setOnClickListener(this);
        binding.nextLayout.setOnClickListener(this);
        Application.BUS.register(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sinhala_layout:
                changeLanguageButtonStates("sinhala");
                binding.chooseLanguageTextview.setText("භාෂාව තෝරන්න");
                binding.nextTextview.setText("ඊළඟ");

                break;
            case R.id.tamil_layout:
                changeLanguageButtonStates("tamil");
                binding.chooseLanguageTextview.setText("மொழியைத் தேர்வு செய்க");
                binding.nextTextview.setText("அடுத்து");
                break;
            case R.id.english_layout:
                changeLanguageButtonStates("english");
                binding.chooseLanguageTextview.setText("Choose language");
                binding.nextTextview.setText("Next");
                break;
            case R.id.next_layout:
                setUpAppLanguage();
                break;
            default:
        }
    }

    private void changeLanguageButtonStates(String language) {

        if (language.equals("english")) {
            selectedLanguage = "English";
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.englishTextview.setTextColor(ContextCompat.getColor(this, R.color.md_black_1000));
            binding.tamilTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
        } else if (language.equals("tamil")) {
            selectedLanguage = "Tamil";
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.tamilTextview.setTextColor(ContextCompat.getColor(this, R.color.md_black_1000));
            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
            binding.englishTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
        } else if (language.equals("sinhala")) {

            selectedLanguage = "Sinhala";
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(this, R.color.md_black_1000));
            binding.tamilTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
            binding.englishTextview.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
        }

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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }

    @Subscribe
    public void onUserNavigateBack(MobileNumberFragment.UserNavigatesBack event) {
        finish();

    }
    @Subscribe
    public void onUserNavigateBack(LoginActivity.UserNavigatesBack event) {
        finish();

    }
    @Override
    protected void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }
}
