package lk.mobilevisions.kiki.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.databinding.ActivityMainBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.notifications.NotificationManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;

import lk.mobilevisions.kiki.ui.channels.ChannelsActivity;
import lk.mobilevisions.kiki.ui.main.home.HomeFragment;
import lk.mobilevisions.kiki.video.activity.ContactActivity;
import lk.mobilevisions.kiki.ui.navigation.UpdateUserActivity;
import lk.mobilevisions.kiki.ui.notifications.NotificationsActivity;
import lk.mobilevisions.kiki.ui.packages.MySubscribePackagesActivity;
import lk.mobilevisions.kiki.ui.settings.SettingsActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity  {

    public static final int REQUEST_CODE_PERMISSIONS = 1001;
    private ActionBarDrawerToggle drawerToggle;
//    TabViewPagerAdapter tabViewPagerAdapter;

    ActivityMainBinding binding;
    HomeFragment homeFragment;
    TextView textViewHeader;

    Button buttonMenuActionPackage;

    @Inject
    SubscriptionsManager subscriptionsManager;

    @Inject
    NotificationManager notificationManager;

    Menu menu;

    //    private MenuItem mediaRouteMenuItem;
//    private CastContext mCastContext;
//    private CastStateListener mCastStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.BUS.register(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ((Application) getApplication()).getInjector().inject(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

//        binding.tabViewPager.setOffscreenPageLimit(4);

        setSupportActionBar(binding.includedToolbar.toolbar);
        binding.includedToolbar.imageView30.setImageResource(R.drawable.ic_kiki_logo);
        String channelName = getIntent().getExtras().getString("channelName", "");
//        binding.includedToolbar.textView26.setText(channelName);
//        binding.includedToolbar.iconSwitch.setCheckedChangeListener(this);
//        getSupportActionBar().setIcon(R.drawable.ic_kiki_logo);


//        getSupportActionBar().setTitle("  " + channelName);


        binding.navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //selectDrawerItem(menuItem);
                        return true;
                    }
                });
        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.includedToolbar.toolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);


        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = "HomeFragment";
        homeFragment = new HomeFragment();
        transaction.replace(R.id.frameLayoutHomeFragment, homeFragment, tag);
        transaction.commitAllowingStateLoss();
//        tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(),
//                Settings.Menu.MENU_TITLES, Settings.Menu.MENU_ICONS);
//        binding.tabViewPager.setAdapter(tabViewPagerAdapter);
//        binding.slidingTabs.setDistributeEvenly(true);
//        binding.slidingTabs.setCustomTabView(R.layout.layout_maintab, R.id.lTabText, R.id.lTabImage);
//        binding.slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
//            }
//        });
//        binding.slidingTabs.setViewPager(binding.tabViewPager);

        binding.navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setCheckable(false);
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        String name = null;
        if (((Application) getApplication()).getAuthUser() != null && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
            textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.textViewWelcome);
            if (name.contains(" ")) {
                name = name.substring(0, name.indexOf(" "));
            }
            textViewHeader.setText(getString(R.string.welcome_user, name));
        }

//        binding.slidingTabs.setEnabled(false);
//        for (int i = 0; i < binding.slidingTabs.getChildCount(); i++) {
//            binding.slidingTabs.getChildAt(i).setClickable(false);
//        }


//        binding.slidingTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Timber.d("Page Changed %s", position);
//                try {
//                    Application.BUS.post(new TabChangedEvent(position));
//                } catch (Exception e) {
//                    Timber.e("Exception page change");
//                }
//
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        mCastContext = CastContext.getSharedInstance(this);
//        mCastStateListener = new CastStateListener() {
//            @Override
//            public void onCastStateChanged(int newState) {
//                if (newState != CastState.NO_DEVICES_AVAILABLE) {
//
//                }
//            }
//        };
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

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navNotifications:
                Intent intentNotifications = new Intent(this, NotificationsActivity.class);
                startActivity(intentNotifications);
                break;
            case R.id.navPackages:
                Intent intentPackages = new Intent(this, MySubscribePackagesActivity.class);
                startActivity(intentPackages);
                break;
            case R.id.navChannels:
                Intent intentChannels = new Intent(this, ChannelsActivity.class);
                intentChannels.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentChannels.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentChannels);
                break;
            case R.id.navFacebookPage:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.App.getConfig(getApplication()).getMenuFacebookPage()));
                startActivity(browserIntent);
                break;
            case R.id.navFAQ:
                Intent faqIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.App.getConfig(getApplication()).getFaqURL()));
                startActivity(faqIntent);
                break;
            case R.id.navContact:
                Intent contactIntent = new Intent(this, ContactActivity.class);
                startActivity(contactIntent);
                break;
            case R.id.navUpdateProfile:
                Intent updateProfileIntent = new Intent(this, UpdateUserActivity.class);
                startActivity(updateProfileIntent);
                break;
            case R.id.navSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.navLogout:
                AuthManager.logoutFromApp((Application) getApplication());
                Intent logoutIntent = new Intent(this, SplashActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
        }

        binding.drawerLayout.closeDrawers();

    }

    @Override
    protected void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }


    public class TabChangedEvent {

        private int position;

        public TabChangedEvent(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        System.out.println("find episode and position 3333   " + ((Application) getApplication()).PLAYER.getCurrentVideo());
        System.out.println("find episode and position 4444   " + ((Application) getApplication()).PLAYER.getCurrentEpisode());
        System.out.println("find episode and position 5555   " + ((Application) getApplication()).PLAYER.getCurrentVideoPosition());

        Utils.SharedPrefUtil.saveIntToSharedPref(getApplicationContext(), "currentVideo", ((Application) getApplication()).PLAYER.getCurrentVideo());
        Utils.SharedPrefUtil.saveIntToSharedPref(getApplicationContext()
                , "currentEpisode", ((Application) getApplication()).PLAYER.getCurrentEpisode());
        Utils.SharedPrefUtil.saveLongToSharedPref(getApplicationContext(),
                "currentVideoPosition", ((Application) getApplication()).PLAYER.getCurrentVideoPosition());

    }

    @Override
    protected void onStart() {
        super.onStart();

        notificationManager.getNotificationsCount(new APIListener<NotificationCountResponse>() {
            @Override
            public void onSuccess(NotificationCountResponse result, List<Object> params) {
                if (menu != null) {
                    if (result.getCounts() <= 0) {
                        ActionItemBadge.update(menu.findItem(R.id.menuButtonNotifications),
                                ContextCompat.getDrawable(MainActivity.this, R.drawable.menu_notification),
                                0);
                        menu.findItem(R.id.menuButtonNotifications).getActionView()
                                .findViewById(R.id.menu_badge).setVisibility(View.GONE);
                        try {
                            Badges.removeBadge(MainActivity.this);
                        } catch (BadgesNotSupportedException badgesNotSupportedException) {
                            Timber.d(badgesNotSupportedException.getMessage());
                        }
                    } else {

                        ActionItemBadge.update(menu.findItem(R.id.menuButtonNotifications),
                                ContextCompat.getDrawable(MainActivity.this, R.drawable.menu_notification),
                                result.getCounts());
                        menu.findItem(R.id.menuButtonNotifications).getActionView()
                                .findViewById(R.id.menu_badge).setVisibility(View.VISIBLE);
                        try {
                            Badges.setBadge(MainActivity.this, result.getCounts());
                        } catch (BadgesNotSupportedException badgesNotSupportedException) {
                            Timber.d(badgesNotSupportedException.getMessage());
                        }
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(MainActivity.this, t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menuButtonNotifications) {
            Intent intentPackages = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intentPackages);
            return true;
        }
//        if (menuItem.getItemId() == R.id.menuButtonSubscribedPrograms) {
//            Intent intentPackages = new Intent(MainActivity.this, SubscriptionActivity.class);
//            startActivity(intentPackages);
//            return true;
//        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actvity_menu, menu);
//        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
//                R.id.media_route_menu_item);
        this.menu = menu;

//        MenuItem item = menu.findItem(R.id.menuButtonPackage);
//        item.setActionView(R.layout.menu_item_package);

        menu.findItem(R.id.menuButtonNotifications).getActionView()
                .findViewById(R.id.menu_badge).setVisibility(View.GONE);

        MenuItem menuItemNotifications = menu.findItem(R.id.menuButtonNotifications);

//        final Button buttonMenuActionPackage = (Button) item.getActionView().findViewById(R.id.buttonMenuActionPackage);
//        buttonMenuActionPackage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentPackages = new Intent(MainActivity.this, MyPackagesActivity.class);
//                startActivity(intentPackages);
//            }
//        });

        final FrameLayout buttonMenuNotifications = (FrameLayout) menuItemNotifications.getActionView().findViewById(R.id.menuBadgeRoot);
        buttonMenuNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPackages = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intentPackages);
            }
        });

//        if (item.getItemId() == R.id.menuButtonPackage) {
//
//        }
        subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
            @Override
            public void onSuccess(Package thePackage, List<Object> params) {
                if (thePackage != null) {
//                    buttonMenuActionPackage.setText(thePackage.getName());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(MainActivity.this, t);
            }
        });

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.i("Permission Granted by the user");
        } else {
            if (((Activity) MainActivity.this).hasWindowFocus()) {
                MaterialDialog.Builder dialogBuilder = Utils.Dialog.createDialog(this, getString(R.string.need_permissions));
                dialogBuilder.cancelable(false).positiveText(getString(R.string.ok));
                dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_CODE_PERMISSIONS);
                    }
                }).show();
            }

        }

    }

    private void requestReadPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                if (((Activity) MainActivity.this).hasWindowFocus()) {
                    MaterialDialog.Builder dialogBuilder = Utils.Dialog.createDialog(this, getString(R.string.need_permissions));
                    dialogBuilder.cancelable(false).positiveText(getString(R.string.ok));
                    dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    REQUEST_CODE_PERMISSIONS);
                        }
                    }).show();
                }

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_PERMISSIONS);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCastContext.addCastStateListener(mCastStateListener);
        if (buttonMenuActionPackage != null) {
            subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
                @Override
                public void onSuccess(Package thePackage, List<Object> params) {
                    if (thePackage != null) {
                        buttonMenuActionPackage.setText(thePackage.getName());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.Error.onServiceCallFail(MainActivity.this, t);
                }
            });
        }
    }

    @Override
    protected void onPause() {
//        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }

//    @Override
//    public void onCheckChanged(IconSwitch.Checked current) {
//        switch (binding.includedToolbar.iconSwitch.getChecked()) {
//            case LEFT:
//
//                break;
//            case RIGHT:
//                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                } else {
//                    //deprecated in API 26
//                    vibe.vibrate(500);
//                }
//
//                Intent intent = new Intent(this, AudioDashboardActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//                break;
//        }
//    }
}