package lk.mobilevisions.kiki.video.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import im.delight.android.webview.AdvancedWebView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.activity.AudioTrialActivationActivity;
import lk.mobilevisions.kiki.databinding.ActivityPaymentBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import timber.log.Timber;

public class VideoTrialActivationActivity extends AppCompatActivity implements AdvancedWebView.Listener{


    ActivityPaymentBinding binding;
    @Inject
    SubscriptionsManager subscriptionsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        binding.includedToolbar.titleTextview.setText(getResources().getString(R.string.payments));

        binding.includedToolbar.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoTrialActivationActivity.this, VideoDashboardActivity.class);
                startActivity(intent);
                finish();
                binding.webViewPayment.clearCache(true);
            }
        });
        binding.webViewPayment.setListener(this, this);
        binding.webViewPayment.getSettings().setJavaScriptEnabled(true);
        binding.webViewPayment.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webViewPayment.setMixedContentAllowed(true);
        binding.webViewPayment.setCookiesEnabled(true);
        binding.webViewPayment.setThirdPartyCookiesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            binding.webViewPayment.getSettings().setDomStorageEnabled(true);
        }
        binding.webViewPayment.setWebChromeClient(new WebChromeClient());
        binding.webViewPayment.setWebViewClient(new PaymentsBrowser());

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webViewPayment, true);
        }

        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(getApplication()).getPaidPackageId(), new APIListener<PackageToken>() {
            @Override
            public void onSuccess(final PackageToken packageToken, List<Object> params) {

                if (packageToken.getTokenHash() != null) {
                    Timber.d("Loading URL from the WebView: %s", Utils.App.getConfig(getApplication()).getMobilePaymentGatewayURL() + "?token=" + packageToken.getTokenHash()  + "&isFreeTrial=true");
                    binding.webViewPayment.addHttpHeader("X-Requested-With", "");
                    binding.webViewPayment.loadUrl(Utils.App.getConfig(getApplication()).getMobilePaymentGatewayURL() + "?token=" + packageToken.getTokenHash() + "&isFreeTrial=true");

                    return;
                }

            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                binding.webviewFramelayout.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(VideoTrialActivationActivity.this, t);
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

    protected void handleSMSLink(String url) {
        // Initialize a new intent to send sms message
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String phoneNumber = url.split("[:?]")[1];

        if (!TextUtils.isEmpty(phoneNumber)) {
            intent.setData(Uri.parse("smsto:" + phoneNumber));
        } else {
            intent.setData(Uri.parse("smsto:"));
        }
        if (url.contains("body=")) {
            String smsBody = url.split("body=")[1];

            // Encode the sms body
            try {
                smsBody = URLDecoder.decode(smsBody, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(smsBody)) {
                // Set intent body
                intent.putExtra("sms_body", smsBody);
            }
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the sms app
            startActivity(intent);
        } else {
            Toast.makeText(this, "No SMS app found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

        binding.aviProgress.setVisibility(View.GONE);
        binding.webviewFramelayout.setVisibility(View.GONE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }


    private class PaymentsBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("lffhfhfbvhh  111" + url);
            if (url.contains("/redirecthome")){
                Intent intent = new Intent(VideoTrialActivationActivity.this, VideoDashboardActivity.class);
                startActivity(intent);
                finish();

            }

            if (url.startsWith("sms:")) {
                handleSMSLink(url);
                finish();
                return true;
            }
            System.out.println("lffhfhfbvhh 2222 " + url);
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        binding.webViewPayment.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.webViewPayment.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        binding.webViewPayment.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webViewPayment.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (binding.webViewPayment.canGoBack()) {
            binding.webViewPayment.goBack();
        } else {
            super.onBackPressed();
        }

//        Intent intent = new Intent(AudioPaymentActivity.this, AudioDashboardActivity.class);
//        startActivity(intent);
//        finish();
//
    }
}