package lk.mobilevisions.kiki.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.inject.Inject;

import im.delight.android.webview.AdvancedWebView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentUserFreeTrailBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;

import timber.log.Timber;

public class UserFreeTrailFragment extends Fragment implements AdvancedWebView.Listener {

    View view;
    FragmentUserFreeTrailBinding binding;
    @Inject
    SubscriptionsManager subscriptionsManager;
    private String paymentTokenHash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_free_trail, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        binding.webViewPayment.setListener(getActivity(), this);
        binding.webViewPayment.getSettings().setJavaScriptEnabled(true);
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application.BUS.post(new UserPaymentComplete());
            }
        });
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(
                getActivity().getApplication()).getPaidPackageId(), new APIListener<PackageToken>() {
            @Override
            public void onSuccess(final PackageToken packageToken, List<Object> params) {
                subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
                    @Override
                    public void onSuccess(Package thePackage, List<Object> params) {
                        paymentTokenHash = packageToken.getTokenHash();
                        System.out.println("checking free trail 0000 " + paymentTokenHash);
                        setUpPaymentWebkit();
                        binding.aviProgress.setVisibility(View.GONE);
                        binding.button2.setVisibility(View.VISIBLE);
                        binding.webViewPayment.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(getActivity(), t);
                        binding.aviProgress.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
                binding.aviProgress.setVisibility(View.GONE);
            }
        });
        binding.webViewPayment.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && binding.webViewPayment.canGoBack()) {
                    System.out.println("checking back pressed 11111 ");
                    binding.webViewPayment.goBack();
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void setUpPaymentWebkit() {
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


        if (paymentTokenHash != null) {
            Timber.d("Loading URL from the WebView: %s", Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?token=" + paymentTokenHash);
            binding.webViewPayment.addHttpHeader("X-Requested-With", "");
            System.out.println("checking free trail 1111 " + Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?token=" + paymentTokenHash + "&isFreeTrial=true");
//            binding.webViewPayment.loadUrl("https://services.mobitel.lk/MCCPortal/service/?compId=SusilaTV&reqType=ACTIVE&servId=SUWS&bridgeID=SUWS");
            binding.webViewPayment.loadUrl(Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?token=" + paymentTokenHash + "&isFreeTrial=true");
            return;
        }

        String viewerToken = Utils.Auth.getBearerToken((Application) getActivity().getApplication());
        System.out.println("checking free trail 2222 " + viewerToken);

        if (viewerToken != null) {
            Timber.d("Loading URL from the WebView: %s", Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?viewerToken=" + viewerToken);
            binding.webViewPayment.addHttpHeader("X-Requested-With", "");
            binding.webViewPayment.loadUrl(Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?viewerToken=" + viewerToken + "&isFreeTrial=true");
            System.out.println("checking free trail 3333 " + Utils.App.getConfig(getActivity().getApplication()).getMobilePaymentGatewayURL() + "?viewerToken=" + viewerToken + "&isFreeTrial=true");

            return;
// binding.webViewPayment.loadUrl("https://services.mobitel.lk/MCCPortal/service/?compId=SusilaTV&reqType=ACTIVE&servId=SUWS&bridgeID=SUWS");
        }
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
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the sms app
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "No SMS app found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

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
            System.out.println("checking free trail 4444 " + url);
            if (url.startsWith("sms:")) {
                System.out.println("checking free trail 121212  ******   ");
                handleSMSLink(url);
                Application.BUS.post(new UserPaymentComplete());
                return true;
            }
            System.out.println("checking free trail 5555 " + url);
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        binding.webViewPayment.onActivityResult(requestCode, resultCode, intent);
        System.out.println("checking free trail 6666 ");
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        binding.webViewPayment.onResume();

        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        binding.webViewPayment.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        binding.webViewPayment.onDestroy();
        // ...
        super.onDestroy();
    }

    public class UserPaymentComplete {


    }


}
