/**
 * Created by Chatura Dilan Perera on 20/8/16.
 */
package lk.mobilevisions.kiki.app;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

import lk.mobilevisions.kiki.BuildConfig;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.StreamSharedPref;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsModule;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.ComAPI;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;
import lk.mobilevisions.kiki.modules.auth.AuthModule;
import lk.mobilevisions.kiki.modules.info.InfoModule;
import lk.mobilevisions.kiki.modules.notifications.NotificationModule;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsModule;
import lk.mobilevisions.kiki.modules.tv.TvModule;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.GzipSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class Application extends android.app.Application {

    lk.mobilevisions.kiki.app.Injector injector;

    public static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    public static lk.mobilevisions.kiki.app.GlobalPlayer PLAYER;

    private AuthUser authUser;
    private ForgotPasswordUser forgotPasswordUser;

    private OkHttpClient.Builder httpClient;

    private OkHttpClient.Builder httpClientCom;

    private Retrofit retrofit;

    private Retrofit retrofitCom;

    private Session session;
    private List<Integer> songsAddedToPlaylist = new ArrayList<>();
    private List<Integer> allSongsAddedPlaylistId = new ArrayList<>();

    private int selectedChannel;
    private static Application sInstance = null;
    private Context mContext;
    public static final String MIXPANEL_TOKEN = "49e61f5bcb22987cfd253c9e6e7e7dbc";
    private FirebaseAnalytics mFirebaseAnalytics;
    private MixpanelAPI mixpanel;
    private String sessionId;

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(new FlowConfig.Builder(this).build());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        sInstance = this;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
//                FirebaseCrash.report(e);
                Timber.e(e, "Uncaught Exception");
            }
        });

        PLAYER = lk.mobilevisions.kiki.app.GlobalPlayer.getInstance(this);

        session = new Session();
        StreamSharedPref.getInstance(this).resetStreamInfo();
        StreamSharedPref.getInstance(this).setStreamUrlFetchedStatus(false);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(this, new Crashlytics());
        initImageLoader(getApplicationContext());

        mixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
    }

    public static Application getInstance() {
        return sInstance;
    }

    //    public Application(Context mContext) {
//        this.mContext = mContext;
//
//
//    }
//    public static Application getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new Application(context);
//        }
//        return mInstance;
//    }
    public MixpanelAPI getMixpanelAPI() {

        if (mixpanel != null) {
            return mixpanel;
        }
        return null;
    }

    public FirebaseAnalytics getAnalytics() {

        if (mFirebaseAnalytics != null) {
            return mFirebaseAnalytics;
        }
        return null;
    }

    public void addSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    public String getSessionId(){
        return sessionId;
    }

    public void addSongToPlayList(Integer id){
        songsAddedToPlaylist.add(id);
    }
    public void addPlayListId(Integer id){
        allSongsAddedPlaylistId.add(id);
    }
    public void clearPlayList(){
        songsAddedToPlaylist.clear();
    }
    public void clearPlayListIds(){
        allSongsAddedPlaylistId.clear();
    }
    public void removeSongFromPlayList(Integer id){
        songsAddedToPlaylist.remove(id);
    }
    public void removePlayListId(Integer id){
        allSongsAddedPlaylistId.remove(id);
    }
    public List<Integer> getSongsAddedToPlaylist(){
        return songsAddedToPlaylist;
    }
    public List<Integer> getPlaylistIds(){
        return allSongsAddedPlaylistId;
    }
    public void initializeApp() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(lk.mobilevisions.kiki.app.Settings.RETROFIT_LOG_LEVEL);
        httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.connectTimeout(20, TimeUnit.SECONDS);
        httpClientCom = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClientCom.readTimeout(10, TimeUnit.SECONDS);
        httpClientCom.connectTimeout(10, TimeUnit.SECONDS);
        httpClientCom.addInterceptor(logging);
        //httpClientCom.addInterceptor(new UnzippingInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(getConfig().getServerURL())
//                .baseUrl("http://35.200.234.252:8080/mobile-tv-webservice/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        API api = retrofit.create(API.class);

        retrofitCom = new Retrofit.Builder()
                .baseUrl("http://220.247.201.206:90")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientCom.build())
                .build();

        ComAPI comAPI = retrofitCom.create(ComAPI.class);

        injector = DaggerInjector.builder()
                .authModule(new AuthModule(this, api))
                .tvModule(new TvModule(this, api))
                .subscriptionsModule(new SubscriptionsModule(this, api))
                .analyticsModule(new AnalyticsModule(this, api))
                .notificationModule(new NotificationModule(this, api))
                .infoModule(new InfoModule(this, comAPI))
                .build();
    }

    private class UnzippingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return unzip(response);
        }

        private Response unzip(final Response response) throws IOException {

            if (response.body() == null) {
                return response;
            }

            GzipSource responseBody = new GzipSource(response.body().source());
            Headers strippedHeaders = response.headers().newBuilder()
                    .removeAll("Content-Encoding")
                    .removeAll("Content-Length")
                    .build();
            return response.newBuilder()
                    .headers(strippedHeaders)
                    .body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody)))
                    .build();
        }
    }

    public lk.mobilevisions.kiki.app.Injector getInjector() {
        return injector;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public ForgotPasswordUser getForgotPasswordUser() {
        return forgotPasswordUser;
    }

    public Config getConfig() {
        return lk.mobilevisions.kiki.app.Config.getInstance();
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    public void setForgotPasswordUser(ForgotPasswordUser forgotPasswordUser) {
        this.forgotPasswordUser = forgotPasswordUser;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public Session getSession() {
        return session;
    }

    public int getSelectedChannel() {
        return selectedChannel;
    }

    public void setSelectedChannel(int selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


}
