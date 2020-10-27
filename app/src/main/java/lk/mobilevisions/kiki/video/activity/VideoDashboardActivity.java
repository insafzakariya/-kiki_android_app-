package lk.mobilevisions.kiki.video.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.databinding.ActivityVideoDashboardBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.notifications.NotificationManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.ui.base.BaseActivity;
import lk.mobilevisions.kiki.ui.notifications.NotificationsActivity;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import lk.mobilevisions.kiki.video.fragment.VideoHomeFragment;
import lk.mobilevisions.kiki.video.fragment.VideoMylistFragment;

public class VideoDashboardActivity extends BaseActivity {

    @Inject
    NotificationManager notificationManager;
    @Inject
    SubscriptionsManager subscriptionsManager;

    ActivityVideoDashboardBinding binding;

    private VideoHomeFragment fragmentOne;
    private VideoMylistFragment fragmentTwo;
    private boolean isHomeLoaded;
    private boolean isMylistLoaded;
    private ActionBarDrawerToggle drawerToggle;
    private TextView textViewHeader;

    private String alertTitle;
    private String messageBody;
    private boolean trialStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_dashboard);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoDashboardActivity", null);


        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                String name = null;
                if (((Application) getApplication()).getAuthUser() != null
                        && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
//                    textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.welcome_textview);
//                    textViewHeader = (TextView) binding.welcomeTextview;
                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }
                    binding.welcomeTextview.setText(getString(R.string.welcome_user, name));
                }


            }
        };
        binding.drawerLayout.addDrawerListener(drawerToggle);
        bindWidgetsWithAnEvent();
        setupTabLayout();
//        checkTrialStatus();
        binding.subLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (trialStatus) {
//                    Intent intentPackages = new Intent(VideoDashboardActivity.this, VideoTrialActivationActivity.class);
//                    startActivity(intentPackages);
//                    binding.drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
                    Intent intentPackages = new Intent(VideoDashboardActivity.this, PaymentActivity.class);
                    startActivity(intentPackages);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
//                }
            }
        });

        binding.settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(VideoDashboardActivity.this, VideoSettingsActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        binding.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(VideoDashboardActivity.this, ContactActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        binding.childLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(VideoDashboardActivity.this, VideoChildModeActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLogoutDialog();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


//        binding.navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setCheckable(false);
//                        selectDrawerItem(menuItem);
//                        return true;
//                    }
//                });

        binding.includedToolbar.navigationImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        binding.includedToggle.musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibe.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibe.vibrate(50);
                }


                binding.includeVideoDashboard.aviProgressRecycler.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(VideoDashboardActivity.this, AudioDashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        binding.includeVideoDashboard.aviProgressRecycler.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);

            }
        });

        String name = null;
        if (((Application) getApplication()).getAuthUser() != null
                && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
//            textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.welcome_textview);
            if (name.contains(" ")) {
                name = name.substring(0, name.indexOf(" "));
            }
            binding.welcomeTextview.setText(getString(R.string.welcome_user, name));
        }

        Utils.SharedPrefUtil.saveStringToSharedPref(this,
                Constants.CURRENT_APP, "video");

        binding.includedToolbar.notificationImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNotifications = new Intent(VideoDashboardActivity.this, NotificationsActivity.class);
                startActivity(intentNotifications);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                System.out.println("audioDeviceId " + token);

                notificationManager.sendDeviceId(token, new APIListener<Void>() {
                    @Override
                    public void onSuccess(Void result, List<Object> params) {

//                        Toast.makeText(getApplicationContext(), "API Success", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });


            }
        });
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Video_Screen");

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

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navPackages:
                Intent intentPackages = new Intent(this, PaymentActivity.class);
                startActivity(intentPackages);
                break;
            case R.id.navContact:
                Intent contactIntent = new Intent(this, ContactActivity.class);
                startActivity(contactIntent);
                break;
            case R.id.navSettings:
                Intent settingsIntent = new Intent(this, VideoSettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.navChild:
                Intent childIntent = new Intent(this, VideoChildModeActivity.class);
                startActivity(childIntent);
                break;
            case R.id.navLogout:
                createLogoutDialog();

        }

        binding.drawerLayout.closeDrawers();

    }

    private void checkTrialStatus() {
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(this.getApplication()).getPaidPackageId(),
                new APIListener<PackageToken>() {
                    @Override
                    public void onSuccess(final PackageToken packageToken, List<Object> params) {

                        subscriptionsManager.getTrialStatus(new APIListener<PackageV2>() {
                            @Override
                            public void onSuccess(PackageV2 thePackage, List<Object> params) {
//                                binding.aviProgress.setVisibility(View.GONE);
                                alertTitle = thePackage.getTitleText();
                                messageBody = thePackage.getMessageBody();
                                trialStatus = thePackage.isTrialStatus();
                                System.out.println("hdfhdfhdh " + alertTitle);
                                System.out.println("hdfhdfhdh 111  " + messageBody);
//                        if (thePackage.getPackageId() == 46 || thePackage.getPackageId() == 81 ||
//                                thePackage.getPackageId() == 101 || thePackage.getPackageId() == 106) {
//
//                        } else {
//                            try {
//                                subscriptionDialog();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }

//                                if (thePackage.isTrialStatus() && !thePackage.isSubStatus()) {
//
//                                    try {
//                                        trialSubscriptionDialog();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {


                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {


                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notificationManager.getNotificationsCount(new APIListener<NotificationCountResponse>() {
            @Override
            public void onSuccess(NotificationCountResponse result, List<Object> params) {

                if (result.getCounts() <= 0) {
                    binding.includedToolbar.noticationCountTextview.setVisibility(View.GONE);
                } else {
                    binding.includedToolbar.noticationCountTextview.setVisibility(View.VISIBLE);
                    binding.includedToolbar.noticationCountTextview.setText(result.getCounts() + "");
                }


            }

            @Override
            public void onFailure(Throwable t) {

            }

        });

    }

    private void setupTabLayout() {

        fragmentOne = new VideoHomeFragment();
        fragmentTwo = new VideoMylistFragment();

        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_video_home_normal, null);
        RelativeLayout tabTwo = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_video_mylist_normal, null);

        binding.includeVideoDashboard.tabLayout.addTab(binding.includeVideoDashboard.tabLayout.newTab().setCustomView(tabOne), true);
        binding.includeVideoDashboard.tabLayout.addTab(binding.includeVideoDashboard.tabLayout.newTab().setCustomView(tabTwo));


    }

    private void bindWidgetsWithAnEvent() {
        binding.includeVideoDashboard.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(final TabLayout.Tab tab) {

                setCurrentTabFragment(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(VideoDashboardActivity.this).inflate(R.layout.tab_video_home_selected, null);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).setCustomView(tabOne);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setBackgroundColor(ContextCompat.getColor(VideoDashboardActivity.this, R.color.color1000));
                        break;
                    case 1:
                        RelativeLayout tabTwo = (RelativeLayout) LayoutInflater.from(VideoDashboardActivity.this).inflate(R.layout.tab_video_mylist_selected, null);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).setCustomView(tabTwo);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setBackgroundColor(ContextCompat.getColor(VideoDashboardActivity.this, R.color.color1000));
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(VideoDashboardActivity.this).inflate(R.layout.tab_video_home_normal, null);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).setCustomView(tabOne);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setBackgroundColor(ContextCompat.getColor(VideoDashboardActivity.this, R.color.backgroundColorTab));
                        break;
                    case 1:
                        RelativeLayout tabTwo = (RelativeLayout) LayoutInflater.from(VideoDashboardActivity.this).inflate(R.layout.tab_video_mylist_normal, null);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).setCustomView(tabTwo);
                        binding.includeVideoDashboard.tabLayout.getTabAt(tab.getPosition()).getCustomView().setBackgroundColor(ContextCompat.getColor(VideoDashboardActivity.this, R.color.backgroundColorTab));
                        break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentOne, "home");
                break;
            case 1:
                replaceFragment(fragmentTwo, "mylist");
                break;

        }
    }

    public void replaceFragment(Fragment fragment, String _fname) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (_fname) {
            case "home":
                binding.includeVideoDashboard.includeDashboardFrame.frameContainerHome.setVisibility(View.VISIBLE);
                binding.includeVideoDashboard.includeDashboardFrame.frameContainerMylist.setVisibility(View.GONE);
                if (!isHomeLoaded) {
                    fm.executePendingTransactions();
                    ft.add(R.id.frame_container_home, fragment, _fname);
                    isHomeLoaded = true;
                }

                ft.commit();
                break;
            case "mylist":
                binding.includeVideoDashboard.includeDashboardFrame.frameContainerHome.setVisibility(View.GONE);
                binding.includeVideoDashboard.includeDashboardFrame.frameContainerMylist.setVisibility(View.VISIBLE);

                if (!isMylistLoaded) {
                    fm.executePendingTransactions();
                    ft.add(R.id.frame_container_mylist, fragment, _fname);
                    isMylistLoaded = true;
                }

                ft.commit();
                break;
            default:
                break;
        }

    }

    private void createLogoutDialog() {
        LayoutInflater myLayout = LayoutInflater.from(VideoDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_logout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                VideoDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView createTextView = (TextView) alertDialog.findViewById(R.id.yes_button_text_view);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.cancel_button_text_view);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                AuthManager.logoutFromApp((Application) getApplication());
                Intent logoutIntent = new Intent(VideoDashboardActivity.this, SplashActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();


            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    @Override
    protected void onDestroy() {
        ((Application) getApplication()).getMixpanelAPI().flush();
        super.onDestroy();
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        System.out.println("jnejnfjtnjtntntjnjt " + item.getItemId());
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.navPackages:
//                Intent intentPackages = new Intent(VideoDashboardActivity.this, PaymentActivity.class);
//                startActivity(intentPackages);
//                break;
//            case R.id.navContact:
//                Intent contactIntent = new Intent(VideoDashboardActivity.this, ContactActivity.class);
//                startActivity(contactIntent);
//                break;
//            case R.id.navSettings:
//                Intent settingsIntent = new Intent(VideoDashboardActivity.this, VideoSettingsActivity.class);
//                startActivity(settingsIntent);
//                break;
//            case R.id.navChild:
//                Intent childIntent = new Intent(VideoDashboardActivity.this, VideoChildModeActivity.class);
//                startActivity(childIntent);
//                break;
//            case R.id.navLogout:
//                createLogoutDialog();
//                break;
//
//        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START);
//
//        return true;
//    }
}
