package lk.mobilevisions.kiki.ui.main.home;


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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.GlobalPlayer;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.app.Utils;


import lk.mobilevisions.kiki.databinding.FragmentHomeBinding;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsManager;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.player.MediaControllerListener;
import lk.mobilevisions.kiki.modules.tv.PlayerListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

import lk.mobilevisions.kiki.ui.main.fragments.PlayerFragment;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import timber.log.Timber;

import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE;
import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE_POSITION;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements PlayerListener, MediaControllerListener,
        ProgramsAdapter.OnProgramItemClickListener, EpisodesAdapter.OnEpisodesItemClickListener {

    public static final String HOME = "HOME";
    public static final String TV_PLAYER_EPISODE_POSITION = "TV_PLAYER_EPISODE_POSITION";
    public static final String TV_PLAYER_EPISODE = "TV_PLAYER_EPISODE";
    public static final String TV_PLAYER_PROGRAM = "TV_PLAYER_PROGRAM";
    public static final String TV_PLAYER_PROGRAM_POSITION = "TV_PLAYER_PROGRAM_POSITION";
    public static final int FULL_SCREEN_REQUEST_CODE = 1001;

FragmentHomeBinding binding;
    public HomeFragment() {
        // Required empty public constructor
    }

    PlayerFragment playerFragment;

    @Inject
    TvManager tvManager;

    @Inject
    AnalyticsManager analyticsManager;

    RecyclerView.Adapter programsAdapter;
    LinearLayoutManager programsLayoutManager;

    public boolean isFullScreen;


    Date selectedDate;

    private boolean isTablet;
    private boolean hasVideoLoaded;

    OrientationEventListener orientationEventListener;

    private boolean canSetToFullScreen = true;

    private boolean isTvFlow = false;
    private boolean isVideoStarted = false;

    private boolean isActivityInBackground;
    private List<Episode> episodeList;
    private int channelId;
    private boolean isShareButtonClicked;

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("checking share event onStart 4444");
        isActivityInBackground = true;
        if(isAdded())
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("checking share event onStop 5555");
        isShareButtonClicked = false;
        if(playerFragment != null) {
            playerFragment.doPause();
        }
        isActivityInBackground = false;
    }

    public void loadPrograms() {
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        binding.textViewDate.setVisibility(View.INVISIBLE);
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if(dateToSend == null){
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }

        final FragmentManager fragmentManager = getChildFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = "PlayerFragment";
        playerFragment = new PlayerFragment();
        playerFragment.setMediaControllerListener(this);
        playerFragment.setPlayerListener(this);
        transaction.replace(R.id.frameLayoutPlayer, playerFragment, tag);
        transaction.commitAllowingStateLoss();
        binding.textViewCurrentVideoTitle.setText("");


        tvManager.getChannelTrailers(channelId, dateToSend, dateToSend, new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> programs, List<Object> params) {
                Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV);
                if (programs.size() <= 0) {
                    if(isAdded())
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if(playerFragment != null){
                        playerFragment.setNoProgramsAvailable(true);
                    }
                } else {
                    if(playerFragment != null){
                        playerFragment.setNoProgramsAvailable(false);
                    }
                    List<Episode> episodes = new ArrayList<Episode>();
                    for (Program program : programs) {
                        program.getEpisode().setName(program.getName());
                        episodes.add(program.getEpisode());
                    }
                    Application.PLAYER.setEpisodes(episodes);
                    Application.PLAYER.setCurrentVideo(0);
                    episodeList = episodes;
                    startVideo();

                }

            }

            @Override
            public void onFailure(Throwable t) {
                if(isAdded())
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

        tvManager.getPrograms(channelId, dateToSend, dateToSend, new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> programs, List<Object> params) {
                if(isAdded()){
                    programsAdapter = new ProgramsAdapter(getActivity(), programs, false, HomeFragment.this);
                    binding.recycleViewPrograms.setAdapter(programsAdapter);
                    binding.aviProgress.setVisibility(View.GONE);
                    if (programs.size() <= 0) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        binding.recycleViewPrograms.setVisibility(View.GONE);
                        binding.TextViewEmptyVideos.setVisibility(View.VISIBLE);
                    } else {
                        binding.recycleViewPrograms.setVisibility(View.VISIBLE);
                        binding.TextViewEmptyVideos.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                if(isAdded())
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("checking share event onResume 3333");
        if(Application.PLAYER.getVideoType() == null
                || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV
                || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV_EPISODES){
            Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV);
            isTvFlow = false;
            if (!hasVideoLoaded) {
                if(!isShareButtonClicked){
                    loadPrograms();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        if(isAdded())
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        Application.BUS.register(this);

        selectedDate = Utils.DateUtil.getDateOnly(new Date());

        if(isAdded())
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(isAdded()){
            if(getActivity().getIntent().getExtras().getInt("channelId") > 0) {
                channelId = getActivity().getIntent().getExtras().getInt("channelId");
                Utils.SharedPrefUtil.saveLongToSharedPref(getActivity().getApplicationContext(), TV_PLAYER_PROGRAM_POSITION, 0);
            }
        }

        //Setting the Player height
        if(isAdded()){
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            binding.frameLayoutPlayer.getLayoutParams().height = size.x / 16 * 9;
        }


        binding.buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();

            }

        });

        programsLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewPrograms.setLayoutManager(programsLayoutManager);


        isTablet = isTablet(getActivity().getApplicationContext());
        Timber.i("This device is a tablet: " + isTablet);

        orientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
            public void onOrientationChanged(int orientation) {

                if (canSetToFullScreen == false) {
                    if (isTablet) {
                        if ((orientation <= 100 && orientation >= 80)) {
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            canSetToFullScreen = true;
                        }
                    } else {
                        if ((orientation <= 10 && orientation >= 0)) {
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            canSetToFullScreen = true;
                        }
                    }
                }
            }
        };

        return binding.getRoot();

    }



    @Override
    public void onVideoEnded() {
        Application.PLAYER.prepareForNextVideo();
        isTvFlow = true;
        startVideo();
    }


    public void startVideo() {
        System.out.println("checking share event startVideo 1111");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {

                    if (isTvFlow == false) {
                        if(isAdded()){
                            Application.PLAYER.setCurrentVideo(Application.PLAYER.getCurrentVideoByEpisodeId(Utils.SharedPrefUtil.getIntFromSharedPref(getActivity().getApplicationContext(), TV_PLAYER_PROGRAM, 0)));
                            Application.PLAYER.setCurrentVideoPosition(Utils.SharedPrefUtil.getLongFromSharedPref(getActivity().getApplicationContext(), TV_PLAYER_PROGRAM_POSITION, 0));
                            isTvFlow = true;
                        }

                    }
                    if(getActivity() != null){
                        getActivity().getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    if(!isActivityInBackground){
                        return;
                    }
                    playerFragment.startVideo();
                    binding.textViewCurrentVideoTitle.setText(Application.PLAYER.getEpisodes()
                            .get(Application.PLAYER.getCurrentVideo()).getName().toString());
                    playerFragment.setVideoLike(Application.PLAYER.getEpisodes()
                            .get(Application.PLAYER.getCurrentVideo()).isLiked());

                    canSetToFullScreen = true;
                    isVideoStarted = true;
                    if (getActivity() != null) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                   if(Application.PLAYER.isPlayerPaused()){
                       Application.PLAYER.setPlayerPaused(false);
                       Application.PLAYER.play();
                   }
                    if(isFullScreen){
                        playerFragment.doPause();
                    }

                    tvManager.getAdvertisement(Application.PLAYER.getCurrentEpisode(), new APIListener<List<Content>>() {
                        @Override
                        public void onSuccess(List<Content> result, List<Object> params) {
                            System.out.println("dfbhkdfhfdfhf 0000   " + result.size());
                            playerFragment.loadAdvertisement(result);
                            Timber.i("Advertisement Loading successful");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            System.out.println("dfbhkdfhfdfhf 1111   " + t.toString());
                            Timber.e(t, "Advertisement Loading failed");
                        }
                    });

                }

                SimpleDateFormat sdfDate = new SimpleDateFormat("dd, MMM yyyy");
                String todayDate = sdfDate.format(selectedDate);
                binding.textViewDate.setVisibility(View.VISIBLE);
                binding.textViewDate.setText(todayDate);
                if (orientationEventListener != null) {
                    orientationEventListener.enable();
                }
            }
        }, 500);

    }



    @Override
    public void onMediaControllerFullScreenToggle(boolean isFullScreen) {
        if(isVideoStarted){
            this.isFullScreen = isFullScreen;
            if (isFullScreen) {
                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                    Utils.SharedPrefUtil.saveIntToSharedPref(getActivity().getApplicationContext(), FULLSCREEN_PLAYER_EPISODE, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                    Utils.SharedPrefUtil.saveLongToSharedPref(getActivity().getApplicationContext(), FULLSCREEN_PLAYER_EPISODE_POSITION, playerFragment.getPlayerPosition());
                }
                if (playerFragment != null) {
                    playerFragment.doPause();
                }
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), binding.frameLayoutPlayer, "playerTransition");
                this.startActivityForResult(intent, FULL_SCREEN_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMoreOptionsButtonClick(View view) {

    }



    @Override
    public void onLikeButtonClick(boolean hasLiked,boolean isLiked) {
//        if(isLiked)
//            tvManager.likeEpisode(Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()),
//                    new APIListener<List<Episode>>() {
//                        @Override
//                        public void onSuccess(List<Episode> result, List<Object> params) {
//                            Utils.Dialog.createOKDialog(getActivity(), getString(R.string.thanks_adding_like), null);
//                            playerFragment.setVideoLike(true);
//                            Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).setLiked(true);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            Utils.Error.modifyErrorMessage(t, Constants.Errors.DUPLICATE_ACTION,
//                                    getString(R.string.user_already_liked));
//                            Utils.Error.onServiceCallFail(getActivity(), t);
//                        }
//                    });

//        {
//            tvManager.unlikeEpisode(Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()),
//                    new APIListener<List<Episode>>() {
//                        @Override
//                        public void onSuccess(List<Episode> result, List<Object> params) {
//                            Utils.Dialog.createOKDialog(getActivity(), getString(R.string.thanks_adding_unlike), null);
//                            playerFragment.setVideoLike(false);
//                            Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).setLiked(false);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            Utils.Error.modifyErrorMessage(t, Constants.Errors.DUPLICATE_ACTION,
//                                    getString(R.string.user_already_unliked));
//                            Utils.Error.onServiceCallFail(getActivity(), t);
//                        }
//                    });
//        }

    }


    @Override
    public void onShareButtonClick() {
        isShareButtonClicked = true;
        Application.PLAYER.setPlayerPaused(true);
        System.out.println("checking share event onShareButtonClick 0000");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Sharing Episode");
        share.putExtra(Intent.EXTRA_TEXT, Uri.parse(((Application) getActivity().getApplication()).getConfig().getShareURL() + Settings.SHARE_EPISODES_URI +
                Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()).toString());
        startActivity(Intent.createChooser(share, "Share this"));
    }



    @Override
    public void onPlayButtonClick() {
        getActivity().getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Application.PLAYER.setPlayerPaused(false);
        analyticsManager.publishAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_START_PLAY, new APIListener() {
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
        getActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Application.PLAYER.setPlayerPaused(true);
        analyticsManager.publishAnalytics(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId(), Constants.PLAYER_STOP_PLAY, new APIListener() {
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
    public void onPause() {
        super.onPause();
        System.out.println("checking share event onPause 6666");
        hasVideoLoaded = false;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        if (playerFragment != null) {
            playerFragment.doPause();
            isVideoStarted = false;
            Application.PLAYER.setEpisodes(episodeList);
            if (Application.PLAYER.getVideoType() == null || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV || Application.PLAYER.getVideoType() == GlobalPlayer.VideoType.TV_EPISODES) {
                if (Application.PLAYER.getEpisodes() != null && Application.PLAYER.getEpisodes().size() > 0) {
                    Utils.SharedPrefUtil.saveIntToSharedPref(getActivity().getApplicationContext(), TV_PLAYER_PROGRAM, Integer.parseInt(Application.PLAYER.getEpisodes().get(Application.PLAYER.getCurrentVideo()).getId()));
                    Utils.SharedPrefUtil.saveLongToSharedPref(getActivity().getApplicationContext(), TV_PLAYER_PROGRAM_POSITION, Application.PLAYER.getReleasedVideoPosition());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("checking share event onActivityResult 7777");
        Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FULL_SCREEN_REQUEST_CODE) {
            canSetToFullScreen = false;
            isFullScreen = false;
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    public void onProgramItemClick(final View view, final Program program, int position, final ProgramsAdapter.ProgramItemClickCallback callback) {
//        switch (view.getId()) {
//            case R.id.imageButtonEpisodesList:
//                Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
//                if(dateToSend == null){
//                    dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
//                }
//                Intent intentEpisodes = new Intent(getActivity(), EpisodesActivity.class);
//                intentEpisodes.putExtra("program", program);
//                intentEpisodes.putExtra("selectedDate", dateToSend);
//                Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV_EPISODES);
//                getActivity().startActivityForResult(intentEpisodes, 1005);
//
//                break;
//            case R.id.imageButtonSubscribe:
//                if(program.isSubscribed()){
//                    new MaterialDialog.Builder(getActivity())
//                            .title(getString(R.string.app_name))
//                            .content("Are you sure you want to unsubscribe from this program?")
//                            .positiveText("Yes")
//                            .negativeText("No")
//                            .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                @Override
//                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    tvManager.unSubscribeProgram(Integer.valueOf(program.getId()), new APIListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void result, List<Object> params) {
//                                            Utils.Dialog.createOKDialog(getActivity(), "You have unsubscribed from the " + program.getName() + " program successfully!", null);
//                                            callback.onUnSubscribe(true);
//                                        }
//
//                                        @Override
//                                        public void onFailure(Throwable t) {
//
//                                            Utils.Error.onServiceCallFail(getActivity(), t);
//                                            callback.onUnSubscribe(false);
//                                        }
//                                    });
//                                }
//                            })
//                            .show();
//                }else{
//                    tvManager.subscribeProgram(Integer.valueOf(program.getId()), new APIListener<Void>() {
//                        @Override
//                        public void onSuccess(Void result, List<Object> params) {
//                            Utils.Dialog.createOKDialog(getActivity(), "You have subscribed to the " + program.getName() + " program successfully!", null);
//                            callback.onSubscribe(true);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//
//                            Utils.Error.onServiceCallFail(getActivity(), t);
//                            callback.onSubscribe(false);
//                        }
//                    });
//                }
//
//                break;
//
//            default:
//
////                Timber.d("Program item clicked on position %s", position);
////                Application.PLAYER.setPlayerPaused(false);
////                Application.PLAYER.setCurrentVideo(position);
////                Application.PLAYER.setCurrentVideoPosition(0);
////                startVideo();
////                isTvFlow = true;
//
//                Date dateToSend1 = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
//                if(dateToSend1 == null){
//                    dateToSend1 = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
//                }
//                Intent intentEpisodes1 = new Intent(getActivity(), EpisodesActivity.class);
//                intentEpisodes1.putExtra("program", program);
//                intentEpisodes1.putExtra("selectedDate", dateToSend1);
//                intentEpisodes1.putExtra("channelId", channelId);
//                Application.PLAYER.setVideoType(GlobalPlayer.VideoType.TV_EPISODES);
//                getActivity().startActivityForResult(intentEpisodes1, 1005);
//        }
    }


    @Override
    public void onEpisodeItemClick(View view, Episode episode, int position) {
//        Intent intent = new Intent(getActivity(), FullScreenActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Application.PLAYER.setCurrentVideo(position);
//        this.startActivityForResult(intent, 1002);
    }





    private boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (canSetToFullScreen && isActivityInBackground) {
                    onMediaControllerFullScreenToggle(true);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }


    }

}
