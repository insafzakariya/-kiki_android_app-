/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lk.mobilevisions.kiki.audio.model.dto.Song;

public class AudioStreamingManager extends StreamingManager {
    private static final String TAG = Logger.makeLogTag(AudioStreamingManager.class);

    private AudioPlaybackListener audioPlayback;
    private CurrentSessionCallback currentSessionCallback;
    private static volatile AudioStreamingManager instance = null;
    private Context context;
    private int index = 0;
    private boolean playMultiple = false;
    private boolean isReplayClicked = false;
    private boolean showPlayerNotification = false;
    public PendingIntent pendingIntent;
    private Song currentAudio;
    private List<Song> mediaList = new ArrayList<>();
    public static volatile Handler applicationHandler = null;


    public static AudioStreamingManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AudioStreamingManager.class) {

                instance = new AudioStreamingManager();
                instance.context = context;
                instance.audioPlayback = new AudioPlaybackListener(context);
                instance.audioPlayback.setCallback(new MyStatusCallback());
                applicationHandler = new Handler(context.getMainLooper());
            }
        }
        return instance;
    }

    public void subscribesCallBack(CurrentSessionCallback callback) {
        this.currentSessionCallback = callback;
    }

    public int getCurrentIndex() {
        return this.index;
    }

    public void setRepeatClicked(boolean isClicked) {
        this.isReplayClicked = isClicked;
    }

    public void unSubscribeCallBack() {
        this.currentSessionCallback = null;
    }

    public Song getCurrentAudio() {
        return currentAudio;
    }

    public int getListSize() {
        if (this.mediaList != null) {
            return mediaList.size();
        }
        return 0;
    }

    public String getCurrentAudioId() {
        return currentAudio != null ? String.valueOf(currentAudio.getId()) : "";
    }

    public boolean isPlayMultiple() {
        return playMultiple;
    }

    public void setPlayMultiple(boolean playMultiple) {
        this.playMultiple = playMultiple;
    }

    public boolean isPlaying() {
        return instance.audioPlayback.isPlaying();
    }

    public void setPendingIntentAct(PendingIntent mPendingIntent) {
        this.pendingIntent = mPendingIntent;
    }

    public void setShowPlayerNotification(boolean showPlayerNotification) {
        this.showPlayerNotification = showPlayerNotification;
    }

    public void setMediaList(List<Song> currentAudioList) {
        if (this.mediaList != null) {
            this.mediaList.clear();
            this.mediaList.addAll(currentAudioList);
        }
    }

//    public void clearList() {
//        if (this.mediaList != null && mediaList.size() > 0) {
//            this.mediaList.clear();
//            this.index = 0;
//            this.onPause();
//        }
//    }

    @Override
    public void onPlay(Song infoData) {
        System.out.println("checking Notification  9999 ");
        if (infoData == null) {
            return;
        }
        if (playMultiple && !isMediaListEmpty()) {
            index = mediaList.indexOf(infoData);
        }

        if (this.currentAudio != null && String.valueOf(this.currentAudio.getId()).equalsIgnoreCase(String.valueOf(infoData.getId())) && instance.audioPlayback != null && instance.audioPlayback.isPlaying()) {
            System.out.println("checking Notification  1000 ");
            onPause();
//            this.currentAudio = infoData;
//            handlePlayRequest();
        } else {
            System.out.println("checking Notification  1212121 ");
            if (infoData.getUrl() != null && infoData.getUrl().length() > 0) {
                System.out.println("checking Notification  1313131 ");
                this.currentAudio = infoData;
                handlePlayRequest();
                if (currentSessionCallback != null) {
                    System.out.println("checking Notification  1414141 ");
                    currentSessionCallback.playCurrent(index, currentAudio);
                }

            } else {
                if (currentSessionCallback != null) {
                    System.out.println("checking Notification  1515151 ");
                    currentSessionCallback.showErrorDialog(index, currentAudio);
                }

            }


        }
    }


    @Override
    public void onPause() {
        handlePauseRequest();
    }

    @Override
    public void onStop() {
        handleStopRequest(null);
    }

    @Override
    public void onSeekTo(long position) {
        audioPlayback.seekTo((int) position);
    }

    @Override
    public int lastSeekPosition() {
        return (audioPlayback == null) ? 0 : (int) audioPlayback.getCurrentStreamPosition();
    }

    @Override
    public void onSkipToNext() {
        System.out.println("checking Notification  5555 ");
        if (isReplayClicked) {
            onReplayAgain();
        } else {
            System.out.println("checking Notification  6666 ");
            int nextIndex = index + 1;
            if (isValidIndex(true, nextIndex)) {
                Song metaData = mediaList.get(nextIndex);
                System.out.println("checking Notification  7777 ");
                onPlay(metaData);
                if (currentSessionCallback != null) {
                    System.out.println("checking Notification  88888 ");
                    currentSessionCallback.playNext(nextIndex, metaData);
                }
            }
        }

    }


    @Override
    public void onSkipToPrevious() {
        int prvIndex = index - 1;
        if (isValidIndex(false, prvIndex)) {
            Song metaData = mediaList.get(prvIndex);
            onPlay(metaData);
            if (instance.currentSessionCallback != null) {
                currentSessionCallback.playPrevious(prvIndex, metaData);
            }
        }
    }

    @Override
    public void onReplayAgain() {
        audioPlayback.mCurrentPosition = 0;
        int currentIndex = index;
        if (isValidIndex(true, currentIndex)) {
            Song metaData = mediaList.get(currentIndex);
            onPlay(metaData);
            if (instance.currentSessionCallback != null) {
                currentSessionCallback.playCurrent(currentIndex, metaData);
            }

        }
    }

    /**
     * @return
     */
    public boolean isMediaListEmpty() {
        return (mediaList == null || mediaList.size() == 0);
    }

    /**
     * @param isIncremental
     * @return
     */

    private boolean isValidIndex(boolean isIncremental, int index) {
        if (isIncremental) {
            return (playMultiple && !isMediaListEmpty() && mediaList.size() > index);
        } else {
            return (playMultiple && !isMediaListEmpty() && index >= 0);
        }
    }

    public void handlePlayRequest() {
        Logger.d(TAG, "handlePlayRequest: mState=" + audioPlayback.getState());
        System.out.println("checking Notification  1717171 ");
        if (audioPlayback != null && currentAudio != null) {
            System.out.println("checking Notification  1818181 ");
            audioPlayback.play(currentAudio);
            if (showPlayerNotification) {
                System.out.println("checking Notification  1919191 ");
                if (context != null) {
                    System.out.println("checking Notification  2020202 ");
                    Intent intent = new Intent(context, AudioStreamingService.class);
                    context.startService(intent);
                } else {
                    System.out.println("checking Notification  21212 ");
                    Intent intent = new Intent(context, AudioStreamingService.class);
                    context.stopService(intent);
                }

                NotificationManager.getInstance().postNotificationName(NotificationManager.audioDidStarted, currentAudio);
                NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, getCurrentAudio().getId());
                setPendingIntent();
            }
        }
    }

    private void setPendingIntent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pendingIntent != null) {
                    NotificationManager.getInstance().postNotificationName(NotificationManager.setAnyPendingIntent, pendingIntent);
                }
            }
        }, 400);
    }

    public void handlePauseRequest() {
        Logger.d(TAG, "handlePauseRequest: mState=" + audioPlayback.getState());
        if (audioPlayback != null && audioPlayback.isPlaying()) {
            audioPlayback.pause();
            if (showPlayerNotification) {
                NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, getCurrentAudio().getId());
            }
        }
    }

    public void handleStopRequest(String withError) {
        Logger.d(TAG, "handleStopRequest: mState=" + audioPlayback.getState() + " error=", withError);
        audioPlayback.stop(true);
    }

    static class MyStatusCallback implements PlaybackListener.Callback {
        @Override
        public void onCompletion() {
            if (instance.playMultiple && !instance.isMediaListEmpty()) {
                if (instance.currentSessionCallback != null) {
                    instance.currentSessionCallback.playSongComplete();
                }
                instance.onSkipToNext();
            } else {
                instance.handleStopRequest(null);
            }
        }

        @Override
        public void onPlaybackStatusChanged(int state) {

            try {

                if (state == PlaybackStateCompat.STATE_PLAYING) {

                    instance.scheduleSeekBarUpdate();
                } else {

                    instance.stopSeekBarUpdate();
                }
                if (instance.currentSessionCallback != null) {

                    instance.currentSessionCallback.updatePlaybackState(state);
                } else {


                }

                instance.mLastPlaybackState = state;
                if (instance.currentAudio != null) {

//                    instance.currentAudio.setPlayState(state);
                }
                if (instance.showPlayerNotification) {

                    NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, instance.getCurrentAudio().getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getDuration(int duration) {
            if (instance.currentSessionCallback != null) {
                instance.currentSessionCallback.currentSongDuration(duration);
            }
        }

        @Override
        public void onError(String error) {
            //TODO FOR ERROR
        }

        @Override
        public void setCurrentMediaId(String mediaId) {

        }

    }


    public void cleanupPlayer(Context context, boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService);
    }


    public void cleanupPlayer(boolean notify, boolean stopService) {
        handlePauseRequest();
        audioPlayback.stop(true);
        if (stopService) {
            Intent intent = new Intent(context, AudioStreamingService.class);
            context.stopService(intent);
        }
    }

    public void clearService() {
        Intent intent = new Intent(context, AudioStreamingService.class);
        context.stopService(intent);
    }

    private ScheduledFuture<?> mScheduleFuture;
    public int mLastPlaybackState;
    private long currentPosition = 0;
    private final Handler mHandler = new Handler();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    public void scheduleSeekBarUpdate() {
        stopSeekBarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    public void stopSeekBarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (instance.mLastPlaybackState == 0 || instance.mLastPlaybackState < 0) {
            return;
        }
        if (instance.mLastPlaybackState != PlaybackStateCompat.STATE_PAUSED && instance.currentSessionCallback != null) {
            instance.currentSessionCallback.currentSeekBarPosition((int) audioPlayback.getCurrentStreamPosition());
        }
    }

}
