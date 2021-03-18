package lk.mobilevisions.kiki.service.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioProfileActivity;
import lk.mobilevisions.kiki.databinding.ActivityServiceHomeBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.service.adapter.EntertainmentAdapter;
import lk.mobilevisions.kiki.service.adapter.GamesAdapter;
import lk.mobilevisions.kiki.service.dto.ServiceModel;
import lk.mobilevisions.kiki.service.dto.ServiceSub;
import lk.mobilevisions.kiki.service.webview.GamesActivity;
import lk.mobilevisions.kiki.service.webview.InsuranceActivity;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import lk.mobilevisions.kiki.video.activity.ContactActivity;
import lk.mobilevisions.kiki.video.activity.VideoChildModeActivity;
import lk.mobilevisions.kiki.video.activity.VideoDashboardActivity;
import lk.mobilevisions.kiki.video.activity.VideoProfileActivity;
import lk.mobilevisions.kiki.video.activity.VideoSettingsActivity;
import lk.mobilevisions.kiki.video.activity.VideoTrialActivationActivity;

import static com.androidquery.util.AQUtility.getContext;

public class ServiceHomeActivity extends AppCompatActivity implements View.OnClickListener, EntertainmentAdapter.OnEntertainmentItemClickListener {

    ActivityServiceHomeBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private boolean trialStatus;

    @Inject
    SubscriptionsManager subscriptionsManager;
    @Inject
    TvManager tvManager;
    @Inject
    AuthManager authManager;

    List<ServiceModel> serviceList = new ArrayList<>();
    LinearLayoutManager channelsLayoutManager;
    EntertainmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_home);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }

        getServices();
        checkTrialStatus();
        currentTime();

        channelsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.includedComponents.servicesRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new EntertainmentAdapter(this, serviceList, ServiceHomeActivity.this);
        binding.includedComponents.servicesRecyclerview.setHasFixedSize(true);
        binding.includedComponents.servicesRecyclerview.setItemViewCacheSize(50);
        binding.includedComponents.servicesRecyclerview.setDrawingCacheEnabled(true);
        binding.includedComponents.servicesRecyclerview.setAdapter(mAdapter);

        binding.includedComponents.userText.setText(Application.getInstance().getAuthUser().getName());

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

                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }
                    binding.welcomeTextview.setText(getString(R.string.welcome_user, name));
                }


            }
        };
        binding.drawerLayout.addDrawerListener(drawerToggle);

        binding.includedComponents.drawerBtn.setOnClickListener(this);

        binding.subLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trialStatus) {
                    Intent intentPackages = new Intent(ServiceHomeActivity.this, VideoTrialActivationActivity.class);
                    startActivity(intentPackages);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent intentPackages = new Intent(ServiceHomeActivity.this, PaymentActivity.class);
                    startActivity(intentPackages);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        binding.settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(ServiceHomeActivity.this, VideoSettingsActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        binding.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(ServiceHomeActivity.this, ContactActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        binding.childLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(ServiceHomeActivity.this, VideoChildModeActivity.class);
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
    }

    private void currentTime() {

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting = null;
        if (hour >= 6 && hour < 12) {
            greeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good Evening";
        } else if (hour >= 21) {
            greeting = "Good Night";
        }

        binding.includedComponents.greetingText.setText(greeting);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_btn:
                System.out.println("gjvghcvghcgh 111");
                binding.drawerLayout.openDrawer(Gravity.LEFT);
                break;
            default:
        }
    }

    private void getServices() {

        tvManager.getAllServices(new APIListener<List<ServiceModel>>() {
            @Override
            public void onSuccess(List<ServiceModel> result, List<Object> params) {

                serviceList = result;
                binding.includedComponents.servicesRecyclerview.setAdapter(new EntertainmentAdapter(getContext(),
                        serviceList, ServiceHomeActivity.this));
                if (result.size() <= 0) {
                    binding.includedComponents.servicesRecyclerview.setVisibility(View.GONE);


                } else {

                    binding.includedComponents.servicesRecyclerview.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }


    private void createLogoutDialog() {
        LayoutInflater myLayout = LayoutInflater.from(ServiceHomeActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_logout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ServiceHomeActivity.this);
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
                Intent logoutIntent = new Intent(ServiceHomeActivity.this, SplashActivity.class);
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

    private void checkTrialStatus() {

        subscriptionsManager.getTrialStatus(new APIListener<PackageV2>() {
            @Override
            public void onSuccess(PackageV2 thePackage, List<Object> params) {

                trialStatus = thePackage.isTrialStatus();

            }

            @Override
            public void onFailure(Throwable t) {


            }
        });
    }

    @Override
    public void onEntertainmentItemClick(ServiceModel serviceModel, int position, List<ServiceModel> list) {

        switch (serviceModel.getName()) {
            case "Video": {
                Intent intent = new Intent(ServiceHomeActivity.this, VideoDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case "Music": {
                Intent intent = new Intent(ServiceHomeActivity.this, AudioDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case "Insurance": {
                Intent intent = new Intent(ServiceHomeActivity.this, InsuranceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Url", serviceModel.getUrl());
                intent.putExtra("referanceCode", serviceModel.getReferanceCode());
                intent.putExtra("landingUrl", serviceModel.getLandingUrl());
                startActivity(intent);
                break;
            }
            case "News":
            Intent intent = new Intent(ServiceHomeActivity.this, InsuranceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Url", serviceModel.getUrl());
            intent.putExtra("isNews", true);
            startActivity(intent);
                break;
        }

    }
}
