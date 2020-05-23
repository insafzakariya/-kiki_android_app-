package lk.mobilevisions.kiki.ui.channels;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.polyak.iconswitch.IconSwitch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.databinding.ActivityChannelsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Channel;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.notifications.NotificationManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.episodes.EpisodesActivity;
import lk.mobilevisions.kiki.ui.main.MainActivity;
import lk.mobilevisions.kiki.video.activity.ContactActivity;
import lk.mobilevisions.kiki.ui.navigation.UpdateUserActivity;
import lk.mobilevisions.kiki.ui.notifications.NotificationsActivity;
import lk.mobilevisions.kiki.ui.packages.MySubscribePackagesActivity;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import lk.mobilevisions.kiki.ui.settings.SettingsActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import timber.log.Timber;

public class ChannelsActivity extends AppCompatActivity implements ChannelsAdapter.OnChannelItemClickListener,
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener,IconSwitch.CheckedChangeListener{


    @Inject
    TvManager tvManager;

    @Inject
    SubscriptionsManager subscriptionsManager;

    @Inject
    NotificationManager notificationManager;

    ActivityChannelsBinding binding;

    GridLayoutManager channelsLayoutManager;

    private ActionBarDrawerToggle drawerToggle;
    private TextView textViewHeader;

    private boolean isPaymentScreenLoaded;

    Menu menu;

    List<Program> programs;


//    private CastContext mCastContext;
//    private MenuItem mediaRouteMenuItem;
//    private IntroductoryOverlay mIntroductoryOverlay;
//    private CastStateListener mCastStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_channels);

        ((Application) getApplication()).getInjector().inject(this);

        setSupportActionBar(binding.includedToolbar.toolbar);
        binding.includedToolbar.imageView30.setImageResource(R.drawable.ic_kiki_logo);
        binding.includedToolbar.textView26.setText("Channels");
//        getSupportActionBar().setIcon(R.drawable.ic_kiki_logo);
//        getSupportActionBar().setTitle("  Channels");
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this,"VideoChannelsActivity",null);

        binding.includedToolbar.iconSwitch.setCheckedChangeListener(this);
        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.includedToolbar.toolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
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
        if (((Application) getApplication()).getAuthUser() != null
                && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
            textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.textViewWelcome);
            if (name.contains(" ")) {
                name = name.substring(0, name.indexOf(" "));
            }
            textViewHeader.setText(getString(R.string.welcome_user, name));
        }

        channelsLayoutManager = new GridLayoutManager(this, 3);
        binding.recycleViewChannels.setLayoutManager(channelsLayoutManager);


        if (Utils.SharedPrefUtil.getBooleanFromSharedPref(this, Constants.COMING_FROM_SPLASH_SCREEN,
                true)) {
            subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(
                    getApplication()).getPaidPackageId(), new APIListener<PackageToken>() {
                @Override
                public void onSuccess(final PackageToken packageToken, List<Object> params) {
                    subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
                        @Override
                        public void onSuccess(Package thePackage, List<Object> params) {
                            final Intent intent = new Intent(ChannelsActivity.this,
                                    PaymentActivity.class);
                            intent.putExtra("paymentTokenHash", packageToken.getTokenHash());
                            if (thePackage.getId() != 46 && thePackage.getId() != 81 &&
                                    thePackage.getId() != 101) {
                                Utils.SharedPrefUtil.saveBooleanToSharedPref(ChannelsActivity.this,
                                        Constants.COMING_FROM_SPLASH_SCREEN, false);
                                showSubscriptionPopup();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
                }
            });


        }
        Utils.SharedPrefUtil.saveStringToSharedPref(ChannelsActivity.this,
                Constants.CURRENT_APP, "video");
//
//        mCastStateListener = new CastStateListener() {
//            @Override
//            public void onCastStateChanged(int newState) {
//                if (newState != CastState.NO_DEVICES_AVAILABLE) {
//                    showIntroductoryOverlay();
//                }
//            }
//        };
//
//        mCastContext = CastContext.getSharedInstance(this);
    }

    @Override
    protected void onResume() {
//        mCastContext.addCastStateListener(mCastStateListener);
        setupSliderView();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }


    private void setupSliderView() {
        String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(this, "kids_mode_password", "");
        boolean isKidsModeEnabled = false;
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            isKidsModeEnabled = true;
        } else {
            isKidsModeEnabled = false;
        }
        binding.imageSlider.removeAllSliders();
        tvManager.getProgramsSliderImages(new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> sliders, List<Object> params) {

                if (sliders != null) {
                    ChannelsActivity.this.programs = sliders;
                    for (Program slider : sliders) {
                        TextSliderView textSliderView = new TextSliderView(ChannelsActivity.this);
                        try {
                            textSliderView
                                    .description(slider.getName())
                                    .image(URLDecoder.decode(slider.getImage(), "UTF-8"))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(ChannelsActivity.this);
                        } catch (UnsupportedEncodingException e) {
                            Timber.e("Invalid url for channel image slider");
                        }
                        binding.imageSlider.addSlider(textSliderView);
                    }

                    binding.imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                    binding.imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    binding.imageSlider.setCustomAnimation(new DescriptionAnimation());
                    binding.imageSlider.setDuration(5000);
                    binding.imageSlider.addOnPageChangeListener(ChannelsActivity.this);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
            }
        }, isKidsModeEnabled);

        if (binding.imageSlider != null)
            binding.imageSlider.startAutoCycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actvity_menu, menu);

        this.menu = menu;
//        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
//                R.id.media_route_menu_item);
//        MenuItem item = menu.findItem(R.id.menuButtonPackage);
//        item.setActionView(R.layout.menu_item_package);

        menu.findItem(R.id.menuButtonNotifications).getActionView()
                .findViewById(R.id.menu_badge).setVisibility(View.GONE);

        MenuItem menuItemNotifications = menu.findItem(R.id.menuButtonNotifications);

//        final Button buttonMenuActionPackage = (Button) item.getActionView().findViewById(R.id.buttonMenuActionPackage);
//        buttonMenuActionPackage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentPackages = new Intent(ChannelsActivity.this, MyPackagesActivity.class);
//                startActivity(intentPackages);
//            }
//        });

        final FrameLayout buttonMenuNotifications = (FrameLayout) menuItemNotifications.getActionView().findViewById(R.id.menuBadgeRoot);
        buttonMenuNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPackages = new Intent(ChannelsActivity.this, NotificationsActivity.class);
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
                    if (!thePackage.isPaid() && !isPaymentScreenLoaded) {
                        isPaymentScreenLoaded = true;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menuButtonNotifications) {
            Intent intentPackages = new Intent(ChannelsActivity.this, NotificationsActivity.class);
            startActivity(intentPackages);
            return true;
        }
//        if (menuItem.getItemId() == R.id.menuButtonSubscribedPrograms) {
//            Intent intentPackages = new Intent(ChannelsActivity.this, SubscriptionActivity.class);
//            startActivity(intentPackages);
//            return true;
//        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Intent browserIntent = null;
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
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.App.getConfig(getApplication()).getMenuFacebookPage()));
                startActivity(browserIntent);
                break;
            case R.id.navUserAgreement:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.App.getConfig(getApplication()).getUserAgreementPage()));
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
    public void onChannelItemClick(View view, Channel channel, int position) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("channelId", channel.getId());
        intent.putExtra("channelName", channel.getName());
        ((Application) getApplication()).setSelectedChannel(channel.getId());
        startActivity(intent);
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
                                ContextCompat.getDrawable(ChannelsActivity.this, R.drawable.menu_notification),
                                0);
                        menu.findItem(R.id.menuButtonNotifications).getActionView()
                                .findViewById(R.id.menu_badge).setVisibility(View.GONE);
                        try {
                            Badges.removeBadge(ChannelsActivity.this);
                        } catch (BadgesNotSupportedException badgesNotSupportedException) {
                            Timber.d(badgesNotSupportedException.getMessage());
                        }
                    } else {

                        ActionItemBadge.update(menu.findItem(R.id.menuButtonNotifications),
                                ContextCompat.getDrawable(ChannelsActivity.this, R.drawable.menu_notification),
                                result.getCounts());
                        menu.findItem(R.id.menuButtonNotifications).getActionView()
                                .findViewById(R.id.menu_badge).setVisibility(View.VISIBLE);
                        try {
                            Badges.setBadge(ChannelsActivity.this, result.getCounts());
                        } catch (BadgesNotSupportedException badgesNotSupportedException) {
                            Timber.d(badgesNotSupportedException.getMessage());
                        }
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
            }

        });

        String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(this, "kids_mode_password", "");
        System.out.println("checking 5.1 error 11111 " + kidsModePassword);
        boolean isKidsModeEnabled = false;
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            isKidsModeEnabled = true;
        } else {
            isKidsModeEnabled = false;
        }

        tvManager.getAllChannels(new APIListener<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> channels, List<Object> params) {

                if (channels != null) {
                    binding.recycleViewChannels.setAdapter(new ChannelsAdapter(ChannelsActivity.this,
                            channels, ChannelsActivity.this));

                    if (channels.size() <= 0) {
                        binding.recycleViewChannels.setVisibility(View.GONE);
                        binding.TextViewEmptyChannels.setVisibility(View.VISIBLE);
                        binding.aviProgress.setVisibility(View.GONE);
                    } else {
                        binding.recycleViewChannels.setVisibility(View.VISIBLE);
                        binding.TextViewEmptyChannels.setVisibility(View.GONE);
                        binding.aviProgress.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(ChannelsActivity.this, t);
            }
        }, isKidsModeEnabled);


    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (programs != null && programs.isEmpty() != true) {
            for (Program program : programs) {
                if (program.getName().equals(slider.getDescription())) {
                    Intent intentEpisodes = new Intent(this, EpisodesActivity.class);
                    intentEpisodes.putExtra("program", program);
                    startActivity(intentEpisodes);
                    break;
                }
            }

        }

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        binding.imageSlider.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void showSubscriptionPopup() {
        final Dialog popup = new Dialog(ChannelsActivity.this);
        popup.setCancelable(false);
        popup.setCanceledOnTouchOutside(false);
        popup.setContentView(R.layout.popup_subscribe);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView btnClose = popup.findViewById(R.id.btn_close);
        ImageView btnSubscribe = popup.findViewById(R.id.btn_subscribe);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
                Intent intentPackages = new Intent(ChannelsActivity.this, MySubscribePackagesActivity.class);
                startActivity(intentPackages);
            }
        });
        if(!((Activity) ChannelsActivity.this).isFinishing())
        {
            popup.show();
            //show dialog
        }

    }

    @Override
    public void onCheckChanged(IconSwitch.Checked current) {
        switch (binding.includedToolbar.iconSwitch.getChecked()) {
            case LEFT:

                break;
            case RIGHT:
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibe.vibrate(500);
                }

                Intent intent = new Intent(this, AudioDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

//    private void showIntroductoryOverlay() {
//        if (mIntroductoryOverlay != null) {
//            mIntroductoryOverlay.remove();
//        }
//        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
//                            ChannelsActivity.this, mediaRouteMenuItem)
//                            .setTitleText("Introducing Cast")
//                            .setSingleTime()
//                            .setOnOverlayDismissedListener(
//                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
//                                        @Override
//                                        public void onOverlayDismissed() {
//                                            mIntroductoryOverlay = null;
//                                        }
//                                    })
//                            .build();
//                    mIntroductoryOverlay.show();
//                }
//            });
//        }
//    }
}
