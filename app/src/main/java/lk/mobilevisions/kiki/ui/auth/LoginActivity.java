package lk.mobilevisions.kiki.ui.auth;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;


import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.databinding.ActivityLoginBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;
import lk.mobilevisions.kiki.modules.auth.exceptions.SocialAuthenticationFailedException;
//import lk.mobilevisions.kiki.modules.auth.twitter.TwitterUtil;
//import lk.mobilevisions.kiki.ui.channels.ChannelsActivity;
import lk.mobilevisions.kiki.ui.base.BaseActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import lk.mobilevisions.kiki.video.activity.VideoDashboardActivity;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private static final int RC_SIGN_IN = 10001;
    private static final int TWITTER_SIGN_IN = 10002;
    private GoogleApiClient googleApiClient;
    public CallbackManager fbCallbackManager;
    private String selectedGender = "Male";
    ActivityLoginBinding binding;
    @Inject
    AuthManager authManager;

    String type;
    String contentType;
    String contentId;
    String typeB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        ((Application) getApplication()).getInjector().inject(this);
//        if (((Application) getApplication()).getInjector()!=null){
//            ((Application) getApplication()).getInjector().inject(this);
//        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }

        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "LoginActivity", null);

        FacebookSdk.sdkInitialize(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestIdToken("638746862442-vdj7ek6fshn1em1loc4aheg8f95ho1q8.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fbCallbackManager = CallbackManager.Factory.create();

        Application.BUS.register(this);

        viewPager = (ViewPager) findViewById(R.id.pagerLogin);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        AuthUser authUser = ((Application) getApplication()).getAuthUser();
        if (authUser != null) {
            if (isMobileNumberVerified(authUser)) {
                goToMobileNumberScreen();
            } else {
                goToHomeScreen();
            }

        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
            }
        });

        type = getIntent().getStringExtra("type");
        contentType = getIntent().getStringExtra("content_type");
        contentId = getIntent().getStringExtra("content_id");


//        typeB = getIntent().getStringExtra("typeB");
//        System.out.println("sdhkfbsdjhfbsdh " + typeB);
    }

    @Override
    protected void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onUserNavigateBack(SocialAuthFragment.UserNavigatesBack event) {
        finish();

    }

    @Subscribe
    public void onUserNavigateBack(UserLoginFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(0, false);

    }

    @Subscribe
    public void onUserNavigateBack(UserRegisterFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(0, false);

    }

    @Subscribe
    public void onUserNavigateBack(MobileNumberFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        finish();
//        viewPager.setCurrentItem(1, false);

    }

    @Subscribe
    public void onUserCustomLogin(SocialAuthFragment.UserCustomLogin event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(2, false);

    }

    @Subscribe
    public void onUserCustomLogin(UserRegisterFragment.UserCustomLogin event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(2, false);

    }

    @Subscribe
    public void onUserCustomRegister(SocialAuthFragment.UserCustomRegister event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(1, false);

    }

    @Subscribe
    public void onUserCustomRegister(UserLoginFragment.UserCustomRegister event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(1, false);

    }

    @Subscribe
    public void onUserForgotPassword(UserLoginFragment.UserCustomForgetPassword event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(4, false);

    }

    @Subscribe
    public void onUserNavigateBack(ForgotPasswordMobileFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(2, false);

    }


    @Subscribe
    public void onUserPasswordReset(PasswordResetOtpFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(4, false);

    }

//    @Subscribe
//    public void onUserPasswordReset(PasswordResetOtpFragment.UserResetPassword event) {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        viewPager.setCurrentItem(3, false);
//
//    }

    @Subscribe
    public void onUserNewPasswordReset(NewPasswordResetFragment.UserNavigatesBack event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(5, false);

    }


    @Subscribe
    public void onLoginButtonsClick(LoginOptionsEvent event) {
        switch (event.getOptions()) {
            case CUSTOM:
                viewPager.setCurrentItem(2, false);
                break;
            case FACEBOOK:
                System.out.println("facebook login checkin 44444 ");
                User user = new User();
                user.setProvider(AuthOptions.AuthMethod.FACEBOOK);
                user.setSocialAccessToken(((LoginResult)
                        event.getParams().get(UserAuthFragment.PARAM_LOGIN_RESULT))
                        .getAccessToken().getToken());
                user.setSession(((Application) getApplication()).getSession());
                final MaterialDialog progressDialog = createProgressDialog();
                if (((Activity) LoginActivity.this).hasWindowFocus()) {
                    progressDialog.show();
                }
                authManager.loginWithFacebook(user, new APIListener<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser, List<Object> params) {
                        System.out.println("facebook login checkin 777777  ");
                        boolean isNewUser = (boolean) params.get(0);
                        ((Application) getApplication()).setAuthUser(authUser);
                        AuthManager.loginToApp(authUser.getAccessToken());
                        if (isNewUser) {
                            System.out.println("facebook login checkin 888888  ");
                            goToMobileNumberScreen();
                        } else {
                            System.out.println("facebook login checkin 999999  ");
                            if (isMobileNumberVerified(authUser)) {
                                goToHomeScreen();
                            } else {
                                goToHomeScreen();
                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println("facebook login checkin 6666  " + t);
                        progressDialog.dismiss();
                        onLoginFailure(t);
                    }
                });
                break;
            case GOOGLE:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            default:
        }
    }


    @Subscribe
    public void onUserLoginAuthenticate(UserLoginFragment.UserLoginAuthEvent event) {
        User user = event.getUser();
        user.setProvider(AuthOptions.AuthMethod.CUSTOM);
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }

        //User login
        if ((event.getUser().getUsername() == null || event.getUser().getUsername().equals("")) && (event.getUser().getPassword() == null || event.getUser().getPassword().equals(""))) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.username_and_password_cannot_be_empty));
            progressDialog.dismiss();
            return;
        } else if (event.getUser().getUsername() == null || event.getUser().getUsername().equals("")) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.username_cannot_be_empty));
            progressDialog.dismiss();
            return;
        } else if (event.getUser().getPassword() == null || event.getUser().getPassword().equals("")) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.password_cannot_be_empty));
            progressDialog.dismiss();
            return;
        }

        authManager.loginWithUsername(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser authUser, List<Object> params) {
                ((Application) getApplication()).setAuthUser(authUser);
                AuthManager.loginToApp(authUser.getAccessToken());
                progressDialog.dismiss();
                if (isMobileNumberVerified(authUser)) {
                    goToMobileNumberScreen();
                } else {
                    goToHomeScreen();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });

    }

    @Subscribe
    public void onUserRegisterAuthenticate(UserRegisterFragment.UserRegisterAuthEvent event) {

        User user = event.getUser();
        user.setSession(((Application) this.getApplication()).getSession());
        user.setProvider(AuthOptions.AuthMethod.CUSTOM);
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        //User register
        String email = (String) event.getParams().get(UserRegisterFragment.EMAIL);
        if ((event.getUser().getUsername() == null || event.getUser().getUsername().equals("")) && (event.getUser().getPassword() == null || event.getUser().getPassword().equals(""))) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.username_and_password_cannot_be_empty));
            progressDialog.dismiss();
            return;
        } else if (event.getUser().getUsername() == null || event.getUser().getUsername().equals("")) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.username_cannot_be_empty));
            progressDialog.dismiss();
            return;
        } else if (event.getUser().getPassword() == null || event.getUser().getPassword().equals("")) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.password_cannot_be_empty));
            progressDialog.dismiss();
            return;
        } else if (!isValidPassword(event.getUser().getPassword().trim())) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.invalid_password));
            progressDialog.dismiss();
            return;
        }

        String confirmPassword = (String) event.getParams().get(UserAuthFragment.CONFIRM_PASSWORD_VALUE);
        if (confirmPassword == null || !confirmPassword.equals(event.getUser().getPassword())) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.invalid_password_confirmation));
            progressDialog.dismiss();
            return;
        }


        authManager.registerWithUsername(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser authUser, List<Object> params) {
                ((Application) getApplication()).setAuthUser(authUser);
                AuthManager.loginToApp(authUser.getAccessToken());
                progressDialog.dismiss();
                goToMobileNumberScreen();
            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });

    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Subscribe
    public void onUserMobileNumberUpdate(MobileNumberFragment.UserMobileNumberEvent event) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final User user = event.getUser();
        Date date = new Date();
        String birthDate = dateFormat.format(date);
        user.setDateOfBirth(birthDate);
        user.setGender(AuthOptions.Gender.valueOf
                (selectedGender.toUpperCase()));
        String selectedLanguage = Utils.SharedPrefUtil.getStringFromSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, null);
        user.setLanguage(AuthOptions.Language.valueOf
                (selectedLanguage.toUpperCase()));
        final Phonenumber.PhoneNumber phoneNumber = event.getPhoneNumber();
        final Pattern mobileNumberPattern = Pattern.compile("^\\+[1-9]{1}[0-9]{3,14}$");
        if (!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
            Utils.Dialog.createOKDialog(this, getString(R.string.invalid_mobile_number), null);
            return;
        }

        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        authManager.updateUserMobileNumber(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser result, List<Object> params) {
                AuthUser authUser = ((Application) getApplication()).getAuthUser();
                authUser.setMobileNumber(result.getMobileNumber());
                authUser.setLanguage(result.getLanguage().toString().toLowerCase());
                // TODO: Enable country code     authUser.setCountryCode(result.getCountryCode().toString().toUpperCase());
                authUser.setMobileNumberVerified(result.isMobileNumberVerified());
                progressDialog.dismiss();
                goToHomeScreen();
            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });
    }

    @Subscribe
    public void onUserVerifyMobileNumber(ForgotPasswordMobileFragment.UserMobileNumberEvent event) {
        final String mobileNumber = event.getMobileNumber();

        System.out.println("en2ejkwnjkenjkernj 555555");
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        authManager.sendUserMobileNumber(mobileNumber, new APIListener<ForgotPasswordUser>() {
            @Override
            public void onSuccess(ForgotPasswordUser result, List<Object> params) {
                System.out.println("en2ejkwnjkenjkernj 66666");
                if(result.isStatus()){
                    System.out.println("en2ejkwnjkenjkernj 77777");
                    Application.getInstance().setForgotPasswordUser(result);
                    progressDialog.dismiss();
                    goToResetOtpScreen();
                    Application.BUS.post(new SetUserNameEvent());
                }else{
                    Utils.Dialog.createOKDialog(LoginActivity.this, getString(R.string.invalid_mobile_number), null);
                    progressDialog.dismiss();
                    System.out.println("en2ejkwnjkenjkernj 98989898");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);


            }
        });
    }

    @Subscribe
    public void onUserVerifyOtpNumber(PasswordResetOtpFragment.UserOtpRequest event) {
        final String otp = event.getOtp();
        int viwerid =Application.getInstance().getForgotPasswordUser().getViwerId();

        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        authManager.forgotPasswordOtp(viwerid,otp, new APIListener<ForgotPasswordUser>() {
            @Override
            public void onSuccess(ForgotPasswordUser result, List<Object> params) {
                progressDialog.dismiss();
                if(result.isStatus()){
                    goToResetPasswordScreen();
                }else{
                    Utils.Dialog.createOKDialog(LoginActivity.this, getString(R.string.invalid_otp), null);

                }


            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });
    }
    @Subscribe
    public void onUserResendOtp(PasswordResetOtpFragment.UserResetPassword event) {
        String mobileNumber = Application.getInstance().getForgotPasswordUser().getMobileNo();
        String newMobileNumber ="";

        System.out.println("mobile" + mobileNumber);

        if(mobileNumber.contains("+")){
            System.out.println("mobile 12121212");
            newMobileNumber = mobileNumber.substring(1,mobileNumber.length());

        }else {
            newMobileNumber=mobileNumber;
        }
        System.out.println("mobile" + newMobileNumber);

        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        authManager.sendUserMobileNumber(newMobileNumber, new APIListener<ForgotPasswordUser>() {
            @Override
            public void onSuccess(ForgotPasswordUser result, List<Object> params) {
                System.out.println("Resend 123123");
                Application.getInstance().setForgotPasswordUser(result);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });
    }
    @Subscribe
    public void onUserNewPasswordReset(NewPasswordResetFragment.UserNewPasswordEvent event) {
        final String password = event.getPassword();
        final String confirmPassword = event.getConfirmPassword();
        int viwerid = Application.getInstance().getForgotPasswordUser().getViwerId();
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) LoginActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        if (password == null || password.equals("")) {
            System.out.println("awfdadfsdvcsdv 143143333");
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.password_cannot_be_empty));

            progressDialog.dismiss();
            return;
        } else if (!isValidPassword(password.trim())) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.invalid_password));
            System.out.println("awfdadfsdvcsdv 143143");
            progressDialog.dismiss();
            return;
        }


        if (confirmPassword == null || !confirmPassword.equals(password)) {
            Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.invalid_password_confirmation));
            System.out.println("awfdadfsdvcsdv 123123");
            progressDialog.dismiss();
            return;
        }


        authManager.updateNewResetPassword(viwerid,password, new APIListener<ForgotPasswordUser>() {
            @Override
            public void onSuccess(ForgotPasswordUser result, List<Object> params) {
                if(result.isStatus()){
                    Application.getInstance().setForgotPasswordUser(result);
                    progressDialog.dismiss();
                    goToResetPasswordComplete();
                }else{
                    Utils.Dialog.createOKDialog(LoginActivity.this, getString(R.string.invalid_password_server), null);

                }

            }

            @Override
            public void onFailure(Throwable t) {
                onLoginFailure(t);
                progressDialog.dismiss();
            }
        });
    }
    public class SetUserNameEvent {
    }
    @Subscribe
    public void onUserNewPasswordReset(ResetPasswordCompleteFragment.UserNavigateToLogin event) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(2, false);
    }



    private void onLoginFailure(Throwable t) {
        String message = "Fatal Error";
        boolean showDialog = true;

        if (t instanceof AuthenticationFailedException ||
                t instanceof AuthenticationFailedWithAccessTokenException ||
                t instanceof RemoteServerException ||
                t instanceof SocialAuthenticationFailedException ||
                t instanceof ApplicationException ||
                t instanceof InvalidResponseException ||
                t instanceof ErrorResponseException) {
            message = t.toString();
        } else {
            message = getString(R.string.network_problem_message);
        }

        if (t instanceof AuthenticationFailedWithAccessTokenException) {
            showDialog = false;
        }

        Timber.d("Login Failure : %s", message);

        if (showDialog) {
            if (message.equals("Invalid credentials.")) {
                Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.invalid_credentials));
            } else if (message.equals("Username already taken.")) {
                Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.username_already_taken));
            } else if (message.equals("User already registered.")) {
                Utils.Dialog.showOKDialog(LoginActivity.this, getString(R.string.user_already_registerd));
            } else {
                Utils.Dialog.showOKDialog(LoginActivity.this, message);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                User user = new User();
                user.setProvider(AuthOptions.AuthMethod.GOOGLE);
                user.setSocialAccessToken(account.getIdToken());
                user.setSession(((Application) getApplication()).getSession());
                final MaterialDialog progressDialog = createProgressDialog();
                if (((Activity) LoginActivity.this).hasWindowFocus()) {
                    progressDialog.show();
                }
                authManager.loginWithGoogle(user, new APIListener<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser, List<Object> params) {
                        boolean isNewUser = (boolean) params.get(0);
                        ((Application) getApplication()).setAuthUser(authUser);
                        AuthManager.loginToApp(authUser.getAccessToken());
                        if (isNewUser) {
                            goToMobileNumberScreen();
                        } else {
                            if (isMobileNumberVerified(authUser)) {
                                goToMobileNumberScreen();
                            } else {
                                goToHomeScreen();
                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                        onLoginFailure(t);
                    }
                });
                Timber.d("Google Sign in success");
            } else {
                Timber.d("Google Sign in failed");
            }
        }
        if (requestCode == TWITTER_SIGN_IN) {

        } else {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void goToMobileNumberScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewPager.setCurrentItem(3, false);
    }

    private void goToResetOtpScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewPager.setCurrentItem(5, false);
    }

    private void goToResetPasswordScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewPager.setCurrentItem(6, false);
    }

    private void goToResetPasswordComplete() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewPager.setCurrentItem(7, false);
    }


    private void goToHomeScreen() {
        System.out.println("uguguugugguguguguuu 1515 ");
        typeB = getIntent().getStringExtra("typeB");
        String programID = getIntent().getStringExtra("programID");
        String contentType = getIntent().getStringExtra("contentType");

        Intent intent = null;
        String currentApp = Utils.SharedPrefUtil.getStringFromSharedPref(LoginActivity.this,Constants.CURRENT_APP,null);
        if (typeB !=null) {
            System.out.println("sdhkfbsdjhfbsdh 222222" + typeB);
            if (typeB.equals("0")) {
                intent = new Intent(LoginActivity.this, VideoDashboardActivity.class);
                intent.putExtra("programType",typeB);
                intent.putExtra("programId",programID);
                intent.putExtra("contentType",contentType);
            } else {
                intent = new Intent(LoginActivity.this, AudioDashboardActivity.class);
                intent.putExtra("programType",typeB);
                intent.putExtra("programId",programID);
                intent.putExtra("contentType",contentType);
            }
        }
        else {
            if (currentApp != null && currentApp.equals("audio")) {
                intent = new Intent(LoginActivity.this, AudioDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, VideoDashboardActivity.class);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
//                if(currentApp!=null && currentApp.equals("audio")) {
//                    intent = new Intent(LoginActivity.this, AudioDashboardActivity.class);
//                }else {
//                    intent = new Intent(LoginActivity.this, VideoDashboardActivity.class);
//                }
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
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


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, false);
        } else if (viewPager.getCurrentItem() == 2) {
            viewPager.setCurrentItem(0, false);
        } else if (viewPager.getCurrentItem() == 4) {
            viewPager.setCurrentItem(2, false);
        } else if (viewPager.getCurrentItem() == 5) {
            viewPager.setCurrentItem(4, false);
        } else if (viewPager.getCurrentItem() == 6) {
            viewPager.setCurrentItem(5, false);
        } else {
            Intent intent = new Intent(this, LanguageSelectionActivity.class);
            startActivity(intent);
            finish();
            Application.BUS.post(new UserNavigatesBack());
            super.onBackPressed();
        }

    }


    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private boolean isMobileNumberVerified(AuthUser authUser) {
        return authUser.getMobileNumber() == null || authUser.getMobileNumber().equals("");
    }

    public class UserNavigatesBack {
    }
}
