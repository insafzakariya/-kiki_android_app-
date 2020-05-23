package lk.mobilevisions.kiki.ui.packages;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;


import lk.mobilevisions.kiki.databinding.ActivityMyPackagesBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;

public class MyPackagesActivity extends AppCompatActivity implements MyPackagesAdapter.OnPackagesItemClickListener {



    private static final String VIU_PACKAGE_NAME_EXTEND = "com.dialog.dialoggo.another.app.ANOTHER_ACTIVITY";

    ActivityMyPackagesBinding binding;

    @Inject
    SubscriptionsManager subscriptionsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_packages);
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.editTextPromocode.addTextChangedListener(new FourDigitCardFormatWatcher());

        ((Application) getApplication()).getInjector().inject(this);

        binding.buttonEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(binding.editTextPromocode.getText() == null || "".equals(binding.editTextPromocode.getText().toString())){
                    Utils.Dialog.createOKDialog(MyPackagesActivity.this, "Code cannot be blank", null);
                    return;
                }
                subscriptionsManager.subscribePromotion(binding.editTextPromocode.getText().toString().replace(" ", ""),new APIListener<Package>() {

                    @Override
                    public void onSuccess(Package pack, List<Object> params) {
                        Utils.Dialog.createOKDialog(MyPackagesActivity.this, "You have activated the " + pack.getName() + " successfully!"
                                + " Package description: " + pack.getDescription(), null);
                        binding.editTextPromocode.setText("");
                        loadSubscriptions();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(MyPackagesActivity.this, t);
                    }
                });
            }
        });

        binding.buttonScratchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPackagesActivity.this, PaymentActivity.class);
                intent.putExtra("viewerToken", Utils.Auth.getBearerToken((Application) getApplication()));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubscriptions();
    }

    private void loadSubscriptions(){

        binding.recycleViewPackages.setLayoutManager(new LinearLayoutManager(this));
        subscriptionsManager.getActivatedPackage(new APIListener<Package>(){
            @Override
            public void onSuccess(Package result, List<Object> params) {
                addtoUI(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(MyPackagesActivity.this, t);
            }

        });

        binding.recycleViewPackagesToActivate.setLayoutManager(new LinearLayoutManager(this));
        subscriptionsManager.getAllSubscriptionPackages(new APIListener<List<Package>>() {
            @Override
            public void onSuccess(List<Package> packages, List<Object> params) {
                binding.recycleViewPackagesToActivate.setAdapter
                        (new MyPackagesAdapter(getApplicationContext(), packages, false,
                                MyPackagesActivity.this));

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(MyPackagesActivity.this, t);
            }
        });
    }


    public void addtoUI(Package p1) {
        List<Package> packages = new ArrayList<Package>();
        packages.add(p1);
        binding.recycleViewPackages.setAdapter
                (new MyPackagesAdapter(getApplicationContext(), packages, true,
                        new MyPackagesAdapter.OnPackagesItemClickListener() {
                            @Override
                            public void onPackagesItemClick(View view, Package thePackage, int position) {

                            }
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPackagesItemClick(View view, final Package thePackage, final int position) {

        if(thePackage.isCanSubscribe()){
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.app_name))
                    .content("Description:\n"+ thePackage.getDescription() + "\nAre you sure you want to activate this package?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .positiveColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            if(thePackage.getId() == 3) {
                                AuthManager.logoutFromApp((lk.mobilevisions.kiki.app.Application) getApplicationContext());
                                Intent loginIntent = new Intent(MyPackagesActivity.this, SplashActivity.class);
                                startActivity(loginIntent);

                                Intent mIntent = new Intent(VIU_PACKAGE_NAME_EXTEND);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(Intent.EXTRA_TEXT, "lk.mobilevisions.kiki");
                                mBundle.putString("providerId", "4");
                                mBundle.putBoolean("isSubscriptionTask",true);
                                mIntent.putExtras(mBundle);
                                startActivity(mIntent);

                                finish();
                            } else {
                                subscriptionsManager.generateSubscriptionToken(thePackage.getId(), new APIListener<PackageToken>() {
                                    @Override
                                    public void onSuccess(PackageToken packageToken, List<Object> params) {
                                        Intent intent = new Intent(MyPackagesActivity.this, PaymentActivity.class);
                                        intent.putExtra("paymentTokenHash", packageToken.getTokenHash());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Utils.Error.onServiceCallFail(MyPackagesActivity.this, t);
                                    }
                                });

                            }
                        }
                    })
                    .show();
        }

    }

}
