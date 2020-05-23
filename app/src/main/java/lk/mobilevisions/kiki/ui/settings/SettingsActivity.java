package lk.mobilevisions.kiki.ui.settings;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivitySettingsBinding;
import lk.mobilevisions.kiki.ui.base.BaseActivity;


public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(this, "kids_mode_password", "");
        System.out.println("checking kidsMode Password 1111  " + kidsModePassword);
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            System.out.println("checking kidsMode Password 2222  ");
            binding.toggleButtonParentalControl.setText("ON");
        } else {
            System.out.println("checking kidsMode Password 3333  ");
            binding.toggleButtonParentalControl.setText("OFF");
        }


        binding.toggleButtonParentalControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("checking kidsMode Password 4444  ");
                final String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(SettingsActivity.this, "kids_mode_password", "");

                if (kidsModePassword != null && !"".equals(kidsModePassword)) {
                    System.out.println("checking kidsMode Password 5555  ");
                    new MaterialDialog.Builder(SettingsActivity.this)
                            .title(R.string.app_name)
                            .content("Please enter password to deactivate Kid's Mode")
                            .inputRangeRes(4, 10, R.color.red)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .negativeText("Cancel")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .input("", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    System.out.println("checking kidsMode Password 6666  " + input.toString());
                                    if (kidsModePassword.equals(input.toString())) {
                                        System.out.println("checking kidsMode Password 77777  ");
                                        Utils.SharedPrefUtil.saveStringToSharedPref(SettingsActivity.this, "kids_mode_password", "");
                                        Utils.Dialog.createOKDialog(SettingsActivity.this, "Kid's mode is deactivated!", new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            }
                                        });
                                        binding.toggleButtonParentalControl.setText("OFF");
                                    } else {
                                        System.out.println("checking kidsMode Password 88888  ");
                                        Utils.Dialog.createOKDialog(SettingsActivity.this, "Invalid password", new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            }
                                        });
                                        binding.toggleButtonParentalControl.setText("ON");
                                    }

                                }
                            }).show();

                } else {
                    System.out.println("checking kidsMode Password 9999  ");
                    new MaterialDialog.Builder(SettingsActivity.this)
                            .title(R.string.app_name)
                            .content("Please enter password to activate Kid's Mode")
                            .inputRangeRes(4, 10, R.color.red)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .negativeText("Cancel")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .input("", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    System.out.println("checking kidsMode Password 1000  " + input.toString());
                                    binding.toggleButtonParentalControl.setText("ON");
                                    Utils.SharedPrefUtil.saveStringToSharedPref(SettingsActivity.this, "kids_mode_password", input.toString());
                                    Utils.Dialog.createOKDialog(SettingsActivity.this, "Kid's mode is activated!", new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        }
                                    });

                                }
                            }).show();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
