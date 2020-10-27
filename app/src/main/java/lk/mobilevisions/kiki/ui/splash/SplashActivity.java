package lk.mobilevisions.kiki.ui.splash;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.microsoft.applicationinsights.library.TelemetryClient;
import com.uxcam.UXCam;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import lk.mobilevisions.kiki.BuildConfig;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.app.Utils;
//import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;


import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.databinding.ActivitySplashBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.info.InfoManager;

import lk.mobilevisions.kiki.ui.auth.LanguageSelectionActivity;
import lk.mobilevisions.kiki.ui.auth.LoginActivity;
import lk.mobilevisions.kiki.ui.main.MainActivity;
import lk.mobilevisions.kiki.video.activity.VideoDashboardActivity;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {


    @Inject
    AuthManager authManager;

    @Inject
    InfoManager infoManager;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    ActivitySplashBinding binding;

    String token;
    String type;
    String contentType;
    String contentId;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        System.out.println("uguguugugguguguguuu 11111 ");
        FacebookSdk.sdkInitialize(getApplicationContext());

        Utils.SharedPrefUtil.saveBooleanToSharedPref(SplashActivity.this,
                Constants.COMING_FROM_SPLASH_SCREEN, true);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.imageViewLogo.setVisibility(View.VISIBLE);
            }
        }, 9000);
        UXCam.startWithKey("znryb2dqfklg85b");

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new Instance ID token
                token = task.getResult().getToken();
                System.out.println("afjbsdjhbvjsdvsdgkv " + token);

            }
        });

//        if (!isTaskRoot()
//                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
//                && getIntent().getAction() != null
//                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
//            finish();
//            return;
//        }

        type = getIntent().getStringExtra("type");
        System.out.println("fsdvsdvsdd " + type);
        contentType = getIntent().getStringExtra("content_type");
        System.out.println("fsdvsdvsdd 11111" + contentType);
        contentId = getIntent().getStringExtra("content_id");
        System.out.println("fsdvsdvsdd 22222" + contentId);

//        checkForDynamicLinks();

//            Intent intent;
//        if (type !=null && type.equals("0")){
//             intent = new Intent(SplashActivity.this, VideoDashboardActivity.class);
//            startActivity(intent);
//            finish();
//
//        } else if (type !=null && type.equals("1")){
//             intent = new Intent(SplashActivity.this, AudioDashboardActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

//    private void checkForDynamicLinks() {
//        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//            @Override
//            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//
//                Uri deepLink = null;
//                if (pendingDynamicLinkData != null) {
//                    deepLink = pendingDynamicLinkData.getLink();
//                    System.out.println("sfgdgdf " + deepLink);
//                }
//
//                if (deepLink != null){
//
//                }
//                Intent intent = null;
//                intent = new Intent(SplashActivity.this, AudioDashboardActivity.class);
//            }
//        }).addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//    }

    @Override
    protected void onStart() {
        init();
        super.onStart();
//        checkForDynamicLinks();
    }

    private void init() {
        System.out.println("uguguugugguguguguuu 2222 ");
        if (!checkPlayServices()) {
            Utils.Dialog.createOKDialog(SplashActivity.this, getString(R.string.google_play_service_unavilable),
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    });
            return;
        } else {
            System.out.println("uguguugugguguguguuu 33333 ");
            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.ALL_USERS_TOPIC_NAME);
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                    new FirebaseRemoteConfigSettings.Builder()
                            .setDeveloperModeEnabled(true)
                            .build();
            firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);

            long cacheExpiration = Settings.Firebase.CACHE_EXPIRATION_TIME;

            firebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseRemoteConfig.activateFetched();

                            Utils.App.getConfig(getApplication())
                                    .setServerURL(firebaseRemoteConfig.getString(Settings.MAIN_SERVER_API_NAME + Settings.MAIN_SERVER_API_VERSION));
                            Utils.App.getConfig(getApplication())
                                    .setPackageName(firebaseRemoteConfig.getString(Constants.MAIN_PACKAGE_NAME));
                            Utils.App.getConfig(getApplication())
                                    .setTwitterConsumerKey(firebaseRemoteConfig.getString(Constants.TWITTER_CONSUMER_KEY));
                            Utils.App.getConfig(getApplication())
                                    .setTwitterConsumerSecret(firebaseRemoteConfig.getString(Constants.TWITTER_CONSUMER_SECRET));
                            Utils.App.getConfig(getApplication())
                                    .setTwitterCallbackURL(firebaseRemoteConfig.getString(Constants.TWITTER_CALLBACK_URL));
                            Utils.App.getConfig(getApplication())
                                    .setMenuFacebookPage(firebaseRemoteConfig.getString(Constants.MENU_FACEBOOK_PAGE_URL));
                            Utils.App.getConfig(getApplication())
                                    .setUserAgreementPage(firebaseRemoteConfig.getString(Constants.USER_AGREEMENT_PAGE_URL));
                            Utils.App.getConfig(getApplication())
                                    .setPaidPackageId(firebaseRemoteConfig.getLong(Constants.PAID_PACKAGE_ID));
                            Utils.App.getConfig(getApplication())
                                    .setFaqURL(firebaseRemoteConfig.getString(Constants.FAQ_URL));
                            Utils.App.getConfig(getApplication())
                                    .setContactTelephone(firebaseRemoteConfig.getString(Constants.CONTACT_TELEPHONE));
                            Utils.App.getConfig(getApplication())
                                    .setContactEmail(firebaseRemoteConfig.getString(Constants.CONTACT_EMAIL));
                            Utils.App.getConfig(getApplication())
                                    .setMobilePaymentGatewayURL(firebaseRemoteConfig.getString(Settings.MOBILE_PAYMENT_GATEWAY_NAME));
                            Utils.App.getConfig(getApplication())
                                    .setShareURL(firebaseRemoteConfig.getString(Constants.SHARE_URL));
                            Utils.App.getConfig(getApplication())
                                    .setScreenCaptureDisabled(firebaseRemoteConfig.getBoolean(Constants.DISABLE_SCREEN_CAPTURING));
                            Utils.App.getConfig(getApplication())
                                    .setDefaultVideoResolution(firebaseRemoteConfig.getLong(Constants.DEFAULT_VIDEO_RESOLUTION));

                            ((Application) getApplication()).initializeApp();
                            ((Application) getApplication()).getInjector().inject(SplashActivity.this);
                            System.out.println("uguguugugguguguguuu 4444 ");
                            checkHeaders();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.e(e);
                            Utils.Dialog.createOKDialog(SplashActivity.this, getString(R.string.network_problem_message),
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            finish();
                                        }
                                    });
                        }
                    });

        }
    }


    private void checkHeaders() {
        System.out.println("uguguugugguguguguuu 55555 ");
        infoManager.getHeaders(new APIListener<HeaderResponse>() {
            @Override
            public void onSuccess(HeaderResponse result, List<Object> params) {
                ((Application) getApplication()).getSession().setHeaderResponse(result);
                loginToApp();
            }

            @Override
            public void onFailure(Throwable t) {
                loginToApp();
            }
        }, getApplicationContext());
    }

    public void loginToApp() {
        System.out.println("uguguugugguguguguuu 66666 ");
//        goToHomeScreen();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                authManager.loginWithAccessToken(new APIListener<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser, List<Object> params) {
                        System.out.println("uguguugugguguguguuu 777777 ");
                        ((Application) getApplication()).setAuthUser(authUser);
                        AuthManager.loginToApp(authUser.getAccessToken());
                        System.out.println("firebasehvjh"+authUser.getAccessToken());
                        Log.d("firebase token:- ",authUser.getAccessToken());
                        if (authUser != null) {
                            System.out.println("uguguugugguguguguuu 88888 ");
                            if (isMobileNumberVerified(authUser)) {
                                System.out.println("uguguugugguguguguuu 99999 ");
                                goToLoginScreen();
                            } else if(isDetailsVerified(authUser)){
                                System.out.println("uguguugugguguguguuu 101010 ");
                                goToLoginScreen();
                            }else{
                                System.out.println("uguguugugguguguguuu 1212121 ");
                                goToHomeScreen();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof NoInternetConnectionException) {
                            Utils.Dialog.createOKDialog(SplashActivity.this, getString(R.string.network_problem_message),
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            finish();
                                        }
                                    });
                            return;
                        } else {
                            goToLoginScreen();
                        }
                    }
                });
            }
        }, Settings.SplashScreen.SPLASH_TIMER);
    }


    private void goToHomeScreen() {

//        System.out.println("scsdcsdc " + type);
//
//        if (type !=null) {
//            System.out.println("scsdcsdc " + type);
//        }
//        else {
//            System.out.println("scsdcsdc 1111");
//        }

        System.out.println("uguguugugguguguguuu");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                String currentApp = Utils.SharedPrefUtil.getStringFromSharedPref(SplashActivity.this,Constants.CURRENT_APP,null);

                if (type !=null) {
                    if (type.equals("0")) {
                        intent = new Intent(SplashActivity.this, VideoDashboardActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, AudioDashboardActivity.class);
                    }
                }
                else {
                    if (currentApp != null && currentApp.equals("audio")) {
                        intent = new Intent(SplashActivity.this, AudioDashboardActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, VideoDashboardActivity.class);
                    }
            }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    private void goToLoginScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               String selectedLanguage = Utils.SharedPrefUtil.getStringFromSharedPref(SplashActivity.this,Constants.KEY_LANGUAGE_PHONE,null);
               if(selectedLanguage!= null){
                   System.out.println("uguguugugguguguguuu 13131 ");

                   Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                   intent.putExtra("typeB",type);
                   intent.putExtra("programID",contentId);
                   intent.putExtra("contentType",contentType);
                   startActivity(intent);
                   finish();
               }else{
                   System.out.println("uguguugugguguguguuu 141414 ");
                   Intent intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                   startActivity(intent);
                   finish();
               }

            }
        }, 1500);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private boolean isMobileNumberVerified(AuthUser authUser) {
        return authUser.getMobileNumber() == null || authUser.getMobileNumber().equals("");

    }
    private boolean isDetailsVerified(AuthUser authUser) {
        return authUser.getDateOfBirth() == null || authUser.getDateOfBirth().equals("");
    }
}
