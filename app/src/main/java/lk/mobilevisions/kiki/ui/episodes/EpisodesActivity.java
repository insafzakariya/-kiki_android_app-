package lk.mobilevisions.kiki.ui.episodes;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;


import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.databinding.ActivityEpisodesBinding;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsManager;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.player.MediaControllerListener;
import lk.mobilevisions.kiki.modules.tv.PlayerListener;
import lk.mobilevisions.kiki.ui.main.fragments.PlayerFragment;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenEpisodesActivity;
import lk.mobilevisions.kiki.ui.widgets.DividerItemDecoration;
import timber.log.Timber;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.GlobalPlayer;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import lk.mobilevisions.kiki.ui.main.home.EpisodesAdapter;
import lk.mobilevisions.kiki.ui.widgets.EndlessRecyclerViewScrollListener;

import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenEpisodesActivity.FULLSCREEN_PLAYER_EPISODE;
import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenEpisodesActivity.FULLSCREEN_PLAYER_EPISODE_POSITION;

public class EpisodesActivity extends AppCompatActivity implements EpisodesAdapter.OnEpisodesItemClickListener, PlayerListener, MediaControllerListener {

    public static final String TV_PLAYER_EPISODE_POSITION = "TV_PLAYER_EPISODE_POSITION";
    public static final String TV_PLAYER_EPISODE_TRAILER_POSITION = "TV_PLAYER_EPISODE_TRAILER_POSITION";
    public static final String TV_PLAYER_EPISODE = "TV_PLAYER_EPISODE";
    public static final int FULL_SCREEN_REQUEST_CODE = 1001;

    @Inject
    TvManager tvManager;

    RecyclerView.Adapter episodesAdapter;
    LinearLayoutManager episodesLayoutManager;

    Program program;

    private List<Episode> currentEpisodes;
    private int episodesLimit = 200;

    Date selectedDate;

    boolean sortAscending;
    String sortOrder;

    EndlessRecyclerViewScrollListener recyclerViewScrollListener;

    ActivityEpisodesBinding binding;

    PlayerFragment playerFragment;
    @Inject
    AnalyticsManager analyticsManager;
    private boolean isTvFlow = false;
    private boolean isVideoStarted = false;

    OrientationEventListener orientationEventListener;
    private boolean canSetToFullScreen = true;
    private List<Episode> episodeList;
    private boolean isTablet;
    private boolean hasVideoLoaded;
    private boolean isActivityInBackground;
    public boolean isFullScreen;
    public boolean isOncreatCalleed = false;
    public boolean isTrailerPlaying = true;
    public boolean isOrientationChange;
    List<Episode> programTrailerList = new ArrayList<Episode>();
    private boolean isShareButtonClicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_episodes);
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Application) getApplication()).getInjector().inject(this);

        sortAscending = false;

        episodesLayoutManager = new LinearLayoutManager(this);
        binding.recycleViewEpisodes.setLayoutManager(episodesLayoutManager);

        isTablet = isTablet(EpisodesActivity.this.getApplicationContext());
        program = (Program) getIntent().getSerializableExtra("program");
        selectedDate = (Date) getIntent().getSerializableExtra("selectedDate");

        setTitle("Episodes - " + program.getName());
        binding.programTitleTextview.setText(program.getName());
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Utils.SharedPrefUtil.saveLongToSharedPref(this, TV_PLAYER_EPISODE_POSITION, 0);
        Utils.SharedPrefUtil.saveLongToSharedPref(this, TV_PLAYER_EPISODE_TRAILER_POSITION, 0);

        binding.frameLayoutPlayer.getLayoutParams().height = size.x / 16 * 9;
        orientationEventListener = new OrientationEventListener(EpisodesActivity.this, SensorManager.SENSOR_DELAY_NORMAL) {
            public void onOrientationChanged(int orientation) {

                if (canSetToFullScreen == false) {
                    if (isTablet) {
                        if ((orientation <= 100 && orientation >= 80)) {
                            EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            canSetToFullScreen = true;
                        }
                    } else {
                        if ((orientation <= 10 && orientation >= 0)) {
                            EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            canSetToFullScreen = true;
                        }
                    }
                }
            }
        };
        if (program != null) {

            binding.recycleViewEpisodes.setLayoutManager(new LinearLayoutManager(this));
            binding.recycleViewEpisodes.setHasFixedSize(false);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            final String tag = "PlayerFragment";
            playerFragment = new PlayerFragment();
            playerFragment.setMediaControllerListener(this);
            playerFragment.setPlayerListener(this);
            transaction.replace(R.id.frameLayoutPlayer, playerFragment, tag);
            transaction.commitAllowingStateLoss();




//            tvManager.getProgramTrailers(Integer.parseInt(program.getId()), dateToSend, dateToSend, new APIListener<List<Episode>>() {
//                @Override
//                public void onSuccess(List<Episode> episodes, List<Object> params) {
//                    System.out.println("fgiasfgigfigifg 1111 " +episodes.size());
//                    Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV);
//                    if (episodes.size() <= 0) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        if(playerFragment != null){
//                            playerFragment.setNoProgramsAvailable(true);
//                        }
//                    } else {
//                        if(playerFragment != null){
//                            playerFragment.setNoProgramsAvailable(false);
//                        }
//
//                        Application.PLAYER.setEpisodes(episodes);
//                        Application.PLAYER.setCurrentVideo(0);
//                        programTrailerList = episodes;
//                        startVideo();
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    System.out.println("fgiasfgigfigifg 2222 " + t.toString());
//                        Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
//                }
//            });
//            programTrailerList.add(program.getEpisode());


//            binding.textViewProgramDescription.setText(Utils.Text.stripHtml(program.getDescription()));
            if (program.isSubscribed()) {
                binding.imageButtonSubscribe.setImageResource(R.drawable.ic_yes_subscribe);
                binding.textViewSubscribe.setText("Subscribed");
            } else {
                binding.imageButtonSubscribe.setImageResource(R.drawable.ic_no_subscribe);
                binding.textViewSubscribe.setText("Subscribe");

            }

            binding.imageSortView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTrailerPlaying = false;
                    if (sortAscending) {
                        binding.imageSortView.setImageResource(R.drawable.ic_sort_up);
                        sortAscending = false;
                        sortOrder = "asc";
                    } else {
                        binding.imageSortView.setImageResource(R.drawable.ic_sort_down);
                        sortAscending = true;
                        sortOrder = "desc";
                    }

                    loadEpisodes();
                }
            });

//            binding.imageButtonSubscribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (program.isSubscribed()) {
//                        new MaterialDialog.Builder(EpisodesActivity.this)
//                                .title(getString(R.string.app_name))
//                                .content("Are you sure you want to unsubscribe from this program?")
//                                .positiveText("Yes")
//                                .negativeText("No")
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        tvManager.unSubscribeProgram(Integer.valueOf(program.getId()), new APIListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void result, List<Object> params) {
//                                                Utils.Dialog.createOKDialog(EpisodesActivity.this, "You have unsubscribed from the " + program.getName() + " program successfully!", null);
//                                                binding.imageButtonSubscribe.setImageResource(R.drawable.ic_no_subscribe);
//                                                binding.textViewSubscribe.setText("Subscribe");
//                                                program.setSubscribed(false);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Throwable t) {
//                                                Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
//                                            }
//                                        });
//                                    }
//                                }).show();
//                    } else {
//                        tvManager.subscribeProgram(Integer.valueOf(program.getId()), new APIListener<Void>() {
//                            @Override
//                            public void onSuccess(Void result, List<Object> params) {
//                                Utils.Dialog.createOKDialog(EpisodesActivity.this, "You have subscribed to the " + program.getName() + " program successfully!", null);
//                                binding.imageButtonSubscribe.setImageResource(R.drawable.ic_yes_subscribe);
//                                binding.textViewSubscribe.setText("Subscribed");
//                                program.setSubscribed(true);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {
//                                Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
//
//                            }
//                        });
//                    }
//                }
//            });


            if (episodesAdapter != null) {
                episodesAdapter.notifyDataSetChanged();
            }
            if (recyclerViewScrollListener != null) {
                recyclerViewScrollListener.resetState();
            }

            sortOrder = "asc";
            System.out.println("START VIDEO 6666 ");
            loadEpisodes();

            recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(episodesLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    tvManager.getEpisodes(Integer.valueOf(program.getId()), 0, episodesLimit, selectedDate, selectedDate, sortOrder, new APIListener<List<Episode>>() {
                        @Override
                        public void onSuccess(List<Episode> episodes, List<Object> params) {
                            if (episodes != null && episodes.size() > 0) {
                                for (Episode episode : episodes) {
                                    episode.setName(program.getName() + " - " + episode.getName());
                                }


                                currentEpisodes.addAll(episodes);

                                Application.PLAYER.setEpisodes(currentEpisodes);
                                Application.PLAYER.setCurrentVideoPosition(0);
                                Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV_EPISODES);
                                binding.recycleViewEpisodes.setAdapter(new EpisodesAdapter(EpisodesActivity.this, episodes, EpisodesActivity.this));
                                episodesAdapter.notifyDataSetChanged();
                                if (episodes.size() <= 0) {
                                    binding.recycleViewEpisodes.setVisibility(View.GONE);
                                    binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                                    binding.aviProgress.setVisibility(View.GONE);
                                } else {
                                    binding.recycleViewEpisodes.setVisibility(View.VISIBLE);
                                    binding.TextViewEmptyVideos.setVisibility(View.GONE);
                                    binding.aviProgress.setVisibility(View.GONE);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
                        }
                    });
                }

            };
            binding.recycleViewEpisodes.addOnScrollListener(recyclerViewScrollListener);
        }

        binding.playAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTrailerPlaying = false;
                Application.PLAYER.setEpisodes(episodeList);
                Application.PLAYER.setCurrentVideo(0);
                System.out.println("START VIDEO 1111 ");
                startVideo();
            }
        });
    }

    private boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private void loadEpisodes() {
        binding.aviProgress.setVisibility(View.VISIBLE);
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }

        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if(dateToSend == null){
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getEpisodes(Integer.valueOf(program.getId()), 0, episodesLimit, dateToSend, dateToSend, sortOrder, new APIListener<List<Episode>>() {
            @Override
            public void onSuccess(List<Episode> episodes, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                if (episodes != null && episodes.size() > 0) {


                    for (Episode episode : episodes) {

                        episode.setName(program.getName() + " - " + episode.getName());
                    }
                    programTrailerList.clear();
                    episodeList = episodes;
                    if (isTrailerPlaying) {
                        programTrailerList.add(episodes.get(episodes.size() - 1));
                        Application.PLAYER.setEpisodes(programTrailerList);
                        Application.PLAYER.setCurrentVideo(0);
                    } else {
                        Application.PLAYER.setEpisodes(episodeList);
                        Application.PLAYER.setCurrentVideo(0);
                    }

                    System.out.println("START VIDEO 2222 ");
                    startVideo();
                    episodesAdapter = new EpisodesAdapter(EpisodesActivity.this, episodes, EpisodesActivity.this);
                    binding.recycleViewEpisodes.setAdapter(episodesAdapter);
                    binding.recycleViewEpisodes.addItemDecoration(
                            new DividerItemDecoration(EpisodesActivity.this, R.drawable.line_divider));
                    if (episodes.size() <= 0) {
                        binding.recycleViewEpisodes.setVisibility(View.GONE);
                        binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                        binding.aviProgress.setVisibility(View.GONE);
                    } else {
                        binding.recycleViewEpisodes.setVisibility(View.VISIBLE);
                        binding.TextViewEmptyVideos.setVisibility(View.GONE);
                        binding.aviProgress.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
            }
        });
    }

    private void loadEpisodesOnResume() {
        binding.aviProgress.setVisibility(View.VISIBLE);
        tvManager.getEpisodes(Integer.valueOf(program.getId()), 0, episodesLimit, selectedDate, selectedDate, sortOrder, new APIListener<List<Episode>>() {
            @Override
            public void onSuccess(List<Episode> episodes, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                if (episodes != null && episodes.size() > 0) {


                    for (Episode episode : episodes) {

                        episode.setName(program.getName() + " - " + episode.getName());
                    }
                    programTrailerList.clear();
                    episodeList = episodes;
                    if (isTrailerPlaying) {
                        programTrailerList.add(episodes.get(episodes.size() - 1));
                        Application.PLAYER.setEpisodes(programTrailerList);
                        Application.PLAYER.setCurrentVideo(0);
                    } else {
                        Application.PLAYER.setEpisodes(episodeList);
                        Application.PLAYER.setCurrentVideo(0);
                    }

                    System.out.println("START VIDEO RESUME 5555 ");
                    startVideoOnResume();
                    episodesAdapter = new EpisodesAdapter(EpisodesActivity.this, episodes, EpisodesActivity.this);
                    binding.recycleViewEpisodes.setAdapter(episodesAdapter);
                    binding.recycleViewEpisodes.addItemDecoration(
                            new DividerItemDecoration(EpisodesActivity.this, R.drawable.line_divider));
                    if (episodes.size() <= 0) {
                        binding.recycleViewEpisodes.setVisibility(View.GONE);
                        binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                        binding.aviProgress.setVisibility(View.GONE);
                    } else {
                        binding.recycleViewEpisodes.setVisibility(View.VISIBLE);
                        binding.TextViewEmptyVideos.setVisibility(View.GONE);
                        binding.aviProgress.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
            }
        });
    }

    @Override
    public void onEpisodeItemClick(View view, Episode episode, int position) {
        isTrailerPlaying = false;
//        Application.PLAYER.setEpisodes(episodeList);
//        Application.PLAYER.setCurrentVideo(position);
//        System.out.println("START VIDEO 3333 ");
//        startVideo();
        Intent intent = new Intent(this, FullScreenActivity.class);
        Application.PLAYER.setCurrentVideo(position);
        this.startActivityForResult(intent, 1004);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivityInBackground = true;
        EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStop() {
        super.onStop();
        isShareButtonClicked = false;
        if (playerFragment != null) {
            playerFragment.doPause();
        }
        isActivityInBackground = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Application.PLAYER.getVideoType() == null
                || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV
                || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV_EPISODES) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV_EPISODES);
            isTvFlow = false;
            if (!hasVideoLoaded) {
                if (isOncreatCalleed)
                    if(!isShareButtonClicked){
                        loadEpisodesOnResume();

                    }else{
                        Application.PLAYER.setPlayerPaused(false);
                        Application.PLAYER.play();
                        if(playerFragment!=null)
                            playerFragment.showControls();

                    }
                if(playerFragment!=null){
                    playerFragment.updatePlayPause();
                }

                isShareButtonClicked = false;
                hasVideoLoaded = true;


            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        hasVideoLoaded = false;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        if (playerFragment != null) {
            playerFragment.doPause();
            isVideoStarted = false;
            if (isTrailerPlaying) {
                Application.PLAYER.setEpisodes(programTrailerList);
            } else {
                Application.PLAYER.setEpisodes(episodeList);
            }

            if (Application.PLAYER.getVideoType() == null || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV_EPISODES) {
                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                    Utils.SharedPrefUtil.saveIntToSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                    if (isTrailerPlaying) {
                        Utils.SharedPrefUtil.saveLongToSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE_TRAILER_POSITION, Application.PLAYER.getReleasedVideoPosition());

                    } else {
                        Utils.SharedPrefUtil.saveLongToSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE_POSITION, Application.PLAYER.getReleasedVideoPosition());

                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    public void startVideo() {
        isOncreatCalleed = true;
        if (!isOrientationChange) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {

                        if (isTvFlow == false) {

                            Application.PLAYER.setCurrentVideo(Application.PLAYER.getCurrentVideoByEpisodeId(Utils.SharedPrefUtil.getIntFromSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE, 0)));
                            if (isTrailerPlaying) {
                                Application.PLAYER.setCurrentVideoPosition(0);
                            } else {
                                Application.PLAYER.setCurrentVideoPosition(Utils.SharedPrefUtil.getLongFromSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE_POSITION, 0));

                            }
                            isTvFlow = true;
                        } else {

                        }
                        if (EpisodesActivity.this != null) {

                            EpisodesActivity.this.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                        if (!isActivityInBackground) {

                            return;
                        }

                        playerFragment.startVideo();
                        playerFragment.setVideoLike(Application.PLAYER.getEpisodes()
                                .get(Application.PLAYER.getCurrentVideo()).isLiked());
                        episodesAdapter.notifyDataSetChanged();
                        if(Application.PLAYER.isPlayerPaused()){
                            Application.PLAYER.setPlayerPaused(false);
                            Application.PLAYER.play();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.recycleViewEpisodes.scrollToPosition(Application.PLAYER.getCurrentVideo());
                            }
                        }, 200);
//                    episodesLayoutManager.scrollToPosition(10);
                        canSetToFullScreen = true;
                        isVideoStarted = true;
                        if (EpisodesActivity.this != null) {
                            EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }

                        if (isFullScreen) {
                            playerFragment.doPause();
                        }

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

                    }


                    if (orientationEventListener != null) {
                        orientationEventListener.enable();
                    }
                }
            }, 500);
        }


    }

    public void startVideoOnResume() {
        if (!isOrientationChange) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {

                        if (isTvFlow == false) {

                            Application.PLAYER.setCurrentVideo(Application.PLAYER.getCurrentVideoByEpisodeId(Utils.SharedPrefUtil.getIntFromSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE, 0)));
                            if (isTrailerPlaying) {

                                Application.PLAYER.setCurrentVideoPosition(Utils.SharedPrefUtil.getLongFromSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE_TRAILER_POSITION, 0));
                            } else {

                                Application.PLAYER.setCurrentVideoPosition(Utils.SharedPrefUtil.getLongFromSharedPref(EpisodesActivity.this.getApplicationContext(), TV_PLAYER_EPISODE_POSITION, 0));

                            }
                            isTvFlow = true;
                        }
                        if (EpisodesActivity.this != null) {

                            EpisodesActivity.this.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                        if (!isActivityInBackground) {

                            return;
                        }

                        playerFragment.startVideo();
                        playerFragment.setVideoLike(Application.PLAYER.getEpisodes()
                                .get(Application.PLAYER.getCurrentVideo()).isLiked());
                        episodesAdapter.notifyDataSetChanged();
                        if(Application.PLAYER.isPlayerPaused()){
                            Application.PLAYER.setPlayerPaused(false);
                            Application.PLAYER.play();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.recycleViewEpisodes.scrollToPosition(Application.PLAYER.getCurrentVideo());
                            }
                        }, 200);
//                    episodesLayoutManager.scrollToPosition(10);
                        canSetToFullScreen = true;
                        isVideoStarted = true;
                        if (EpisodesActivity.this != null) {
                            EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }

                        if (isFullScreen) {
                            playerFragment.doPause();
                        }

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

                    }


                    if (orientationEventListener != null) {
                        orientationEventListener.enable();
                    }
                }
            }, 500);
        }


    }



    @Override
    public void onMediaControllerFullScreenToggle(boolean isFullScreen) {
        if (isVideoStarted) {
            this.isFullScreen = isFullScreen;
            if (isFullScreen) {
                Intent intent = new Intent(EpisodesActivity.this, FullScreenEpisodesActivity.class);
                intent.putExtra("isTrailerPlaying", isTrailerPlaying);


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                    Utils.SharedPrefUtil.saveIntToSharedPref(EpisodesActivity.this.getApplicationContext(), FULLSCREEN_PLAYER_EPISODE, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                    Utils.SharedPrefUtil.saveLongToSharedPref(EpisodesActivity.this.getApplicationContext(), FULLSCREEN_PLAYER_EPISODE_POSITION, playerFragment.getPlayerPosition());
                }
                if (playerFragment != null) {
                    playerFragment.doPause();
                }
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(EpisodesActivity.this, binding.frameLayoutPlayer, "playerTransition");
                this.startActivityForResult(intent, FULL_SCREEN_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMoreOptionsButtonClick(View view) {

    }

    @Override
    public void onLikeButtonClick(boolean hasLiked, boolean isLiked) {
//        if (isLiked) {
//            tvManager.likeEpisode(Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()),
//                    new APIListener<List<Episode>>() {
//                        @Override
//                        public void onSuccess(List<Episode> result, List<Object> params) {
//                            Utils.Dialog.createOKDialog(EpisodesActivity.this, getString(R.string.thanks_adding_like), null);
//                            playerFragment.setVideoLike(true);
//                            Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).setLiked(true);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            Utils.Error.modifyErrorMessage(t, Constants.Errors.DUPLICATE_ACTION,
//                                    getString(R.string.user_already_liked));
//                            Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
//                        }
//                    });
//
//        } else {
//            tvManager.unlikeEpisode(Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()),
//                    new APIListener<List<Episode>>() {
//                        @Override
//                        public void onSuccess(List<Episode> result, List<Object> params) {
//                            Utils.Dialog.createOKDialog(EpisodesActivity.this, getString(R.string.thanks_adding_unlike), null);
//                            playerFragment.setVideoLike(false);
//                            Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).setLiked(false);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            Utils.Error.modifyErrorMessage(t, Constants.Errors.DUPLICATE_ACTION,
//                                    getString(R.string.user_already_unliked));
//                            Utils.Error.onServiceCallFail(EpisodesActivity.this, t);
//                        }
//                    });
        }




    @Override
    public void onShareButtonClick() {
        isShareButtonClicked = true;
        Application.PLAYER.setPlayerPaused(true);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Sharing Episode");
        share.putExtra(Intent.EXTRA_TEXT, Uri.parse(((Application) EpisodesActivity.this.getApplication()).getConfig().getShareURL() + Settings.SHARE_EPISODES_URI +
                Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()).toString());
        startActivity(Intent.createChooser(share, "Share this"));
    }


    @Override
    public void onPlayButtonClick() {
        EpisodesActivity.this.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Application.PLAYER.setPlayerPaused(false);
        String currentVideoTime = playerFragment.getCurrentVideoStreamingPosition();
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
        EpisodesActivity.this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Application.PLAYER.setPlayerPaused(true);
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
    }

    @Override
    public void onMediaControllerClick() {

    }

    @Override
    public void onSubTitlesButtonClick(View view) {

    }

    @Override
    public void onRotateToggle(boolean isRotationEnabled) {

    }

    @Override
    public void onVideoEnded() {
        if (!isTrailerPlaying) {
            Application.PLAYER.prepareForNextVideo();
            isTvFlow = true;
            System.out.println("START VIDEO 4444 ");
            startVideo();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (canSetToFullScreen && isActivityInBackground) {
                isOrientationChange = true;
                onMediaControllerFullScreenToggle(true);
                EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV_EPISODES);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FULL_SCREEN_REQUEST_CODE) {
            isOrientationChange = false;
            canSetToFullScreen = false;
            isFullScreen = false;
            EpisodesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
