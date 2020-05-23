package lk.mobilevisions.kiki.audio.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityAudioPackagesBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;



public class AudioPackagesActivity extends AppCompatActivity {

    ActivityAudioPackagesBinding binding;
    @Inject
    SubscriptionsManager subscriptionsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_packages);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        binding.includedToolbar.titleTextview.setText("Pakages");

        binding.includedToolbar.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.subscribeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.aviProgress.setVisibility(View.VISIBLE);
                subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(getApplication()).getPaidPackageId(), new APIListener<PackageToken>() {
                    @Override
                    public void onSuccess(final PackageToken packageToken, List<Object> params) {

                        subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
                            @Override
                            public void onSuccess(Package thePackage, List<Object> params) {
                                binding.aviProgress.setVisibility(View.GONE);
                                final Intent intent = new Intent(AudioPackagesActivity.this, AudioPaymentActivity.class);
                                intent.putExtra("paymentTokenHash", packageToken.getTokenHash());
                                startActivity(intent);

//                                if (thePackage.getId() == 46 || thePackage.getId() == 81) {
////                                    new MaterialDialog.Builder(AudioPackagesActivity.this)
////                                            .title(getString(R.string.app_name))
////                                            .content("You already have a subscription. Are you sure you want to subscribe it again?")
////                                            .positiveText("Yes")
////                                            .negativeText("No")
////                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
////                                                @Override
////                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
////                                                    startActivity(intent);
////
////                                                }
////                                            })
////                                            .show();
//                                } else {
//
//
//                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                binding.aviProgress.setVisibility(View.GONE);
                                Utils.Error.onServiceCallFail(AudioPackagesActivity.this, t);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        binding.aviProgress.setVisibility(View.GONE);
                        Utils.Error.onServiceCallFail(AudioPackagesActivity.this, t);
                    }
                });
            }
        });

        AuthUser authUser = ((Application) getApplication()).getAuthUser();
        if (authUser != null && authUser.getCarrier() == AuthUser.Carrier.MOBITEL) {
            binding.dialogImageview.setVisibility(View.GONE);
        } else {
            binding.mobitelImageview.setVisibility(View.VISIBLE);
        }


    }


}
