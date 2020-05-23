package lk.mobilevisions.kiki.ui.main.fullscreen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.GlobalPlayer;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.app.Utils;


import lk.mobilevisions.kiki.databinding.ActivityFullScreenBinding;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsManager;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.player.MediaControllerListener;
import lk.mobilevisions.kiki.modules.player.PlayerBackNavigationListner;
import lk.mobilevisions.kiki.modules.tv.PlayerListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.main.fragments.PlayerFragment;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import timber.log.Timber;

import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE_POSITION;

//import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE;
//import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE_POSITION;

public class FullScreenActivity extends AppCompatActivity implements PlayerListener, MediaControllerListener, PlayerBackNavigationListner, View.OnClickListener {

    public static final String FULLSCREEN_PLAYER_EPISODE = "FULLSCREEN_PLAYER_EPISODE";
    public static final String FULLSCREEN_PLAYER_EPISODE_POSITION = "FULLSCREEN_PLAYER_EPISODE_POSITION";
    PlayerFragment playerFragment;


    ActivityFullScreenBinding binding;

    @Inject
    TvManager tvManager;

    @Inject
    AnalyticsManager analyticsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen);

        ((Application) getApplication()).getInjector().inject(this);
        binding.goBackTextview.setOnClickListener(this);
        binding.subscribeNowLayout.setOnClickListener(this);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onVideoEnded() {

        if (Application.PLAYER.getEpisodes().size() > 1) {
            if (Application.PLAYER.checkSubscription()) {
                binding.subscribeLayout.setVisibility(View.GONE);
                binding.frameLayoutFullScreenPlayer.setVisibility(View.VISIBLE);
                Application.PLAYER.prepareForNextVideo();
                startVideo();
            } else {
                binding.textViewTitle.setText(Application.PLAYER.getCurrentEpisodeItem().getName());
                try {
                    Picasso.with(FullScreenActivity.this).load(URLDecoder.decode(Application.PLAYER.getCurrentEpisodeItem().getImage(), "UTF-8"))
                            .fit()
                            .placeholder(R.drawable.program)
                            .into(binding.episodeImageView);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                binding.frameLayoutFullScreenPlayer.setVisibility(View.GONE);
                binding.subscribeLayout.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.subscribe_now_layout:
                Intent intentPackages = new Intent(this, PaymentActivity.class);
                startActivity(intentPackages);
                finish();
                break;
            case R.id.go_back_textview:
                finish();
                break;
            default:
        }
    }

    public void startVideo() {

        Application.PLAYER.setPlayerPaused(false);
        analyticsManager.publishActionAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_START_PLAY,"00:00", new APIListener() {
            @Override
            public void onSuccess(Object result, List params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error while publishing Analytics");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                    if (Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId())
                            == (Utils.SharedPrefUtil.getIntFromSharedPref(FullScreenActivity.this, FULLSCREEN_PLAYER_EPISODE, 0))) {
                        Application.PLAYER.setCurrentVideoPosition(Utils.SharedPrefUtil.getLongFromSharedPref(FullScreenActivity.this, FULLSCREEN_PLAYER_EPISODE_POSITION, 0));
                    } else {
                        Application.PLAYER.setCurrentVideoPosition(0);
                    }
                    Utils.SharedPrefUtil.saveIntToSharedPref(FullScreenActivity.this, FULLSCREEN_PLAYER_EPISODE, 0);
                    Utils.SharedPrefUtil.saveLongToSharedPref(FullScreenActivity.this, FULLSCREEN_PLAYER_EPISODE_POSITION, 0);
                }
                playerFragment.startVideo();

                //Load ad
                tvManager.getAdvertisement(Application.PLAYER.getCurrentEpisode(), new APIListener<List<Content>>() {
                    @Override
                    public void onSuccess(List<Content> result, List<Object> params) {
                        playerFragment.loadAdvertisement(result);
                        Timber.i("Advertisement Loading successful");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Timber.e(t, "Advertisement Loading failed");
                    }
                });

                playerFragment.setVideoLike(Application.PLAYER.getEpisodes()
                        .get(Application.PLAYER.getCurrentVideo()).isLiked());
                playerFragment.setVideoTitle(Application.PLAYER.getEpisodes()
                        .get(Application.PLAYER.getCurrentVideo()).getName());
                getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }, 500);


    }


    @Override
    public void onMediaControllerFullScreenToggle(boolean isFullScreen) {
        onBackPressed();
    }

    @Override
    public void onMoreOptionsButtonClick(View view) {

    }

    @Override
    public void onLikeButtonClick(boolean hasLiked, boolean islike) {
//        tvManager.likeEpisode(Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()),
//                new APIListener<List<Episode>>() {
//                    @Override
//                    public void onSuccess(List<Episode> result, List<Object> params) {
//                        Utils.Dialog.createOKDialog(FullScreenActivity.this, getString(R.string.thanks_adding_like), null);
//                        playerFragment.setVideoLike(true);
//                        Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).setLiked(true);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Utils.Error.modifyErrorMessage(t, Constants.Errors.DUPLICATE_ACTION,
//                                getString(R.string.user_already_liked));
//                        Utils.Error.onServiceCallFail(FullScreenActivity.this, t);
//                    }
//                });
    }


    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tag = "FullScreenPlayerFragment";
        playerFragment = new PlayerFragment();
        playerFragment.setEnableFullScreen();
        playerFragment.setMediaControllerListener(this);
        playerFragment.setNavigationBackListener(this);
        playerFragment.setPlayerListener(this);
        transaction.replace(R.id.frameLayoutFullScreenPlayer, playerFragment, tag);
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();

        playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag(tag);

        if (playerFragment != null) {
            startVideo();
        }
    }


    @Override
    public void onShareButtonClick() {
        if (playerFragment != null) {
            playerFragment.doPause();
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Sharing Episode");
        share.putExtra(Intent.EXTRA_TEXT, Uri.parse(((Application) getApplication()).getConfig().getShareURL() + Settings.SHARE_EPISODES_URI +
                Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()).toString());
        startActivity(Intent.createChooser(share, "Share this"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerFragment != null) {
            playerFragment.doPause();
            if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                Utils.SharedPrefUtil.saveIntToSharedPref(this, FULLSCREEN_PLAYER_EPISODE, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                Utils.SharedPrefUtil.saveLongToSharedPref(this, FULLSCREEN_PLAYER_EPISODE_POSITION, Application.PLAYER.getReleasedVideoPosition());
                if (Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV) {
                    Utils.SharedPrefUtil.saveIntToSharedPref(this, TV_PLAYER_EPISODE, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                    Utils.SharedPrefUtil.saveLongToSharedPref(this, TV_PLAYER_EPISODE_POSITION, Application.PLAYER.getReleasedVideoPosition());
                }
            }
            playerFragment.doPause();
        }
    }

    @Override
    public void onPlayButtonClick() {
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Application.PLAYER.setPlayerPaused(false);
        String currentVideoTime = playerFragment.getCurrentVideoStreamingPosition();
        System.out.println("djndbnsjndnmdnmdnmdnmdnmd 1111 " + currentVideoTime);
        analyticsManager.publishActionAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_START_PLAY,currentVideoTime, new APIListener() {
            @Override
            public void onSuccess(Object result, List params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error while publishing Analytics");
            }
        });
    }

    @Override
    public void onPauseButtonClick() {
        Application.PLAYER.setPlayerPaused(true);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String currentVideoTime = playerFragment.getCurrentVideoStreamingPosition();
        System.out.println("djndbnsjndnmdnmdnmdnmdnmd 2222 " + currentVideoTime);
        analyticsManager.publishActionAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_PAUSE_PLAY,currentVideoTime, new APIListener() {
            @Override
            public void onSuccess(Object result, List params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error while publishing Analytics");
            }
        });
    }

    @Override
    public void onMediaControllerClick() {

    }

    @Override
    public void onSubTitlesButtonClick(View view) {

    }

    @Override
    public void onRotateToggle(boolean rotationEnabled) {
        if (rotationEnabled) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    @Override
    public void onBackPressed() {
        Application.PLAYER.setCurrentVideoPosition(playerFragment.getPlayerPosition());
        String currentVideoTime = playerFragment.getCurrentVideoStreamingPosition();
        analyticsManager.publishActionAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_STOP_PLAY,currentVideoTime, new APIListener() {
            @Override
            public void onSuccess(Object result, List params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error while publishing Analytics");
            }
        });
        try {
            super.onBackPressed();
        } catch (Exception ex) {
            Timber.e("Orientation changed");
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV) {
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackButtonClick() {

        String currentVideoTime = playerFragment.getCurrentVideoStreamingPosition();
        analyticsManager.publishActionAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_STOP_PLAY,currentVideoTime, new APIListener() {
            @Override
            public void onSuccess(Object result, List params) {

            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error while publishing Analytics");
            }
        });
        finish();
    }
}

