package lk.mobilevisions.kiki.audio.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;

import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.BuildConfig;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.adapter.AddSongToPlaylistDialogAdapter;
import lk.mobilevisions.kiki.audio.adapter.ArtistListAdapter;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.LatestPlaylistAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistDialogAdapter;
import lk.mobilevisions.kiki.audio.adapter.PopularSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.RecentlyPlayedVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.SearchSuggestionAdapter;
import lk.mobilevisions.kiki.audio.adapter.SongsAdapter;
import lk.mobilevisions.kiki.audio.adapter.YouAlsoMightLikeVerticalAdapter;
import lk.mobilevisions.kiki.audio.event.SearchNavigationEvent;
import lk.mobilevisions.kiki.audio.event.UserNavigateBackEvent;
import lk.mobilevisions.kiki.audio.fragment.ArtistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.AudioHomeFragment;
import lk.mobilevisions.kiki.audio.fragment.AudioSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.BrowseAllSongsFrangment;
import lk.mobilevisions.kiki.audio.fragment.LibraryFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryPlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibrarySongSelectionFragment;
import lk.mobilevisions.kiki.audio.fragment.PlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.ProgrammesFragment;
import lk.mobilevisions.kiki.audio.fragment.RecentlyPlayedFragment;
import lk.mobilevisions.kiki.audio.fragment.SearchedArtistFragment;
import lk.mobilevisions.kiki.audio.fragment.SearchedPlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.SearchedPlaylistFragment;
import lk.mobilevisions.kiki.audio.fragment.SearchedSongsFragment;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.SearchResponse;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.player.AudioStreamingManager;
import lk.mobilevisions.kiki.audio.player.CurrentSessionCallback;
import lk.mobilevisions.kiki.audio.player.Logger;
import lk.mobilevisions.kiki.audio.util.CustomTypefaceSpan;
import lk.mobilevisions.kiki.audio.util.SharedPrefrenceUtils;
import lk.mobilevisions.kiki.audio.util.SlidingUpPanelLayoutOne;
import lk.mobilevisions.kiki.audio.widgets.PersistentSearchView;
import lk.mobilevisions.kiki.audio.widgets.SimpleAnimationListener;
import lk.mobilevisions.kiki.audio.widgets.Slider;
import lk.mobilevisions.kiki.databinding.ActivityDashboadNewBinding;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsManager;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.notifications.NotificationManager;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.base.BaseActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import lk.mobilevisions.kiki.video.activity.VideoChildModeActivity;
import lk.mobilevisions.kiki.video.activity.VideoDashboardActivity;
import timber.log.Timber;

import static com.androidquery.util.AQUtility.getContext;
import static com.facebook.FacebookSdk.getApplicationContext;


public class AudioDashboardActivity extends BaseActivity implements CurrentSessionCallback, Slider.OnValueChangedListener,
        RecentlyPlayedVerticalAdapter.RecentlyPlayedItemActionListener,
        YouAlsoMightLikeVerticalAdapter.OnYouMightAlsoLikeItemActionListener,
        PopularSongsVerticalAdapter.OnPopularSongsItemActionListener,
        DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener,
        PlaylistDialogAdapter.OnAudioPlaylistItemClickListener,
        SongsAdapter.OnAudioSongsItemClickListener,
        ArtistsVerticalAdapter.OnArtistsItemActionListener,
        AddSongToPlaylistDialogAdapter.OnPlaylistDialogItemClickListener,
        ArtistListAdapter.OnArtistListItemActionListener,
        LatestPlaylistAdapter.OnLatestPlaylistItemClickListener,
        SearchSuggestionAdapter.OnSearchSugTextviewItemClickListener {


    ActivityDashboadNewBinding binding;
    @Inject
    TvManager tvManager;
    @Inject
    SubscriptionsManager subscriptionsManager;

    private AudioHomeFragment fragmentOne;
    private BrowseAllSongsFrangment fragmentTwo;
    private LibraryFragment fragmentThree;
    //    private ChannelFragment fragmentFour;
//    private VideosFragment fragmentFive;
    private AudioSongsFragment fragmentSix;

    private boolean isHomeLoaded;
    private boolean isFavouriteLoaded;
    private boolean isPlaylistLoaded;
    private boolean isChannelLoaded;
    private boolean isVideosLoaded;
    private TabLayout tabLayout;
    RelativeLayout addPlaylistLayout;
    TextView cancelTextview;
    TextView titleTextview;
    ImageView drawerImageView;
    private Animation animShow, animHide;
    Menu menu;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isExpand = false;
    private int songPosition;
    private TextView textViewHeader;
    private AudioStreamingManager streamingManager;
    private Song currentSong;
    private List<Song> listOfSongs = new ArrayList<Song>();
    private List<Song> suggestedSongList = new ArrayList<Song>();
    private List<Genre> listOfGenres = new ArrayList<Genre>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean itemEventClicked;
    private boolean shouldBottomPlayerPlay;
    private boolean repeatCurrentSong;
    private boolean isSuffleSongs;

    private String currentSongTime;
    private Song song;
    private AlertDialog alertDialog;
    private int playlistId;
    List<Integer> songId = new ArrayList<>();
    String searchKeyWord;

    private String alertTitle;
    private String messageBody;
    private boolean trialStatus;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;
    int songDuration;
    int lastsongDuration;
    private boolean isItemClicked;
    private SharedPrefrenceUtils utils;
    AlertDialog selectPlaylistAlertDialog;
    final ArrayList<Integer> songIdsToCreatePlaylist = new ArrayList<>();
    SongsAdapter mAdapter;
    ArtistListAdapter artistListAdapter;
    LatestPlaylistAdapter playlistAdapter;
    LinearLayoutManager songsLayoutManager;
    List<Song> searchedSongsList = new ArrayList<>();
    List<Artist> artistsArrayList = new ArrayList<>();
    List<Artist> nArtistsArrayList = new ArrayList<>();
    List<PlayList> playListArrayList = new ArrayList<>();
    List<PlayList> nPlayListArrayList = new ArrayList<>();
    List<String> searchSugList = new ArrayList<>();
    SearchSuggestionAdapter searchSuggestionAdapter;
    @Inject
    NotificationManager notificationManager;
    @Inject
    AnalyticsManager analyticsManager;
    private SongAdapter songAdapter;

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private HashMap<String, Object> firebaseDefaultMap;
    public static final String VERSION_CODE_KEY = "force_update_current_version";
    public static final String APP_UPDATE_URL = "force_update_store_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboad_new);
        AppCenter.start(getApplication(), "867537ba-cfac-4794-a7ec-b8eabeca3288",
                Analytics.class, Crashes.class);
        utils = SharedPrefrenceUtils.getInstance(this);
        ((Application) getApplication()).getInjector().inject(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art)
                .showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
            }

            /** Called when a drawer has settled in a completely open state. */


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                String name = null;
                if (((Application) getApplication()).getAuthUser() != null
                        && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
//                    textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.welcome_textview);
//                    textViewHeader = (TextView) binding.welcomeTextview;
                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }
                    binding.welcomeTextview.setText(getString(R.string.welcome_user, name));
                }


            }
        };
//        binding.drawerLayout.addDrawerListener(drawerToggle);
//        bindWidgetsWithAnEvent();
//        setupTabLayout();
        checkTrialStatus();
        binding.subLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trialStatus) {
                    Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioTrialActivationActivity.class);
                    startActivity(intentPackages);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioPaymentActivity.class);
                    startActivity(intentPackages);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        binding.settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioSettingsActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
//                throw new RuntimeException("Test Crash");
            }
        });

        binding.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioContactActivity.class);
                startActivity(intentPackages);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLogoutDialog();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        binding.proPicImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

//        binding.navigationView.setItemIconTintList(null);
//        binding.navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        System.out.println("check " + 123123);
//                        menuItem.setCheckable(false);
//                        selectDrawerItem(menuItem);
//                        return true;
//                    }
//                });

        changeNavigtionItemFont();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        songsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.includeDashboard.searchSongsRecyclerview.setLayoutManager(songsLayoutManager);
        binding.includeDashboard.searchSongsRecyclerview.setHasFixedSize(true);
        binding.includeDashboard.searchSongsRecyclerview.setItemViewCacheSize(1000);
        binding.includeDashboard.searchSongsRecyclerview.setDrawingCacheEnabled(true);
        mAdapter = new SongsAdapter(this, this);
        binding.includeDashboard.searchSongsRecyclerview.setAdapter(mAdapter);

        songsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.includeDashboard.searchArtistRecyclerview.setLayoutManager(songsLayoutManager);
        binding.includeDashboard.searchArtistRecyclerview.setHasFixedSize(true);
        binding.includeDashboard.searchArtistRecyclerview.setItemViewCacheSize(1000);
        binding.includeDashboard.searchArtistRecyclerview.setDrawingCacheEnabled(true);
        artistListAdapter = new ArtistListAdapter(this, artistsArrayList, this);
        binding.includeDashboard.searchArtistRecyclerview.setAdapter(artistListAdapter);

        songsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.includeDashboard.searchPlaylistRecyclerview.setLayoutManager(songsLayoutManager);
        binding.includeDashboard.searchPlaylistRecyclerview.setHasFixedSize(true);
        binding.includeDashboard.searchPlaylistRecyclerview.setItemViewCacheSize(1000);
        binding.includeDashboard.searchPlaylistRecyclerview.setDrawingCacheEnabled(true);
        playlistAdapter = new LatestPlaylistAdapter(this, playListArrayList, this);
        binding.includeDashboard.searchPlaylistRecyclerview.setAdapter(playlistAdapter);

        songsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.includeDashboard.recentSearchRecyclerview.setLayoutManager(songsLayoutManager);
        binding.includeDashboard.recentSearchRecyclerview.setHasFixedSize(true);
        binding.includeDashboard.recentSearchRecyclerview.setItemViewCacheSize(1000);
        binding.includeDashboard.recentSearchRecyclerview.setDrawingCacheEnabled(true);
        searchSuggestionAdapter = new SearchSuggestionAdapter(this, searchSugList, this);
        binding.includeDashboard.recentSearchRecyclerview.setAdapter(searchSuggestionAdapter);


        addPlaylistLayout = (RelativeLayout) findViewById(R.id.add_play_list_layout);
        cancelTextview = (TextView) findViewById(R.id.textView13);
        drawerImageView = (ImageView) findViewById(R.id.pro_pic_imageview);
        titleTextview = (TextView) findViewById(R.id.tool_bar_title_textview);
        animShow = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        animHide = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bindWidgetsWithAnEvent();
        setupTabLayout();
        String name = null;
        if (((Application) getApplication()).getAuthUser() != null
                && (name = ((Application) getApplication()).getAuthUser().getName()) != null) {
//            textViewHeader = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.welcome_textview);
            if (name.contains(" ")) {
                name = name.substring(0, name.indexOf(" "));
            }
            binding.welcomeTextview.setText(getString(R.string.welcome_user, name));
        }
        binding.proPicImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        cancelTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddToPlaylistLayout();

            }
        });

        binding.notificationImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNotifications = new Intent(AudioDashboardActivity.this, AudioNotificationActivity.class);
                startActivity(intentNotifications);
            }
        });
        binding.includeDashboard.includedToggle.videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSong != null) {
                    if (streamingManager != null) {
                        streamingManager.onPause();
                        streamingManager.clearService();
                    }

                }

                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibe.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibe.vibrate(50);
                }

//                final MaterialDialog progressDialog = createProgressDialog();
//                if (((Activity) AudioDashboardActivity.this).hasWindowFocus()) {
//                    progressDialog.show();
//                }

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                Intent intent = new Intent(AudioDashboardActivity.this, VideoDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        progressDialog.dismiss();
                startActivity(intent);
                finish();
//  }
//                }, 1000);

            }
        });
        binding.includeDashboard.addPlaylistTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.includeDashboard.aviProgressPlaylist.setVisibility(View.VISIBLE);
                tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
                    @Override
                    public void onSuccess(List<PlayList> playLists, List<Object> params) {
                        binding.includeDashboard.aviProgressPlaylist.setVisibility(View.GONE);
                        if (playLists.size() == 0) {
                            noPlaylistFoundDialog();
                        } else {
                            selectPlaylistDialog(playLists);
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(AudioDashboardActivity.this, t);
                    }
                });
            }
        });
        binding.includeDashboard.seeAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("searchKey", searchKeyWord);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                SearchedSongsFragment searchedSongsFragment = new SearchedSongsFragment();
                searchedSongsFragment.setArguments(bundle);

                transaction.add(R.id.frame_container_search, searchedSongsFragment, "SearchedSongsFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                binding.searchImageview.setVisibility(View.INVISIBLE);
                binding.includeDashboard.searchLayout.setVisibility(View.GONE);
                binding.includeDashboard.searchview.setVisibility(View.GONE);

            }
        });
        binding.includeDashboard.seeAllArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("searchKey", searchKeyWord);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                SearchedArtistFragment searchedArtistFragment = new SearchedArtistFragment();
                searchedArtistFragment.setArguments(bundle);

                transaction.add(R.id.frame_container_search, searchedArtistFragment, "SearchedArtistFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                binding.searchImageview.setVisibility(View.INVISIBLE);
                binding.includeDashboard.searchLayout.setVisibility(View.GONE);
                binding.includeDashboard.searchview.setVisibility(View.GONE);

            }
        });
        binding.includeDashboard.seeAllPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("searchKey", searchKeyWord);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                SearchedPlaylistFragment searchedPlaylistFragment = new SearchedPlaylistFragment();
                searchedPlaylistFragment.setArguments(bundle);

                transaction.add(R.id.frame_container_search, searchedPlaylistFragment, "SearchedPlaylistFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                binding.searchImageview.setVisibility(View.INVISIBLE);
                binding.includeDashboard.searchLayout.setVisibility(View.GONE);
                binding.includeDashboard.searchview.setVisibility(View.GONE);


            }
        });

        binding.includeDashboard.createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlaylistDialog();

            }
        });
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            binding.includeDashboard.searchview.setVoiceRecognitionDelegate(delegate);
        }
        binding.searchImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.includeDashboard.searchview.isSearching()) {
                    binding.includeDashboard.searchview.closeSearch();
                } else {
                    openSearch();
                }
                if (binding.includeDashboard.searchview.isSearching()) {
                    binding.slidingLayout.setPanelState(SlidingUpPanelLayoutOne.PanelState.COLLAPSED);
                }

            }
        });

        binding.includeDashboard.viewSearchTint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.includeDashboard.searchview.cancelEditing();
            }
        });

        binding.includeSlidingPanelChildtwo.slideBottomView.getRoot().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                binding.slidingLayout.setPanelState(SlidingUpPanelLayoutOne.PanelState.EXPANDED);
            }
        });

        binding.slidingLayout.setPanelSlideListener(new SlidingUpPanelLayoutOne.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    binding.includeSlidingPanelChildtwo.slideBottomView.getRoot().setVisibility(View.VISIBLE);
                    //slideBottomView.getBackground().setAlpha(0);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    //slideBottomView.getBackground().setAlpha((int) slideOffset * 255);
                } else {
                    //slideBottomView.getBackground().setAlpha(100);
                    isExpand = true;
                    binding.includeSlidingPanelChildtwo.slideBottomView.getRoot().setVisibility(View.GONE);
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.includeSlidingPanelChildtwo.slideBottomView.songProgressNormal.getLayoutParams();
        binding.includeSlidingPanelChildtwo.slideBottomView.songProgressNormal.measure(0, 0);
        layoutParams.setMargins(0, -(binding.includeSlidingPanelChildtwo.slideBottomView.songProgressNormal.getMeasuredHeight() / 2), 0, 0);
        binding.includeSlidingPanelChildtwo.slideBottomView.songProgressNormal.setLayoutParams(layoutParams);
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "AudioDashboardActivity", null);
        configAudioStreamer();
        checkAlreadyPlaying();
        binding.includeSlidingPanelChildtwo.audioProgressControl.setMax(0);
        binding.includeSlidingPanelChildtwo.audioProgressControl.setOnValueChangedListener(this);

        binding.includeSlidingPanelChildtwo.songPicker.setOrientation(DSVOrientation.HORIZONTAL);
        binding.includeSlidingPanelChildtwo.songPicker.addOnItemChangedListener(this);
        binding.includeSlidingPanelChildtwo.songPicker.setItemTransitionTimeMillis(600);
        binding.includeSlidingPanelChildtwo.songPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());


        binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setOnClickListener(this);
        binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setOnClickListener(this);
        binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setOnClickListener(this);
        binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setOnClickListener(this);

        binding.includeDashboard.searchview.setHomeButtonColor(Color.parseColor("#000000"));
        binding.includeDashboard.searchview.setSearchListener(new PersistentSearchView.SearchListener() {


            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen
                binding.includeDashboard.viewSearchTint.setVisibility(View.VISIBLE);
                getSearchSuggestions();
                binding.includeDashboard.viewSearchTint
                        .animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener())
                        .start();

            }

            @Override
            public void onSearchEditClosed() {
                binding.includeDashboard.viewSearchTint
                        .animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                binding.includeDashboard.viewSearchTint.setVisibility(View.GONE);
                                binding.searchImageview.setVisibility(View.VISIBLE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                if (binding.includeDashboard.searchview.isEditing()) {
                    binding.includeDashboard.searchview.cancelEditing();
                    binding.includeDashboard.searchLayout.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }

            @Override
            public void onSearchExit() {
                binding.includeDashboard.searchLayout.setVisibility(View.GONE);
                binding.searchImageview.setVisibility(View.VISIBLE);
                Application.BUS.post(new UserNavigateBackEvent());
            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String string) {
                searchKeyWord = string;
                System.out.println("sjldsdjksdd " + searchKeyWord);
                System.out.println("dnbdfbfhfhfn " + string);
                searchSongs(searchKeyWord);

            }


            @Override
            public void onSearchCleared() {

            }

        });

        binding.includeSlidingPanelChildtwo.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.slidingLayout.setPanelState(SlidingUpPanelLayoutOne.PanelState.COLLAPSED);
            }
        });

        binding.includeSlidingPanelChildtwo.audioForwardImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamingManager.onSkipToNext(false);

            }
        });
        binding.includeSlidingPanelChildtwo.audioRewindImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamingManager.onSkipToPrevious();
            }
        });
        binding.includeSlidingPanelChildtwo.audioRepeatImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRepeatClicked();

            }
        });
        binding.includeSlidingPanelChildtwo.audioSuffleImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSuffleClicked();

            }
        });
//        binding.includeSlidingPanelChildtwo.addSongs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                addToLibraryDialog(song);
//            }
//        });

        binding.includeSlidingPanelChildtwo.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToLibraryDialog(song);
            }
        });


        binding.includeSlidingPanelChildtwo.audioReplyImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOfSongs.size() == 1) {
                    streamingManager.onReplayAgain();
                } else {
                    binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(0);
                }
            }
        });
        binding.includeSlidingPanelChildtwo.slideBottomView.replyImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOfSongs.size() == 1) {
                    streamingManager.onReplayAgain();
                } else {
                    binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(0);
                }


            }
        });
        binding.includeDashboard.closePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addPlaylistLayout.getVisibility() == View.VISIBLE) {
                    addPlaylistLayout.startAnimation(animHide);
                    addPlaylistLayout.setVisibility(View.GONE);
                }

            }
        });

        binding.slidingLayout.setEnableDragViewTouchEvents(true);
//        subscriptionDialog();

        if (utils.getLastItemThumbnail().length() > 0) {
            binding.includeSlidingPanelChildtwo.slideBottomView.welcomeLayout.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.youMightLayout.setVisibility(View.VISIBLE);
            if (streamingManager.isPlaying()) {
                currentSong = streamingManager.getCurrentAudio();
                binding.slidingLayout.setEnabled(true);
                binding.slidingLayout.setClickable(true);
            } else {

                binding.slidingLayout.setEnabled(false);
                binding.slidingLayout.setClickable(false);
            }
            shouldBottomPlayerPlay = true;
            prepareBottomPlayer();
        } else {
            // user has not played any song so disable to scroll bottom view
            binding.includeSlidingPanelChildtwo.slideBottomView.welcomeLayout.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.youMightLayout.setVisibility(View.GONE);
            binding.slidingLayout.setEnabled(false);
            binding.slidingLayout.setClickable(false);
        }
//        utils.getLastItemStreamUrl();
//        System.out.println("sdfsdssdfsd " + utils.getLastItemStreamUrl());

        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Audio_Screen");

        firebaseDefaultMap = new HashMap<>();
        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
        mFirebaseRemoteConfig.setDefaults(firebaseDefaultMap);

        mFirebaseRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build());

        //Fetching the values here
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activateFetched();

                    //calling function to check if new version is available or not
                    checkForUpdate();
                } else {
                    Toast.makeText(AudioDashboardActivity.this, "Something went wrong please try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkForUpdate() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        String updateStoreUrl = (String) mFirebaseRemoteConfig.getString(APP_UPDATE_URL);

        if (latestAppVersion > getCurrentVersionCode()) {
            new AlertDialog.Builder(this).setTitle("Please Update the App")
                    .setMessage("Currently a new version of KiKi app is available.")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateStoreUrl));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).setCancelable(false).show();
        }
    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void subscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        RelativeLayout createTextView = (RelativeLayout) alertDialog.findViewById(R.id.subscribe_now_layout);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.go_back_textview);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioPaymentActivity.class);
                startActivity(intentPackages);
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
//                streamingManager.onSkipToNext();   //todo create new method without skip to single clicks in home
            }
        });
    }

    private void checkTrialStatus() {
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(this.getApplication()).getPaidPackageId(),
                new APIListener<PackageToken>() {
                    @Override
                    public void onSuccess(final PackageToken packageToken, List<Object> params) {

                        subscriptionsManager.getTrialStatus(new APIListener<PackageV2>() {
                            @Override
                            public void onSuccess(PackageV2 thePackage, List<Object> params) {
//                                binding.aviProgress.setVisibility(View.GONE);
                                alertTitle = thePackage.getTitleText();
                                messageBody = thePackage.getMessageBody();
                                trialStatus = thePackage.isTrialStatus();
                                System.out.println("hdfhdfhdh " + alertTitle);
                                System.out.println("hdfhdfhdh 111  " + messageBody);
//                        if (thePackage.getPackageId() == 46 || thePackage.getPackageId() == 81 ||
//                                thePackage.getPackageId() == 101 || thePackage.getPackageId() == 106) {
//
//                        } else {
//                            try {
//                                subscriptionDialog();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }

                                if (thePackage.isTrialStatus() && !thePackage.isSubStatus()) {

                                    try {
                                        trialSubscriptionDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {


                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {


                    }
                });
    }

    private void trialSubscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_trial_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(dialogView);
        dialogView.setClipToOutline(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        RelativeLayout tryTextview = (RelativeLayout) alertDialog.findViewById(R.id.try_now_layout);
        ImageView closeImageView = (ImageView) alertDialog.findViewById(R.id.close_imageview);
        TextView title = (TextView) alertDialog.findViewById(R.id.alert_title);
        title.setText(alertTitle);
        TextView body = (TextView) alertDialog.findViewById(R.id.message_body_text);
        body.setText(messageBody);


        tryTextview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intentPackages = new Intent(AudioDashboardActivity.this, AudioTrialActivationActivity.class);
                startActivity(intentPackages);
            }
        });
        closeImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void searchSongs(String text) {
        binding.includeDashboard.seeAllSongs.setEnabled(false);
        binding.includeDashboard.seeAllArtists.setEnabled(false);
        binding.includeDashboard.seeAllPlaylists.setEnabled(false);
        binding.includeDashboard.aviProgress.setVisibility(View.VISIBLE);
        binding.includeDashboard.noSongsTextView.setVisibility(View.GONE);
        tvManager.getSearchedAll(text, new APIListener<SearchResponse>() {
            @Override
            public void onSuccess(SearchResponse response, List<Object> params) {
                System.out.println("dhdhdhdhdhdh 000  ");
                binding.includeDashboard.aviProgress.setVisibility(View.GONE);
                binding.includeDashboard.searchLayout.setVisibility(View.VISIBLE);
                searchedSongsList.clear();
                if (response.getSongList().size() > 0) {
                    binding.includeDashboard.searchSongsLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.includeDashboard.searchSongsLayout.setVisibility(View.GONE);
                }
                if (response.getSongList() != null && response.getSongList().size() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        searchedSongsList.add(response.getSongList().get(i));
                    }
                } else {
                    if (response.getSongList() != null && response.getSongList().size() <= 3) {
                        for (int i = 0; i < response.getSongList().size(); i++) {
                            searchedSongsList.add(response.getSongList().get(i));
                        }
                    }
                }
                if (response.getSongList() != null && response.getSongList().size() <= 3) {
                    binding.includeDashboard.seeAllSongs.setVisibility(View.GONE);
                } else {
                    binding.includeDashboard.seeAllSongs.setVisibility(View.VISIBLE);
                }

//                searchedSongsList.addAll(response.getSongList());
                mAdapter.setData(searchedSongsList);

                nArtistsArrayList.clear();
                artistsArrayList.clear();
                if (response.getArtistList().size() > 0) {
                    binding.includeDashboard.searchArtistLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.includeDashboard.searchArtistLayout.setVisibility(View.GONE);
                }
                if (response.getArtistList() != null && response.getArtistList().size() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        nArtistsArrayList.add(response.getArtistList().get(i));
                    }
                } else {
                    if (response.getArtistList() != null && response.getArtistList().size() <= 3) {
                        for (int i = 0; i < response.getArtistList().size(); i++) {
                            nArtistsArrayList.add(response.getArtistList().get(i));
                            System.out.println("shdbhdcvh " + searchedSongsList.size());
                        }
                    }
                }
                if (response.getArtistList() != null && response.getArtistList().size() <= 3) {
                    binding.includeDashboard.seeAllArtists.setVisibility(View.GONE);
                } else {
                    binding.includeDashboard.seeAllArtists.setVisibility(View.VISIBLE);
                }

                artistListAdapter.setList(nArtistsArrayList);

                nPlayListArrayList.clear();
                playListArrayList.clear();
                if (response.getPlaylistList().size() > 0) {
                    binding.includeDashboard.searchPlaylistLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.includeDashboard.searchPlaylistLayout.setVisibility(View.GONE);
                }
                if (response.getPlaylistList() != null && response.getPlaylistList().size() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        nPlayListArrayList.add(response.getPlaylistList().get(i));
                    }
                } else {
                    if (response.getPlaylistList() != null && response.getPlaylistList().size() <= 3) {
                        for (int i = 0; i < response.getPlaylistList().size(); i++) {
                            nPlayListArrayList.add(response.getPlaylistList().get(i));
                            System.out.println("shdbhdcvh " + searchedSongsList.size());
                        }
                    }
                }
                if (response.getPlaylistList() != null && response.getPlaylistList().size() <= 3) {
                    binding.includeDashboard.seeAllPlaylists.setVisibility(View.GONE);
                } else {
                    binding.includeDashboard.seeAllPlaylists.setVisibility(View.VISIBLE);
                }

                playlistAdapter.setList(nPlayListArrayList);

                if (response.getSongList().size() == 0 && response.getArtistList().size() == 0 && response.getPlaylistList().size() == 0) {
                    binding.includeDashboard.noSongsTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.includeDashboard.noSongsTextView.setVisibility(View.GONE);
                }
                binding.includeDashboard.seeAllSongs.setEnabled(true);
                binding.includeDashboard.seeAllArtists.setEnabled(true);
                binding.includeDashboard.seeAllPlaylists.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                binding.includeDashboard.aviProgress.setVisibility(View.GONE);
                System.out.println("dhdhdhdhdhdh 222  " + t.toString());

            }
        });
    }

    private void getSearchSuggestions() {


        tvManager.getSearchSuggestions(new APIListener<List<String>>() {
            @Override
            public void onSuccess(List<String> result, List<Object> params) {
                searchSugList = result;
                binding.includeDashboard.recentSearchRecyclerview.setAdapter(new SearchSuggestionAdapter(getContext(),
                        searchSugList, AudioDashboardActivity.this));
                if (result.size() <= 0) {
                    binding.includeDashboard.recentSearchRecyclerview.setVisibility(View.GONE);
                    binding.includeDashboard.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.includeDashboard.recentSearchRecyclerview.setVisibility(View.VISIBLE);
                    binding.includeDashboard.aviProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }


    @Override
    public void onArtistListItemClick(Artist artist, int position, List<Artist> artistList) {
        System.out.println("sjkdcadcadc 1111");
        Bundle bundle = new Bundle();
        bundle.putInt("artistID", artist.getId());
        System.out.println("sjkdcadcadc 2222" + artist.getId());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());

        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container_search_toArtist, artistDetailFragment, "Search to nArtistDetail")
                .addToBackStack(null)
                .commit();

        binding.searchImageview.setVisibility(View.INVISIBLE);
        binding.includeDashboard.searchLayout.setVisibility(View.GONE);
        binding.includeDashboard.searchview.setVisibility(View.GONE);
    }

    @Override
    public void onAddArtistItemClick(Artist artist) {

        addArtistToLibrary(artist.getId());
    }

    @Override
    public void onLatestPlaylistItemClick(PlayList playList, int position, List<PlayList> songs) {

        Bundle bundle = new Bundle();
        bundle.putInt("playlistID", playList.getId());
        bundle.putString("playlistName", playList.getName());
        bundle.putString("songCount", playList.getSongCount());
        bundle.putString("playlistImage", playList.getImage());
        bundle.putString("playlistYear", playList.getDate());

        SearchedPlaylistDetailFragment playlistDetailFragment = new SearchedPlaylistDetailFragment();
        playlistDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container_search_toPlaylist, playlistDetailFragment, "Search to nPlaylistDetail")
                .addToBackStack(null)
                .commit();

        binding.searchImageview.setVisibility(View.INVISIBLE);
        binding.includeDashboard.searchLayout.setVisibility(View.GONE);
        binding.includeDashboard.searchview.setVisibility(View.GONE);
    }

    private void addArtistToLibrary(int artistID) {

        ArrayList<Integer> artistId = new ArrayList<>();
        artistId.add(artistID);
        tvManager.addDataToLibraryHash("A", artistId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getApplicationContext(), t);
            }
        });

    }

    private void addPlaylistToLibrary(int playlistID) {

        ArrayList<Integer> playlistId = new ArrayList<>();
        playlistId.add(playlistID);
        tvManager.addDataToLibraryHash("P", playlistId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getApplicationContext(), t);
            }
        });

    }

    @Override
    public void onAddPlaylistItemClick(PlayList playList) {

        addPlaylistToLibrary(playList.getId());
    }

//    private MaterialDialog createProgressDialog() {
//        MaterialDialog dialog = Utils.Dialog.createDialog(this, getString(R.string.please_wait))
//                .cancelable(false)
//                .backgroundColorRes(R.color.dialogProgressBackground)
//                .canceledOnTouchOutside(false)
//                .progress(true, 0)
//                .build();
//        return dialog;
//    }

    @Override
    protected void onResume() {
        Utils.SharedPrefUtil.saveStringToSharedPref(AudioDashboardActivity.this,
                Constants.CURRENT_APP, "audio");
        super.onResume();
    }

    private void getPlaylists() {
        tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {
                binding.includeDashboard.aviProgressPlaylist.setVisibility(View.GONE);
                if (playLists.size() == 0) {
                    noPlaylistFoundDialog();
                } else {
                    selectPlaylistDialog(playLists);

                }

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioDashboardActivity.this, t);
            }
        });
    }

//    private void addSongsToPlaylist(int playlistId) {
//        tvManager.addSongToPlaylist(playlistId, songIdsToCreatePlaylist, new APIListener<Void>() {
//            @Override
//            public void onSuccess(Void result, List<Object> params) {
//                Timber.d("Notifications markd as read %s", songIdsToCreatePlaylist);
//                songIdsToCreatePlaylist.clear();
//                if (selectPlaylistAlertDialog != null && selectPlaylistAlertDialog.isShowing()) {
//                    selectPlaylistAlertDialog.findViewById(R.id.aviProgress).setVisibility(View.GONE);
//                    selectPlaylistAlertDialog.dismiss();
//                }
//                hideAddToPlaylistLayout();
//                songAddedSuccessfullyDialog();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Utils.Error.onServiceCallFail(AudioDashboardActivity.this, t);
//            }
//        });
//    }

    private void createPlaylistDialog() {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_create_playlist, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        final EditText playlistName = (EditText) alertDialog.findViewById(R.id.input_first_name);
        TextView createTextView = (TextView) alertDialog.findViewById(R.id.create_button_text_view);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.cancel_button_text_view);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String name = playlistName.getText().toString();
                if (name != null && !name.isEmpty()) {
//                    createPlayList(name);
                    alertDialog.dismiss();
                } else {

//                    KikiToast.show(AudioDashboardActivity.this,"Please enter the Playlist name",false);
                    Toast.makeText(AudioDashboardActivity.this, "Please enter the Playlist name",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void createLogoutDialog() {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_logout_audio, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView createTextView = (TextView) alertDialog.findViewById(R.id.yes_button_text_view);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.cancel_button_text_view);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                if (streamingManager != null) {
                    streamingManager.cleanupPlayer(false, true);
                }

                AuthManager.logoutFromApp((Application) getApplication());
                Intent logoutIntent = new Intent(AudioDashboardActivity.this, SplashActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();


            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void hideAddToPlaylistLayout() {
        songIdsToCreatePlaylist.clear();
        if (addPlaylistLayout.getVisibility() == View.VISIBLE) {
            addPlaylistLayout.startAnimation(animHide);
            addPlaylistLayout.setVisibility(View.GONE);
        }
    }

    private void noPlaylistFoundDialog() {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_no_playlist, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView createTextView = (TextView) alertDialog.findViewById(R.id.create_button_text_view);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.cancel_button_text_view);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                createPlaylistDialog();

            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void songAddedSuccessfullyDialog() {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_song_added, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView okTextView = (TextView) alertDialog.findViewById(R.id.ok_button_text_view);


        okTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });

    }

    private void playListCreatedSuccessfullyDialog(String name, final int id) {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_playlist_created, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView greetingTextView = (TextView) alertDialog.findViewById(R.id.greeting_text_view);
        greetingTextView.setText("Your Playlist " + name + " have created. Do you want add this song to this playlist or select from playlist?");
        TextView addTextView = (TextView) alertDialog.findViewById(R.id.add_button_text_view);
        TextView listTextView = (TextView) alertDialog.findViewById(R.id.list_button_text_view);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.cancel_button_text_view);


        addTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

//                addSongsToPlaylist(id);


            }
        });
        listTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
//                selectPlaylistDialog();
                getPlaylists();

            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void selectPlaylistDialog(List<PlayList> playLists) {
        LayoutInflater myLayout = LayoutInflater.from(AudioDashboardActivity.this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_playlist, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AudioDashboardActivity.this);
        alertDialogBuilder.setView(dialogView);
        selectPlaylistAlertDialog = alertDialogBuilder.create();
        selectPlaylistAlertDialog.setCanceledOnTouchOutside(false);
        selectPlaylistAlertDialog.show();

        //RECYCER
        RecyclerView rv = (RecyclerView) selectPlaylistAlertDialog.findViewById(R.id.mRecyerID);
        rv.setLayoutManager(new LinearLayoutManager(AudioDashboardActivity.this));

        //ADAPTER
        PlaylistDialogAdapter adapter = new PlaylistDialogAdapter(AudioDashboardActivity.this, playLists, AudioDashboardActivity.this);
        rv.setAdapter(adapter);

        TextView cancelTextView = (TextView) selectPlaylistAlertDialog.findViewById(R.id.cancel_button_text_view);


        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                selectPlaylistAlertDialog.dismiss();


            }
        });
    }

//    private void createPlayList(String name) {
//
//
//        tvManager.createPlaylist(name, new APIListener<PlayList>() {
//            @Override
//            public void onSuccess(PlayList playList, List<Object> params) {
//                playListCreatedSuccessfullyDialog(playList.getName(), playList.getId());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Utils.Error.onServiceCallFail(AudioDashboardActivity.this, t);
//            }
//        });
//    }

    public void openSearch() {

        binding.includeDashboard.searchview.setStartPositionFromMenuItem(binding.searchImageview);
        binding.includeDashboard.searchview.openSearch();
    }

    private void configAudioStreamer() {
        streamingManager = AudioStreamingManager.getInstance(AudioDashboardActivity.this);
        //Set PlayMultiple 'true' if want to playing sequentially one by one songs
        // and provide the list of songs else set it 'false'
        streamingManager.setPlayMultiple(true);
        streamingManager.setMediaList(listOfSongs);
        //If you want to show the Player Notification then set ShowPlayerNotification as true
        //and provide the pending intent so that after click on notification it will redirect to an activity
        streamingManager.setShowPlayerNotification(true);
        streamingManager.setPendingIntentAct(getNotificationPendingIntent());
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent intent = new Intent(this, AudioDashboardActivity.class);
        intent.setAction("openplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return mPendingIntent;
    }


    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navPackages:
                Intent intentPackages = new Intent(this, AudioPaymentActivity.class);
                startActivity(intentPackages);
                break;
            case R.id.navContact:
                Intent contactIntent = new Intent(this, AudioContactActivity.class);
                startActivity(contactIntent);
                break;
            case R.id.navSettings:
                Intent settingsIntent = new Intent(this, AudioSettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.navChild:
                Intent childIntent = new Intent(this, VideoChildModeActivity.class);
                startActivity(childIntent);
                break;
            case R.id.navLogout:
                createLogoutDialog();

        }

        binding.drawerLayout.closeDrawers();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }


    private void setupTabLayout() {

        fragmentOne = new AudioHomeFragment();
        fragmentTwo = new BrowseAllSongsFrangment();
        fragmentThree = new LibraryFragment();
        fragmentSix = new AudioSongsFragment();

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_normal, null);
        tabOne.setText(R.string.home);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_unselected, 0, 0);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_normal, null);
        tabTwo.setText(R.string.browse);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_all_songs_unselected, 0, 0);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_normal, null);
        tabThree.setText(R.string.library);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_playlist_unselected, 0, 0);

        tabLayout.addTab(tabLayout.newTab().setCustomView(tabOne), true);
        tabLayout.addTab(tabLayout.newTab().setCustomView(tabTwo));
        tabLayout.addTab(tabLayout.newTab().setCustomView(tabThree));


    }

    private void bindWidgetsWithAnEvent() {

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            FragmentManager fragmentManager = getSupportFragmentManager();

            @Override
            public void onTabSelected(final TabLayout.Tab tab) {

                setCurrentTabFragment(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        removeFragment();
                        TextView tabOne = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_selected, null);
                        tabOne.setText(R.string.home);
                        titleTextview.setText(R.string.home);
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_selected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabOne);
                        binding.tab1.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.color1000));


                        RecentlyPlayedFragment recentlyPlayedFragment = (RecentlyPlayedFragment) fragmentManager.findFragmentByTag("recently played");
                        if (recentlyPlayedFragment != null && recentlyPlayedFragment.isVisible()) {
                            // add your code here

                            titleTextview.setText("Recently Played");
                        }


                        break;
                    case 1:
                        TextView tabTwo = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_selected, null);
                        tabTwo.setText(R.string.browse);
                        titleTextview.setText(R.string.browse);
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_all_songs_selected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabTwo);
                        binding.tab2.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.color1000));


                        break;
                    case 2:
                        TextView tabThree = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_selected, null);
                        tabThree.setText(R.string.library);
                        titleTextview.setText(R.string.library);
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_playlist_selected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabThree);
                        binding.tab3.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.color1000));

                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        TextView tabOne = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_normal, null);
                        tabOne.setText(R.string.home);
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_unselected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabOne);
                        binding.tab1.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.newUiBackground));


                        break;
                    case 1:
                        TextView tabTwo = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_normal, null);
                        tabTwo.setText(R.string.browse);
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_all_songs_unselected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabTwo);
                        binding.tab2.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.newUiBackground));


                        break;
                    case 2:
                        TextView tabThree = (TextView) LayoutInflater.from(AudioDashboardActivity.this).inflate(R.layout.custom_tab_normal, null);
                        tabThree.setText(R.string.library);
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_playlist_unselected, 0, 0);
                        tabLayout.getTabAt(tab.getPosition()).getCustomView().setVisibility(View.GONE);
                        tabLayout.getTabAt(tab.getPosition()).setCustomView(tabThree);
                        binding.tab3.setBackgroundColor(ContextCompat.getColor(AudioDashboardActivity.this, R.color.newUiBackground));

                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        switch (tabPosition) {
            case 0:
                fragmentOne.setActionListener(this);
                fragmentOne.setYouMightAlsoLikeItemActionListenerActionListener(this);
                fragmentOne.setOnPopularOnSongsItemActionListener(this);
                fragmentOne.setArtistsItemActionListener(this);
                replaceFragment(fragmentOne, "home");
                hideAddToPlaylistLayout();
                break;
            case 1:

                replaceFragment(fragmentTwo, "browse");

                break;
            case 2:

                replaceFragment(fragmentThree, "library");
                fragmentThree.onResume();
                hideAddToPlaylistLayout();
                break;

        }
    }

    public void replaceFragment(Fragment fragment, String _fname) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (_fname) {
            case "home":
                binding.includeDashboard.includeDashboardFrame.frameContainerHome.setVisibility(View.VISIBLE);
                binding.includeDashboard.includeDashboardFrame.frameContainerFavourite.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerPlaylist.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerChannel.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerVideos.setVisibility(View.GONE);
                if (!isHomeLoaded) {
                    fm.executePendingTransactions();
                    ft.add(R.id.frame_container_home, fragment, _fname);
                    isHomeLoaded = true;
                }

                ft.commit();
                break;
            case "browse":
                binding.includeDashboard.includeDashboardFrame.frameContainerHome.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerFavourite.setVisibility(View.VISIBLE);
                binding.includeDashboard.includeDashboardFrame.frameContainerPlaylist.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerChannel.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerVideos.setVisibility(View.GONE);

                if (!isFavouriteLoaded) {
                    fm.executePendingTransactions();
                    ft.add(R.id.frame_container_favourite, fragment, _fname);
                    isFavouriteLoaded = true;
                }

                ft.commit();
                break;
            case "library":
                binding.includeDashboard.includeDashboardFrame.frameContainerHome.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerFavourite.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerPlaylist.setVisibility(View.VISIBLE);
                binding.includeDashboard.includeDashboardFrame.frameContainerChannel.setVisibility(View.GONE);
                binding.includeDashboard.includeDashboardFrame.frameContainerVideos.setVisibility(View.GONE);

                if (!isPlaylistLoaded) {
                    fm.executePendingTransactions();
                    ft.add(R.id.frame_container_playlist, fragment, _fname);
                    isPlaylistLoaded = true;
                }
                ft.detach(fragment);
                ft.attach(fragment);
                ft.commit();
                break;
            default:
                break;
        }

    }

    private void removeFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.getTag() != null) {
                if (fragment.getTag().equals("you might like") || fragment.getTag().equals("popular songs") || fragment.getTag().equals("latest songs")
                        || fragment.getTag().equals("Artists") || fragment.getTag().equals("Home Artist Detail") || fragment.getTag().equals("Home Playlist Detail")
                        || fragment.getTag().equals("Home to ArtistDetail") || fragment.getTag().equals("Home to PlaylistDetail")) {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager nFragmentManager = getSupportFragmentManager();

        LibrarySongSelectionFragment librarySongSelectionFragment = (LibrarySongSelectionFragment) nFragmentManager.findFragmentByTag("librarySongSelection");
        LibraryPlaylistDetailFragment libraryPlaylistDetailFragment = (LibraryPlaylistDetailFragment) nFragmentManager.findFragmentByTag("slectionToDetail");

        if (libraryPlaylistDetailFragment != null && libraryPlaylistDetailFragment.isVisible()) {
            nFragmentManager.popBackStack("getThis", 0);
            System.out.println("backCheck 111 ");
        } else {
            Application.BUS.post(new UserNavigateBackEvent());
            System.out.println("backCheck 222 ");
        }

        if (binding.includeDashboard.searchview.isSearching()) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            SearchedSongsFragment searchedSongsFragment = (SearchedSongsFragment) fragmentManager.findFragmentByTag("SearchedSongsFragment");
            SearchedArtistFragment searchedArtistFragment = (SearchedArtistFragment) fragmentManager.findFragmentByTag("SearchedArtistFragment");
            SearchedPlaylistFragment searchedPlaylistFragment = (SearchedPlaylistFragment) fragmentManager.findFragmentByTag("SearchedPlaylistFragment");
            ArtistDetailFragment artistDetailFragment = (ArtistDetailFragment) fragmentManager.findFragmentByTag("Search to nArtistDetail");
            ArtistDetailFragment nArtistDetailFragment = (ArtistDetailFragment) fragmentManager.findFragmentByTag("SearchedArtistDetail");
            SearchedPlaylistDetailFragment playlistDetailFragment = (SearchedPlaylistDetailFragment) fragmentManager.findFragmentByTag("Search to nPlaylistDetail");
            PlaylistDetailFragment sPlaylistDetailFragment = (PlaylistDetailFragment) fragmentManager.findFragmentByTag("searchedPlaylistDetail");

            if (searchedArtistFragment != null && searchedArtistFragment.isVisible() || searchedSongsFragment != null && searchedSongsFragment.isVisible()
                    || searchedPlaylistFragment != null && searchedPlaylistFragment.isVisible() || artistDetailFragment != null && artistDetailFragment.isVisible()
                    || playlistDetailFragment != null && playlistDetailFragment.isVisible() || sPlaylistDetailFragment != null && sPlaylistDetailFragment.isVisible()
                    || nArtistDetailFragment != null && nArtistDetailFragment.isVisible()) {

                fragmentManager.popBackStack();
                binding.includeDashboard.searchview.setVisibility(View.VISIBLE);
                binding.includeDashboard.searchLayout.setVisibility(View.VISIBLE);

            } else {
                binding.includeDashboard.searchview.closeSearch();
            }


        } else {
            if (isExpand) {

                binding.slidingLayout.setPanelState(SlidingUpPanelLayoutOne.PanelState.COLLAPSED);
            } else if (addPlaylistLayout.getVisibility() == View.VISIBLE) {

                addPlaylistLayout.startAnimation(animHide);
                addPlaylistLayout.setVisibility(View.GONE);
            } else if (addPlaylistLayout.getVisibility() == View.VISIBLE) {

                addPlaylistLayout.startAnimation(animShow);
                addPlaylistLayout.setVisibility(View.VISIBLE);
            } else {

                FragmentManager fragmentManager = getSupportFragmentManager();

                RecentlyPlayedFragment recentlyPlayedFragment = (RecentlyPlayedFragment) fragmentManager.findFragmentByTag("recently played");
                ProgrammesFragment programmesFragment = (ProgrammesFragment) fragmentManager.findFragmentByTag("programes");
                if (recentlyPlayedFragment != null && recentlyPlayedFragment.isVisible()) {
                    fragmentManager.popBackStack();
                    titleTextview.setText("Home");
                } else if (programmesFragment != null && programmesFragment.isVisible()) {
                    fragmentManager.popBackStack();
                    titleTextview.setText("Channel");
                } else {
                    System.out.println("rfjrjffiorfjiorfjiof 2222 ");

                    super.onBackPressed();
                }

            }

        }

    }


    public void changeToolbarName(String name) {
        titleTextview.setText(name);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NeufvilleDigital-FuturaNext-Book.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void changeNavigtionItemFont() {
        Menu m = binding.navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }


    private void playSong(Song media) {
        isItemClicked = true;
        if (streamingManager != null) {
            if (shouldBottomPlayerPlay) {

                if (streamingManager.isPlaying()) {
                    setLastSongDuration(lastsongDuration);
                    binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_pause_black);
                    binding.includeSlidingPanelChildtwo.aviProgress.setVisibility(View.GONE);
                    binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.VISIBLE);
                    binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_pause);
                    binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward_inactive);
                    binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind_inactive);
                } else {
                    binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_play_black);

                }
                binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.slideBottomView.previousGrayoutImageview.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.slideBottomView.nextGrayoutImageview.setVisibility(View.VISIBLE);
            } else {
                if (streamingManager.getCurrentAudio() == null) {
                    streamingManager.onPlay(media);
                } else {
                    if (streamingManager.getCurrentAudio().getId() != media.getId()) {
                        streamingManager.onPlay(media);
                    } else {

                    }
                }


            }
            showSongInfo(media);
        }

    }

    private void checkAlreadyPlaying() {
        if (streamingManager.isPlaying()) {
            currentSong = streamingManager.getCurrentAudio();

        }
    }

    @Override
    public void updatePlaybackState(int state) {
        Logger.e("updatePlaybackState: ", "" + state);
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                sendAnalytics(Constants.PLAYER_AUDIO_START_PLAY);
                binding.includeSlidingPanelChildtwo.slideBottomView.replyImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.aviProgress.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.slideBottomView.aviProgress.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_pause);
                binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_pause_black);

                break;
            case PlaybackStateCompat.STATE_PAUSED:
                sendAnalytics(Constants.PLAYER_AUDIO_PAUSE_PLAY);
                binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_play_black);
                binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_play);

                break;
            case PlaybackStateCompat.STATE_NONE:

                break;
            case PlaybackStateCompat.STATE_STOPPED:
                sendAnalytics(Constants.PLAYER_AUDIO_STOP_PLAY);
                binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_play);
                binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_play_black);
                binding.includeSlidingPanelChildtwo.audioProgressControl.setValue(0);

                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.slideBottomView.aviProgress.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.aviProgress.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.slideBottomView.replyImageview.setVisibility(View.GONE);
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.GONE);

                break;
        }
    }

    private void sendScreenAnalytics(int action) {
        Song currentSong = streamingManager.getCurrentAudio();
        if (currentSong != null) {
            String curntSongId = String.valueOf(currentSong.getId());
            analyticsManager.sendActionAnalytics(9, 16, action, currentSongTime, new APIListener() {
                @Override
                public void onSuccess(Object result, List params) {
//                    System.out.println("sdkbvhasaassdhbv " + currentSongTime);
                }

                @Override
                public void onFailure(Throwable t) {
                    Timber.e(t, "Error while publishing Analytics");
                }
            });
        }


    }

    private void sendAnalytics(String action) {
        Song currentSong = streamingManager.getCurrentAudio();
        if (currentSong != null) {
            String curntSongId = String.valueOf(currentSong.getId());
            analyticsManager.publishActionAnalytics(curntSongId, action, currentSongTime, new APIListener() {
                @Override
                public void onSuccess(Object result, List params) {
                    System.out.println("sdhgadhbsschadbc " + action);
                }

                @Override
                public void onFailure(Throwable t) {
                    Timber.e(t, "Error while publishing Analytics");
                }
            });
        }


    }


    private void setPGTime(int progress) {
        try {
            String timeString = "00:00";
            int linePG = 0;
            currentSong = streamingManager.getCurrentAudio();
            if (currentSong != null && progress != Long.parseLong(String.valueOf(songDuration / 1000))) {
                timeString = DateUtils.formatElapsedTime(progress / 1000);
                currentSongTime = timeString;
                Long audioDuration = Long.parseLong(String.valueOf(songDuration / 1000));
                if (audioDuration != 0)
                    linePG = (int) (((progress / 1000) * 100) / audioDuration);
            }
            binding.includeSlidingPanelChildtwo.songPositionTextview.setText(timeString);
            binding.includeSlidingPanelChildtwo.slideBottomView.songProgressNormal.setProgress(linePG);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playSongComplete() {

        if (listOfSongs.size() == 1) {
            binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setVisibility(View.GONE);
            if (repeatCurrentSong) {
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.GONE);

            } else {
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.slideBottomView.replyImageview.setVisibility(View.VISIBLE);
            }


        } else if (songPosition == listOfSongs.size() - 1) {
            binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setVisibility(View.GONE);

            if (repeatCurrentSong) {
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.GONE);

            } else {
                binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.VISIBLE);
                binding.includeSlidingPanelChildtwo.slideBottomView.replyImageview.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void currentSeekBarPosition(int progress) {
        binding.includeSlidingPanelChildtwo.audioProgressControl.setValue(progress / 1000);
        setPGTime(progress);
    }

    @Override
    public void currentSongDuration(int duration) {
        utils.setCurrentItemDuration(duration);
        int max = duration / 1000;
        binding.includeSlidingPanelChildtwo.audioProgressControl.setMax(max);
        songDuration = duration;
        setPGTime(0);
        setMaxTime();

    }

    private void setLastSongDuration(int duration) {
        int max = duration / 1000;
        binding.includeSlidingPanelChildtwo.audioProgressControl.setMax(max);
        songDuration = duration;
        setPGTime(0);
        setMaxTime();
    }

    @Override
    public void playCurrent(int indexP, Song currentAudio) {
        if (shouldBottomPlayerPlay) {
            setLastSongDuration(lastsongDuration);
        }
        shouldBottomPlayerPlay = false;
        binding.slidingLayout.setEnabled(true);
        binding.slidingLayout.setClickable(true);
        binding.includeSlidingPanelChildtwo.slideBottomView.welcomeLayout.setVisibility(View.GONE);
        binding.includeSlidingPanelChildtwo.slideBottomView.youMightLayout.setVisibility(View.VISIBLE);
        utils.setCurrentItemStreamUrl(currentAudio.getUrl());
        utils.setCurrentItemTitle(currentAudio.getName());
        utils.setCurrentItemThumbnailUrl(currentAudio.getImage());
        utils.setCurrentItemDiscription(currentAudio.getDescription());
        utils.setCurrentItemId(currentAudio.getId());
        songPosition = indexP;
        binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(songPosition);
        showSongInfo(currentAudio);

        if (currentAudio.getUrl() != null && currentAudio.getUrl().contains("radio")) {
            binding.includeSlidingPanelChildtwo.audioProgressControl.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.threeDots.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioRepeatImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioSuffleImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.songDurationTextview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.songPositionTextview.setVisibility(View.GONE);
        } else {
            binding.includeSlidingPanelChildtwo.audioProgressControl.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.threeDots.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.audioRepeatImageview.setVisibility(View.VISIBLE);
//    binding.includeSlidingPanelChildtwo.audioReplyImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.audioSuffleImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.songDurationTextview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.songPositionTextview.setVisibility(View.VISIBLE);
        }

        if (listOfSongs.size() == 1) {
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward_inactive);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind_inactive);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousGrayoutImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextGrayoutImageview.setVisibility(View.VISIBLE);
        } else if (indexP == 0) {
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind_inactive);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousGrayoutImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextGrayoutImageview.setVisibility(View.GONE);
        } else if (indexP == (listOfSongs.size() - 1)) {
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward_inactive);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextGrayoutImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousGrayoutImageview.setVisibility(View.GONE);
        } else {
            binding.includeSlidingPanelChildtwo.slideBottomView.nextImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.nextGrayoutImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousImageview.setVisibility(View.VISIBLE);
            binding.includeSlidingPanelChildtwo.slideBottomView.previousGrayoutImageview.setVisibility(View.GONE);
            binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward);
            binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind);
        }
    }

    @Override
    public void playNext(int indexP, Song currentAudio) {
        showSongInfo(currentAudio);
        sendAnalytics(Constants.PLAYER_AUDIO_STOP_PLAY);

    }

    @Override
    public void playPrevious(int indexP, Song currentAudio) {
        showSongInfo(currentAudio);
        sendAnalytics(Constants.PLAYER_AUDIO_STOP_PLAY);
    }

    @Override
    public void showErrorDialog(int indexP, Song currentAudio) {
        if (trialStatus) {
            trialSubscriptionDialog();
        } else {
            subscriptionDialog();
        }

        if (streamingManager.isPlaying()) {
            streamingManager.onStop();
        }
    }

    @Override
    public void onValueChanged(int value) {
        streamingManager.onSeekTo(value * 1000);
        streamingManager.scheduleSeekBarUpdate();

    }


    public void dailymixSongs(List<Song> songs) {
        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = 0;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
//        binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(songPosition);
    }

    public void youAlsoLikeFragmentSongClickedEvent(Song song, int position, List<Song> songs) {

        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = position;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);

    }

    public void genreSongFragmentSongClickedEvent(Song song, int position, List<Song> songs) {
        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = position;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
    }

    public void setNotifcationSong(Song song, int position, List<Song> songs) {
        listOfSongs = new ArrayList<Song>();
        songPosition = 0;
        if (listOfSongs.size() <= 0) {
            listOfSongs.add(song);
            songAdapter = new SongAdapter(listOfSongs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        streamingManager.setMediaList(listOfSongs);
        binding.slidingLayout.setEnabled(true);
        binding.slidingLayout.setClickable(true);
        binding.slidingLayout.setPanelState(SlidingUpPanelLayoutOne.PanelState.EXPANDED);
        binding.includeSlidingPanelChildtwo.aviProgress.setVisibility(View.GONE);
        binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_play);
        binding.includeSlidingPanelChildtwo.audioForwardImageview.setBackgroundResource(R.drawable.audio_forward_inactive);
        binding.includeSlidingPanelChildtwo.audioRewindImageview.setBackgroundResource(R.drawable.audio_rewind_inactive);
//        binding.includeSlidingPanelChildtwo.audioRewindImageview.setVisibility(View.VISIBLE);
//        binding.includeSlidingPanelChildtwo.audioForwardImageview.setVisibility(View.VISIBLE);
//        binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setVisibility(View.VISIBLE);


//        shouldBottomPlayerPlay = false;
//        itemEventClicked = true;
//        listOfSongs = new ArrayList<Song>();
//        songPosition = position;
//        if (listOfSongs.size() <= 0) {
//            songAdapter = new SongAdapter(songs, null);
//            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
//        }
//        listOfSongs = songs;
//        streamingManager.setMediaList(listOfSongs);
    }
//    public void browseAllClickedEvent(Genre genre, int position, List<Genre> genres) {
//        shouldBottomPlayerPlay = false;
//        itemEventClicked = true;
//        listOfGenres = new ArrayList<Genre>();
//        songPosition = position;
//        if (listOfGenres.size() <= 0) {
//            songAdapter = new SongAdapter(genres, null);
//            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
//        }
//        listOfGenres = genres;
//        streamingManager.setMediaList(listOfSongs);
//    }

    public void allSongsItemClickEvent(List<Song> songs, String genre) {

        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = 0;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, genre);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
    }

    @Override
    public void onRecentlyPlayedPlayAction(Song song, int position, List<Song> songs) {
        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = position;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
//        binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(songPosition);


    }

    private void onRepeatClicked() {
        if (repeatCurrentSong) {
            repeatCurrentSong = false;
            streamingManager.setRepeatClicked(false);
            binding.includeSlidingPanelChildtwo.audioRepeatImageview.setImageResource(R.drawable.ic_audio_repeat);
        } else {
            repeatCurrentSong = true;
            streamingManager.setRepeatClicked(true);
            binding.includeSlidingPanelChildtwo.audioRepeatImageview.setImageResource(R.drawable.ic_audio_repeat_activated);
        }
    }

    private void onSuffleClicked() {

        if (listOfSongs.size() == 1) {
            Toast.makeText(getApplicationContext(), "Unable to Shuffle, since there is only one song..", Toast.LENGTH_SHORT).show();
        } else {
            if (isSuffleSongs) {
                isSuffleSongs = false;
                binding.includeSlidingPanelChildtwo.audioSuffleImageview.setImageResource(R.drawable.audio_suffle);

            } else {
                isSuffleSongs = true;
                Collections.shuffle(listOfSongs.subList(songPosition + 1, listOfSongs.size() - 1));
                streamingManager.setMediaList(listOfSongs);
                if (songAdapter != null) {
                    songAdapter.setData(listOfSongs);
                }
                binding.includeSlidingPanelChildtwo.audioSuffleImageview.setImageResource(R.drawable.ic_audio_shuffle_on);

            }

        }
    }

    @Override
    public void onYouMightAlsoLikePlayAction(Song song, int position, List<Song> songs) {
        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = position;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
//        binding.includeSlidingPanelChildtwo.songPicker.smoothScrollToPosition(songPosition);

    }

    @Override
    public void onPopularSongsPlayAction(Song song, int position, List<Song> songs) {
        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = position;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
    }


    @Override
    public void onStart() {
        super.onStart();
        notificationManager.getNotificationsCount(new APIListener<NotificationCountResponse>() {
            @Override
            public void onSuccess(NotificationCountResponse result, List<Object> params) {

                if (result.getCounts() <= 0) {
                    binding.noticationCountTextview.setVisibility(View.GONE);
                } else {
                    binding.noticationCountTextview.setVisibility(View.VISIBLE);
                    binding.noticationCountTextview.setText(result.getCounts() + "");
                }


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioDashboardActivity.this, t);
            }

        });
        try {
            if (streamingManager != null) {
                streamingManager.subscribesCallBack(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
//        try {
//            if (streamingManager != null) {
//                streamingManager.unSubscribeCallBack();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            if (streamingManager != null) {
                streamingManager.unSubscribeCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void loadSongDetailsBottomView(Song song) {
        binding.includeSlidingPanelChildtwo.slideBottomView.audioTitleTextview.setText(song.getName());
        binding.includeSlidingPanelChildtwo.slideBottomView.audioDesTextview.setText(song.getDescription());

        if (song.getImage() != null) {
            try {
                Picasso.with(this).load(URLDecoder.decode(song.getImage(), "UTF-8"))
                        .placeholder(R.drawable.bg_default_album_art)
                        .into(binding.includeSlidingPanelChildtwo.slideBottomView.imageArtViewBottom);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    }

    private void showSongInfo(Song song) {
        currentSong = song;
        binding.includeSlidingPanelChildtwo.audioProgressControl.setValue(0);
        binding.includeSlidingPanelChildtwo.audioProgressControl.setMin(0);
        loadSongDetailsBottomView(song);
    }

    private void setMaxTime() {
        try {
            String timeString = DateUtils.formatElapsedTime(Long.parseLong(String.valueOf(songDuration / 1000)));
            binding.includeSlidingPanelChildtwo.songDurationTextview.setText(timeString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        if (itemEventClicked) {
            if (position == 0) {
                if (position == songPosition) {
                    System.out.println("checkinh player onPause 2222 ");
                    onItemChanged(listOfSongs.get(songPosition));
                } else {
                    System.out.println("checkinh player onPause 3333 ");
                    onItemChanged(listOfSongs.get(songPosition));
                }
            } else {
                songPosition = position;
                System.out.println("checkinh player onPause 4444 ");
                onItemChanged(listOfSongs.get(songPosition));

            }
        } else {
            songPosition = position;
            System.out.println("checkinh player onPause 5555 ");
            onItemChanged(listOfSongs.get(songPosition));
        }
        itemEventClicked = false;


    }

    private void onItemChanged(Song item) {
        System.out.println("checkinh player onPause 1111 ");
        changeRateButtonState(item);
        playSong(item);
        if (repeatCurrentSong) {
            //playCurrent(songPosition, currentSong);
            //changeRateButtonState(item);
            //playSong(item);
        } else {

        }

    }

    private void changeRateButtonState(Song item) {
        song = item;
        songId.add(item.getId());
        System.out.println("sjkdbjhsv " + item.getId());
        binding.includeSlidingPanelChildtwo.audioName.setText(item.getName());
        binding.includeSlidingPanelChildtwo.audioDes.setText(item.getDescription());

    }

    private void addToLibraryDialog(Song song) {

        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();


        TextView songTitle = (TextView) alertDialog.findViewById(R.id.add_song_title);
        songTitle.setText(song.getName());
        TextView addToLibrary = (TextView) alertDialog.findViewById(R.id.add_to_library);
        TextView addToPlaylist = (TextView) alertDialog.findViewById(R.id.add_to_playlist);
        ImageView cancelView = (ImageView) alertDialog.findViewById(R.id.cancel_view);


        addToLibrary.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                addDataToLibrary(song);
                alertDialog.dismiss();

            }
        });
        addToPlaylist.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                getUserPlaylists();
//                alertDialog.dismiss();

            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

    }

    private void addDataToLibrary(Song song) {

        tvManager.addDataToLibraryHash("S", songId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(AudioDashboardActivity.this,
                        getResources().getString(R.string.added_to_library), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getBaseContext(), t);
            }
        });

    }

    private void addToLibraryPlaylistDialog(List<PlayList> playLists) {

        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.audio_alertdialog_playlist_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        RecyclerView recyclerView = (RecyclerView) alertDialog.findViewById(R.id.addSongToPlaylistRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AddSongToPlaylistDialogAdapter adapter = new AddSongToPlaylistDialogAdapter(this, playLists, (AddSongToPlaylistDialogAdapter.OnPlaylistDialogItemClickListener) AudioDashboardActivity.this);
        recyclerView.setAdapter(adapter);

        TextView songTitle = (TextView) alertDialog.findViewById(R.id.add_to_playlist_text);
        ImageView cancelView = (ImageView) alertDialog.findViewById(R.id.cancel_view);

        cancelView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void getUserPlaylists() {
        tvManager.getAllPlaylist(new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> playLists, List<Object> params) {
//                binding.aviProgress.setVisibility(View.GONE);
                if (playLists.size() == 0) {
                    Toast.makeText(AudioDashboardActivity.this,
                            getResources().getString(R.string.no_playlist_found), Toast.LENGTH_SHORT).show();
                } else {
                    addToLibraryPlaylistDialog(playLists);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getBaseContext(), t);
            }
        });
    }

    @Override
    public void onPlaylistDialogItemClick(PlayList playList, int position, List<PlayList> songs) {

        playlistId = playList.getId();
        addSongToPlaylist(playlistId, songId);
        alertDialog.dismiss();

    }

    private void addSongToPlaylist(int playlistId, List<Integer> songIdList) {

        tvManager.addSongsToPlaylist(playlistId, songIdList, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                Toast.makeText(AudioDashboardActivity.this,
                        getResources().getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_imageview:
                streamingManager.onSkipToNext(false);
//                sendAnalytics(Constants.PLAYER_AUDIO_STOP_PLAY);
                break;
            case R.id.previous_imageview:
                streamingManager.onSkipToPrevious();
//                sendAnalytics(Constants.PLAYER_AUDIO_STOP_PLAY);
                break;
            case R.id.play_pause_imageview:
                if (currentSong != null) {
                    playPauseEvent();
                }
                break;
            case R.id.audio_play_pause_imageview:
                if (currentSong != null) {
                    playPauseEvent();
                }
        }
    }

    public void playAndPauseSong() {
        if (currentSong != null) {
            playPauseEvent();
        }
    }

    private void playPauseEvent() {
        if (streamingManager.isPlaying()) {
            streamingManager.onPause();
            binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_play);
            binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_play_black);

        } else {
            streamingManager.onPlay(currentSong);
            binding.includeSlidingPanelChildtwo.audioPlayPauseImageview.setBackgroundResource(R.drawable.audio_pause);
            binding.includeSlidingPanelChildtwo.slideBottomView.playPauseImageview.setBackgroundResource(R.drawable.audio_pause_black);

        }
    }

    @Override
    public void onAudioSongsPlayItemClick(Song song, int position) {

        System.out.println("efjbdnfnff 11111");
        List<Song> songs = new ArrayList<Song>();
        songs.add(song);

        shouldBottomPlayerPlay = false;
        itemEventClicked = true;
        listOfSongs = new ArrayList<Song>();
        songPosition = 0;
        if (listOfSongs.size() <= 0) {
            songAdapter = new SongAdapter(songs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);
        }
        listOfSongs = songs;
        streamingManager.setMediaList(listOfSongs);
//        youAlsoLikeFragmentSongClickedEvent(song,position,listOfSongs);
    }

    @Override
    public void onAddSongsItemClick(Song song) {

        songId.add(song.getId());
        addToLibraryDialog(song);
    }

    @Override
    public void onAudioSongsMoreItemClick(View view, Song song, int position) {
        System.out.println("efjbdnfnff 3333");
        if (addPlaylistLayout.getVisibility() == View.VISIBLE) {
            addPlaylistLayout.startAnimation(animHide);
            addPlaylistLayout.setVisibility(View.GONE);

        } else {
            addPlaylistLayout.startAnimation(animShow);
            addPlaylistLayout.setVisibility(View.VISIBLE);
            binding.includeDashboard.playlistSongNameTextview.setText(song.getName());
            binding.includeDashboard.songDesTextview.setText(song.getDescription());
            songIdsToCreatePlaylist.add(song.getId());
            if (song.getImage() != null) {
                try {
                    Picasso.with(AudioDashboardActivity.this).load(URLDecoder.decode(song.getImage(), "UTF-8"))
                            .placeholder(R.drawable.program)
                            .into(binding.includeDashboard.playlistSongImageImageview);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void prepareBottomPlayer() {

        String streamImage = utils.getLastItemThumbnail();
        String streamName = utils.getLastItemTitle();
        String streamDescription = utils.getLastItemDescription();
        String streamUrl = utils.getLastItemStreamUrl();
        int streamId = utils.getLastItemId();
        lastsongDuration = utils.getLastItemDuration();
        Song song = new Song();
        song.setId(streamId);
        song.setName(streamName);
        song.setDescription(streamDescription);
        song.setImage(streamImage);
        song.setUrl(streamUrl);

        listOfSongs = new ArrayList<Song>();
        songPosition = 0;
        if (listOfSongs.size() <= 0) {
            listOfSongs.add(song);
            songAdapter = new SongAdapter(listOfSongs, null);
            binding.includeSlidingPanelChildtwo.songPicker.setAdapter(songAdapter);

        }
        streamingManager.setMediaList(listOfSongs);

    }

    @Override
    public void onAudioSongsPlayListItemClick(PlayList playList, int position) {

        if (selectPlaylistAlertDialog != null && selectPlaylistAlertDialog.isShowing()) {
            selectPlaylistAlertDialog.findViewById(R.id.aviProgress).setVisibility(View.VISIBLE);
        }
//        addSongsToPlaylist(playList.getId());
    }

    private void getNextOrSuggestedSongs(int id, final int offset) {

        tvManager.getAudioSuggestionList(id, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {

                if (songs.size() > 0) {
                    if (songAdapter != null) {
                        listOfSongs.addAll(songs);
                        streamingManager.setMediaList(listOfSongs);
                        songAdapter.notifyItemRangeInserted(offset, songs.size());

                    }

                }


            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void getGenreSongs(final int offset, final String genre) {

        tvManager.getGenreSongs(offset, 8, genre, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                if (songs.size() > 0) {
                    if (songAdapter != null) {
                        listOfSongs.addAll(songs);
                        streamingManager.setMediaList(listOfSongs);
                        songAdapter.notifyItemRangeInserted(offset, songs.size());
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });
    }

    @Override
    public void onArtistsPlayAction(Artist artist, int position, List<Artist> artistList) {


    }

    @Override
    public void onSearchSugTextviewItemClick(String string, int position, List<String> songs) {
        searchKeyWord = string;
        searchSongs(searchKeyWord);
        binding.includeDashboard.viewSearchTint.setVisibility(View.GONE);
        hideKeyboard();
        binding.includeDashboard.searchview.setSearchString(searchKeyWord, true);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

        private List<Song> data;
        private String genre;

        public SongAdapter(List<Song> data, String genre) {
            this.data = data;
            this.genre = genre;
        }

        public void setData(List<Song> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.audio_player_list_item, parent, false);
            return new SongAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SongAdapter.ViewHolder holder, int position) {

            if ((data.size() - position) == 1) {
                int lastSongId = data.get(position).getId();
                getNextOrSuggestedSongs(lastSongId, data.size());

            }


            Song current = data.get(position);
            if (current.getImage() != null) {
                try {
                    Picasso.with(AudioDashboardActivity.this).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                            .placeholder(R.drawable.program)
                            .into(holder.image);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.includeDashboard.searchview.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
