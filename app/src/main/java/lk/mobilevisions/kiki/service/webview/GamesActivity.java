package lk.mobilevisions.kiki.service.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import im.delight.android.webview.AdvancedWebView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.ActivityGamesBinding;

public class GamesActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    ActivityGamesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_games);
        ((Application) getApplication()).getInjector().inject(this);
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "GamesActivity", null);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String gameUrl = getIntent().getStringExtra("gameUrl");
//        binding.includedToolbar.backImageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (navigateToHome) {
//                    Intent intent = new Intent(InsuranceActivity.this, ServiceHomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                    binding.webViewInsurance.clearCache(true);
//                }
//                binding.webViewInsurance.goBack();
//            }
//        });
        binding.webViewGames.setListener(this, this);
        binding.webViewGames.getSettings().setJavaScriptEnabled(true);
        binding.webViewGames.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webViewGames.setMixedContentAllowed(true);
        binding.webViewGames.setCookiesEnabled(true);
        binding.webViewGames.setThirdPartyCookiesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            binding.webViewGames.getSettings().setDomStorageEnabled(true);
        }
        binding.webViewGames.setWebChromeClient(new WebChromeClient());
        binding.webViewGames.setWebViewClient(new GamesBrowser());

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webViewGames, true);
        }

        binding.webViewGames.addHttpHeader("X-Requested-With", "");
        binding.webViewGames.loadUrl(gameUrl);

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


    private class GamesBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.contains("/app/pages/success")) {
//                binding.includedToolbar.backImageview.setBackgroundResource(R.drawable.ic_video_home_selected);
//                navigateToHome = true;
//            }

            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        binding.webViewGames.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.webViewGames.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        binding.webViewGames.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webViewGames.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!binding.webViewGames.onBackPressed()) {
            return;
        }
        // ...
        super.onBackPressed();
    }
}