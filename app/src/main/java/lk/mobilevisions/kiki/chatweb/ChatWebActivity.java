package lk.mobilevisions.kiki.chatweb;

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
import lk.mobilevisions.kiki.databinding.ActivityChatwebBinding;
import lk.mobilevisions.kiki.databinding.ActivityGamesBinding;
import lk.mobilevisions.kiki.service.webview.GamesActivity;

public class ChatWebActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    ActivityChatwebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatweb);
        ((Application) getApplication()).getInjector().inject(this);
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "ChatActivity", null);


        String token = getIntent().getStringExtra("token");
        int chatID = getIntent().getIntExtra("id",0);

        binding.webViewChat.setListener(this, this);
        binding.webViewChat.getSettings().setJavaScriptEnabled(true);
        binding.webViewChat.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webViewChat.setMixedContentAllowed(true);
        binding.webViewChat.setCookiesEnabled(true);
        binding.webViewChat.setThirdPartyCookiesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            binding.webViewChat.getSettings().setDomStorageEnabled(true);
        }
        binding.webViewChat.setWebChromeClient(new WebChromeClient());
        binding.webViewChat.setWebViewClient(new ChatBrowser());

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webViewChat, true);
        }

        binding.webViewChat.addHttpHeader("X-Requested-With", "");
        binding.webViewChat.loadUrl("http://35.200.142.38:8080/chat/?token=" + token + "&chatId=" + chatID);

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


    private class ChatBrowser extends WebViewClient {
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
        binding.webViewChat.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.webViewChat.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        binding.webViewChat.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webViewChat.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!binding.webViewChat.onBackPressed()) {
            return;
        }
        // ...
        super.onBackPressed();
    }
}