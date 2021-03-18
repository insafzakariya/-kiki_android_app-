package lk.mobilevisions.kiki.service.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import im.delight.android.webview.AdvancedWebView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.ActivityInsuranceBinding;
import lk.mobilevisions.kiki.service.activity.ServiceHomeActivity;

public class InsuranceActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    ActivityInsuranceBinding binding;
    private boolean navigateToHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insurance);
        ((Application) getApplication()).getInjector().inject(this);
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "InsuranceActivity", null);

        String newsUrl = getIntent().getStringExtra("Url");
        String referanceCode = getIntent().getStringExtra("referanceCode");
        String landingUrl = getIntent().getStringExtra("landingUrl");
        boolean isNews = getIntent().getBooleanExtra("isNews", false);

        binding.includedToolbar.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateToHome) {
                    Intent intent = new Intent(InsuranceActivity.this, ServiceHomeActivity.class);
                    startActivity(intent);
                    finish();
                    binding.webViewInsurance.clearCache(true);
                }
                if (!binding.webViewInsurance.canGoBack()){
                    finish();
                }

                binding.webViewInsurance.onBackPressed();
            }
        });
        binding.webViewInsurance.setListener(this, this);
        binding.webViewInsurance.getSettings().setJavaScriptEnabled(true);
        binding.webViewInsurance.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webViewInsurance.setMixedContentAllowed(true);
        binding.webViewInsurance.setCookiesEnabled(true);
        binding.webViewInsurance.setThirdPartyCookiesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            binding.webViewInsurance.getSettings().setDomStorageEnabled(true);
        }
        binding.webViewInsurance.setWebChromeClient(new WebChromeClient());
        binding.webViewInsurance.setWebViewClient(new InsuranceBrowser());

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webViewInsurance, true);
        }

        binding.webViewInsurance.addHttpHeader("X-Requested-With", "");
        if (isNews) {
            binding.includedToolbar.titleTextview.setText("News");
            binding.webViewInsurance.loadUrl(newsUrl);
        } else {
            binding.webViewInsurance.loadUrl("http://dev.v4.janashakthi-digital.echonlabs.com/app/plans/health#quickstart" + "?referralcode=" + referanceCode + "&lang=" +
                    Application.getInstance().getAuthUser().getLanguage().toLowerCase().substring(0, 2) + "&bridge_id=" + Application.getInstance().getAuthUser().getId());
        }

        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
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


    private class InsuranceBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("/app/pages/success")) {
                binding.includedToolbar.backImageview.setBackgroundResource(R.drawable.ic_video_home_selected);
                navigateToHome = true;
            }

            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        binding.webViewInsurance.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.webViewInsurance.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        binding.webViewInsurance.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webViewInsurance.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!binding.webViewInsurance.onBackPressed()) {
            return;
        }
        // ...
        super.onBackPressed();
    }
}