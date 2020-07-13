package lk.mobilevisions.kiki.video.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityVideoChildModeBinding;


public class VideoChildModeActivity extends AppCompatActivity   {

    ActivityVideoChildModeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_child_mode);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }

        binding.includedToolbar.titleTextview.setText(R.string.menu_chile_mode);

        binding.includedToolbar.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(this, "kids_mode_password", "");
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            binding.onSelectImageview.setVisibility(View.VISIBLE);
            binding.offSelectImageview.setVisibility(View.GONE);
        } else {
            binding.onSelectImageview.setVisibility(View.GONE);
            binding.offSelectImageview.setVisibility(View.VISIBLE);

        }

        binding.onTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.onSelectImageview.getVisibility() == View.GONE)
                enableKidsMode();
            }
        });
        binding.offTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.offSelectImageview.getVisibility() == View.GONE)
                enableKidsMode();
            }
        });

    }

    private void enableKidsMode(){
        final String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(VideoChildModeActivity.this, "kids_mode_password", "");
        System.out.println("checking kidsmood 3333 " + kidsModePassword);
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            new MaterialDialog.Builder(VideoChildModeActivity.this)
                    .title(R.string.app_name)
                    .content(R.string.child_mode_deact_pass)
                    .inputRangeRes(4, 10, R.color.red)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .negativeText(getString(R.string.cancel))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (kidsModePassword.equals(input.toString())) {
                                Utils.SharedPrefUtil.saveStringToSharedPref(VideoChildModeActivity.this, "kids_mode_password", "");
                                Utils.Dialog.createOKDialog(VideoChildModeActivity.this, getResources().getString(R.string.child_mode_deact), new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                });
                                Application.BUS.post(new ChildModeOffEvent());
                                binding.onSelectImageview.setVisibility(View.GONE);
                                binding.offSelectImageview.setVisibility(View.VISIBLE);


                            } else {
                                Utils.Dialog.createOKDialog(VideoChildModeActivity.this, "Invalid password", new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                });
//                                Application.BUS.post(new ChildModeOnEvent());
//                                binding.onSelectImageview.setVisibility(View.VISIBLE);
//                                binding.offSelectImageview.setVisibility(View.GONE);

                            }

                        }
                    }).show();

        } else {
            new MaterialDialog.Builder(VideoChildModeActivity.this)
                    .title(R.string.app_name)
                    .content(R.string.child_mode_act_pass)
                    .inputRangeRes(4, 10, R.color.red)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .negativeText(getString(R.string.cancel))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {

                            Utils.SharedPrefUtil.saveStringToSharedPref(VideoChildModeActivity.this, "kids_mode_password", input.toString());
                            Utils.Dialog.createOKDialog(VideoChildModeActivity.this, getResources().getString(R.string.child_mode_act), new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            });
                            binding.onSelectImageview.setVisibility(View.VISIBLE);
                            binding.offSelectImageview.setVisibility(View.GONE);
                            Application.BUS.post(new ChildModeOnEvent());
                        }
                    }).show();
        }


    }

    public class ChildModeOnEvent {
    }

    public class ChildModeOffEvent {
    }
}
