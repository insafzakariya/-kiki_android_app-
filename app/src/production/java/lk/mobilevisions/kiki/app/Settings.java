/**
 * Created by Chatura Dilan Perera on 28/8/2016.
 */
package lk.mobilevisions.kiki.app;


import okhttp3.logging.HttpLoggingInterceptor;
import lk.mobilevisions.kiki.BuildConfig;
import lk.mobilevisions.kiki.R;

public class Settings {

    public static final HttpLoggingInterceptor.Level RETROFIT_LOG_LEVEL = BuildConfig.RETROFIT_LOG_LEVEL;
    public static final int APP_ID = BuildConfig.APP_ID;

    public static final String MAIN_SERVER_API_NAME = BuildConfig.MAIN_SERVER_API_NAME;
    public static final String MOBILE_PAYMENT_GATEWAY_NAME = BuildConfig.MOBILE_PAYMENT_GATEWAY_NAME;
    public static final int MAIN_SERVER_API_VERSION = BuildConfig.MAIN_SERVER_API_VERSION;

    public static final String SHARE_EPISODES_URI = "episode/";

    public static final boolean DISABLE_SCREEN_CAPTURING = BuildConfig.DISABLE_SCREEN_CAPTURING;


    public static class Auth {
        public static class Twitter {
            public static final String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
        }
    }

    public static class Database {
        public static final String SUSILA_DATABASE_NAME = "susila_db";
        public static final int SUSILA_DATABASE_VERSION = 2;
    }

    public static class Firebase {
        public static final long CACHE_EXPIRATION_TIME = 0;
    }

    public static class Menu {
        public static final String[] MENU_TITLES = {"Home", "Hot Movies", "Live Videos",  "Subscriptions"};
        public static final int[] MENU_ICONS = {R.drawable.ic_menu_home, R.drawable.ic_menu_hot_videos,
                R.drawable.ic_menu_live_videos, R.drawable.ic_menu_subscriptions};
    }

    public static class SplashScreen {
        public static final int SPLASH_TIMER = 0;
    }

    public static class Date {
        public static final String FORMAT = "yyyy-MM-dd";
    }

}
