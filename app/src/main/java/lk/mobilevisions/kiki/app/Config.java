/**
 * Created by Chatura Dilan Perera on 6/11/2016.
 */
package lk.mobilevisions.kiki.app;

public class Config {

    private static Config config;

    private Config(){

    }

    public static Config getInstance(){
        if(config == null){
            config = new Config();
        }
        return config;
    }

    private String serverURL;
    private String packageName;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private String twitterCallbackURL;
    private String menuFacebookPage;
    private String userAgreementPage;
    private String shareURL;
    private boolean screenCaptureDisabled;
    private String mobilePaymentGatewayURL;
    private String faqURL;
    private String contactTelephone;
    private String contactEmail;
    private long defaultVideoResolution;
    private long defaultSubtitle;
    private long paidPackageId;

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        Config.config = config;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        this.twitterConsumerSecret = twitterConsumerSecret;
    }

    public String getTwitterCallbackURL() {
        return twitterCallbackURL;
    }

    public void setTwitterCallbackURL(String twitterCallbackURL) {
        this.twitterCallbackURL = twitterCallbackURL;
    }

    public String getMenuFacebookPage() {
        return menuFacebookPage;
    }

    public void setMenuFacebookPage(String menuFacebookPage) {
        this.menuFacebookPage = menuFacebookPage;
    }

    public boolean isScreenCaptureDisabled() {
        return screenCaptureDisabled;
    }

    public void setScreenCaptureDisabled(boolean screenCaptureDisabled) {
        this.screenCaptureDisabled = screenCaptureDisabled;
    }

    public String getShareURL() {
        return shareURL;
    }

    public void setShareURL(String shareURL) {
        this.shareURL = shareURL;
    }

    public String getMobilePaymentGatewayURL() {
        return mobilePaymentGatewayURL;
    }

    public void setMobilePaymentGatewayURL(String mobilePaymentGatewayURL) {
        this.mobilePaymentGatewayURL = mobilePaymentGatewayURL;
    }

    public String getFaqURL() {
        return faqURL;
    }

    public void setFaqURL(String faqURL) {
        this.faqURL = faqURL;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public long getDefaultVideoResolution() {
        return defaultVideoResolution;
    }

    public void setDefaultVideoResolution(long defaultVideoResolution) {
        this.defaultVideoResolution = defaultVideoResolution;
    }

    public long getDefaultSubtitle() {
        return defaultSubtitle;
    }

    public void setDefaultSubtitle(long defaultSubtitle) {
        this.defaultSubtitle = defaultSubtitle;
    }

    public String getUserAgreementPage() {
        return userAgreementPage;
    }

    public void setUserAgreementPage(String userAgreementPage) {
        this.userAgreementPage = userAgreementPage;
    }

    public long getPaidPackageId() {
        return paidPackageId;
    }

    public void setPaidPackageId(long paidPackageId) {
        this.paidPackageId = paidPackageId;
    }
}
