package lk.mobilevisions.kiki.ui.navigation;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;



import lk.mobilevisions.kiki.databinding.ActivityUpdateUserBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;

public class UpdateUserActivity extends AppCompatActivity  {


    @Inject
    AuthManager authManager;

    public static final int DEFAULT_YEAR = 1990;
    public static final int DEFAULT_MONTH_OF_YEAR = 0;
    public static final int DEFAULT_DAY_OF_MONTH = 1;
    public static final String USER_DETAILS = "USER_DETAILS";
    private String birthDate;
    ActivityUpdateUserBinding binding;
    private boolean unVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_user);
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Application) getApplication()).getInjector().inject(this);

        binding.includedToolbar.aviProgressToolbar.show();
        binding.buttonUpdateProfile.setEnabled(false);
        binding.editTextMobileNumber.setNumber("+94");
        authManager.getUserDetails(new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser user, List<Object> params) {
                System.out.println("fgkwsgfkgfgfk " + user.getId());
                binding.editTextMobileNumber.setNumber(user.getMobileNumber());
                binding.setUser(user);
                binding.spinnerGender.setSelection(Arrays.asList(getResources()
                        .getStringArray(R.array.gender))
                        .indexOf(WordUtils.capitalize(user.getGender().toLowerCase())));
                binding.spinnerLanguage.setSelection(Arrays.asList(getResources()
                        .getStringArray(R.array.language))
                        .indexOf(WordUtils.capitalize(user.getLanguage().toLowerCase())));
                binding.includedToolbar.aviProgressToolbar.hide();
                //binding.textViewLoginWith.setText(Application);
                binding.buttonUpdateProfile.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                binding.includedToolbar.aviProgressToolbar.hide();
                Utils.Error.onServiceCallFail(UpdateUserActivity.this, t);
            }
        });

        binding.buttonBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.buttonBirthday.getText().toString().equals("-")) {
                } else {
                    binding.buttonBirthday.setText("-");
                    birthDate = "-";
                }

            }
        });

        binding.buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.editTextMobileNumber.getPhoneNumber() == null
                        || "".equals(binding.editTextMobileNumber.getPhoneNumber())) {
                    Utils.Dialog.showOKDialog(UpdateUserActivity.this,
                            "Invalid mobile number. Please enter a valid mobile number.");
                    return;
                }
                final User user = new User();
                Phonenumber.PhoneNumber phoneNumber = binding.editTextMobileNumber.getPhoneNumber();
                user.setMobileNumber("+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber());
                user.setCountryCode(binding.editTextMobileNumber.getSelectedCountry().getIso().toUpperCase());
                user.setDateOfBirth(birthDate);
                user.setGender(AuthOptions.Gender.valueOf
                        (binding.spinnerGender.getSelectedItem().toString().toUpperCase()));
                user.setLanguage(AuthOptions.Language.valueOf
                        (binding.spinnerLanguage.getSelectedItem().toString().toUpperCase()));

                final Pattern mobileNumberPattern = Pattern.compile("^\\+[1-9]{1}[0-9]{3,14}$");
                if (!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
                    Utils.Dialog.createOKDialog(UpdateUserActivity.this,
                            "Invalid mobile number. Please enter a valid mobile number.", null);
                    return;
                }


                binding.includedToolbar.aviProgressToolbar.show();
                authManager.updateUserDetails(user, new APIListener<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser result, List<Object> params) {
                        AuthUser authUser = ((Application) getApplication()).getAuthUser();
                        authUser.setGender(result.getGender().toString().toLowerCase());
                        authUser.setDateOfBirth((result.getDateOfBirth()));
                        authUser.setMobileNumber(result.getMobileNumber());
                        authUser.setLanguage(result.getLanguage().toString().toLowerCase());
                        // TODO: Remove country code        authUser.setCountryCode(result.getCountryCode().toString().toUpperCase());
                        authUser.setMobileNumberVerified(result.isMobileNumberVerified());
                        binding.includedToolbar.aviProgressToolbar.hide();

                        if (!authUser.isMobileNumberVerified()) {
                            unVerified = true;

                            //Removed SMS Verification
                            unVerified = false;
                            finish();
                            return;

//                            new MaterialDialog.Builder(UpdateUserActivity.this)
//                                    .title(R.string.app_name)
//                                    .content("Please enter the SMS verification code S:")
//                                    .inputType(InputType.TYPE_CLASS_NUMBER)
//                                    .inputRange(0, 4)
//                                    .cancelable(false)
//                                    .canceledOnTouchOutside(false)
//                                    .input("", "", new MaterialDialog.InputCallback() {
//                                        @Override
//                                        public void onInput(MaterialDialog dialog, CharSequence input) {
//                                            if(input == null || "".equals(input.toString())){
//                                                Utils.Dialog.createOKDialog(UpdateUserActivity.this, "Invalid verification code. Your mobile number is unverified. Please login and verify it again", new MaterialDialog.SingleButtonCallback() {
//                                                    @Override
//                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                        AuthManager.logoutFromApp((Application) getApplication());
//                                                        Intent loginIntent = new Intent(UpdateUserActivity.this, SplashActivity.class);
//                                                        startActivity(loginIntent);
//                                                        finish();
//                                                    }
//                                                });
//                                            }else{
//                                                authManager.verifyUser(Integer.parseInt(input.toString()), new APIListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void result, List<Object> params) {
//                                                        unVerified = false;
//                                                        Utils.Dialog.createOKDialog(UpdateUserActivity.this, "Your details has been updated successfully!", new MaterialDialog.SingleButtonCallback() {
//                                                            @Override
//                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                finish();
//                                                            }
//                                                        });
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Throwable t) {
//                                                        if (t instanceof ApplicationException) {
//                                                            Utils.Dialog.createOKDialog(UpdateUserActivity.this, "Invalid verification code. Your mobile number is unverified. Please login and verify it again", new MaterialDialog.SingleButtonCallback() {
//                                                                @Override
//                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                    AuthManager.logoutFromApp((Application) getApplication());
//                                                                    Intent loginIntent = new Intent(UpdateUserActivity.this, SplashActivity.class);
//                                                                    startActivity(loginIntent);
//                                                                    finish();
//                                                                }
//                                                            }, new MaterialDialog.SingleButtonCallback() {
//                                                                @Override
//                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                    AuthManager.logoutFromApp((Application) getApplication());
//                                                                    Intent loginIntent = new Intent(UpdateUserActivity.this, SplashActivity.class);
//                                                                    startActivity(loginIntent);
//                                                                    finish();
//                                                                }
//                                                            }, new DialogInterface.OnCancelListener() {
//                                                                @Override
//                                                                public void onCancel(DialogInterface dialog) {
//                                                                    AuthManager.logoutFromApp((Application) getApplication());
//                                                                    Intent loginIntent = new Intent(UpdateUserActivity.this, SplashActivity.class);
//                                                                    startActivity(loginIntent);
//                                                                    finish();
//                                                                }
//                                                            });
//                                                        } else {
//                                                            Utils.Error.onServiceCallFail(UpdateUserActivity.this, t);
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }).show();


                        } else {
                            Utils.Dialog.createOKDialog(UpdateUserActivity.this, "Your details has been updated successfully!", new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        binding.includedToolbar.aviProgressToolbar.hide();
                        Utils.Error.onServiceCallFail(UpdateUserActivity.this, t);
                    }
                });
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

    @Override
    protected void onPause() {
        super.onPause();
        if (unVerified) {
            AuthManager.logoutFromApp((Application) getApplication());
            Intent loginIntent = new Intent(UpdateUserActivity.this, SplashActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }




}
