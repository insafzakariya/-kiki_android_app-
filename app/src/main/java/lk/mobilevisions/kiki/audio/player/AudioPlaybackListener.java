/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.io.IOException;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SharedPrefrenceUtils;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;

import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;
import static android.media.MediaPlayer.OnSeekCompleteListener;

 public class AudioPlaybackListener implements PlaybackListener, AudioManager.OnAudioFocusChangeListener,
        OnCompletionListener, OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    private static final String TAG = Logger.makeLogTag(AudioPlaybackListener.class);

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;

    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;

    private final Context mContext;
    private final WifiManager.WifiLock mWifiLock;

    private int mState;
    private Callback mCallback;
    private boolean mPlayOnFocusGain;
    public volatile int mCurrentPosition;
    private volatile int duration;
    private volatile String mCurrentMediaId;
    private SharedPrefrenceUtils utils;
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private final AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    public AudioPlaybackListener(Context context) {
        this.mContext = context;
        utils = SharedPrefrenceUtils.getInstance(context);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "dmAudioStreaming_Lock");
        this.mState = PlaybackStateCompat.STATE_NONE;

    }

    private final IntentFilter mAudioNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {

                if (isPlaying()) {
                    //TODO PAUSE THE PLAYING SONG
                    System.out.println("CheckaudioQ 2 ");
                }
                System.out.println("CheckaudioQ 3 ");
            }
        }
    };

    @Override
    public void start() {
        System.out.println("CheckaudioQ 1 ");
    }

    @Override
    public void stop(boolean notifyListeners) {
        System.out.println("CheckaudioQ 4 ");
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            System.out.println("CheckaudioQ 5 ");
            mCallback.onPlaybackStatusChanged(mState);
        }
        System.out.println("CheckaudioQ 6 ");
        mCurrentPosition = getCurrentStreamPosition();
        // Give up Audio focus
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        // Relax all resources
        relaxResources(true);
    }

    @Override
    public void setState(int state) {
        System.out.println("CheckaudioQ 7 ");
        this.mState = state;
    }

    @Override
    public int getState() {
        System.out.println("CheckaudioQ 8 ");
        return mState;
    }

    @Override
    public boolean isConnected() {
        System.out.println("CheckaudioQ 9 ");
        return true;
    }

    @Override
    public boolean isPlaying() {
        System.out.println("CheckaudioQ 10 ");
        return mPlayOnFocusGain || (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    @Override
    public int getCurrentStreamPosition() {
        System.out.println("CheckaudioQ 11 ");
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : mCurrentPosition;
    }




    @Override
    public void updateLastKnownStreamPosition() {
        if (mMediaPlayer != null) {
            System.out.println("CheckaudioQ 12 ");
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
        System.out.println("CheckaudioQ 13 ");
    }


    @Override
    public void play(Song item) {
        System.out.println("checking Notification  232323 ");
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();
        String mediaId = String.valueOf(item.getId());
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        if (mediaHasChanged) {
            System.out.println("CheckaudioQ 15 ");
            mCurrentPosition = 0;
            mCurrentMediaId = mediaId;
        }
        System.out.println("CheckaudioQ 16 ");
        utils.setCurrentSongId(item.getId());
        if (mState == PlaybackStateCompat.STATE_PAUSED && !mediaHasChanged && mMediaPlayer != null) {
            System.out.println("CheckaudioQ 17 ");
            configMediaPlayerState();
        } else {
            System.out.println("CheckaudioQ 18 ");
            mState = PlaybackStateCompat.STATE_STOPPED;
            relaxResources(false); // release everything except MediaPlayer
            String token = Utils.Auth.getBearerToken(Application.getInstance());


            System.out.println("sfksfbvjksdfb 111 " + token);
            System.out.println("sfksfbvjksdfb 222 " + item.getUrl());

            String source = item.getUrl()+"?token="+token;
//            String source = item.getUrl();
            if (source != null) {
                System.out.println("CheckaudioQ 19 ");
                source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
            }
            try {
                System.out.println("CheckaudioQ 20 ");
                System.out.println("checking audio song url 111111 " + source);
                createMediaPlayerIfNeeded();
                mState = PlaybackStateCompat.STATE_BUFFERING;
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(source);

                mMediaPlayer.prepareAsync();

                mWifiLock.acquire();



                if (mCallback != null) {
                    System.out.println("CheckaudioQ 21 ");
                    mCallback.onPlaybackStatusChanged(mState);
                }

            } catch (IOException ex) {
                System.out.println("CheckaudioQ 22 ");
                System.out.println("checking audio song url 22222 " + ex.toString());
                if (mCallback != null) {
                    System.out.println("CheckaudioQ 23 ");
                    mCallback.onError(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void pause() {
        System.out.println("CheckaudioQ 24 ");
        try {
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                System.out.println("CheckaudioQ 25 ");
                // Pause media player and cancel the 'foreground service' state.
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    System.out.println("CheckaudioQ 26 ");
                    System.out.println("checking pause 1111");
                    mMediaPlayer.pause();
                    mCurrentPosition = mMediaPlayer.getCurrentPosition();
                }
                System.out.println("CheckaudioQ 27 ");
                // while paused, retain the MediaPlayer but give up audio focus
                relaxResources(false);
            }
            System.out.println("CheckaudioQ 28 ");
            mState = PlaybackStateCompat.STATE_PAUSED;
            if (mCallback != null) {
                System.out.println("CheckaudioQ 29 ");
                mCallback.onPlaybackStatusChanged(mState);
            }
            System.out.println("CheckaudioQ 30 ");
            unregisterAudioNoisyReceiver();
        } catch (IllegalStateException ex) {
            Logger.e(TAG, ex, "Exception pause IllegalStateException");
            ex.printStackTrace();
        }
    }


    @Override
    public void seekTo(int position) {
        Logger.d(TAG, "seekTo called with ", position);
        System.out.println("CheckaudioQ 31 ");
        if (mMediaPlayer == null) {
            System.out.println("CheckaudioQ 32 ");
            // If we do not have a current media player, simply update the current position
            mCurrentPosition = position;
        } else {
            System.out.println("CheckaudioQ 33 ");
            if (mMediaPlayer.isPlaying()) {
                System.out.println("CheckaudioQ 34 ");
                mState = PlaybackStateCompat.STATE_BUFFERING;
            }
            registerAudioNoisyReceiver();
            mMediaPlayer.seekTo(position);
            if (mCallback != null) {
                System.out.println("CheckaudioQ 35 ");
                mCallback.onPlaybackStatusChanged(mState);
            }
        }
    }

    @Override
    public void setCallback(Callback callback) {
        System.out.println("CheckaudioQ 36 ");
        this.mCallback = callback;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {
        System.out.println("CheckaudioQ 37 ");
        this.mCurrentPosition = pos;
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        System.out.println("CheckaudioQ 38 ");
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        System.out.println("CheckaudioQ 39 ");
        return mCurrentMediaId;
    }

    private void tryToGetAudioFocus() {
        System.out.println("CheckaudioQ 40 ");
        Logger.d(TAG, "tryToGetAudioFocus");
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            System.out.println("CheckaudioQ 41 ");
            mAudioFocus = AUDIO_FOCUSED;
        } else {
            System.out.println("CheckaudioQ 42 ");
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        System.out.println("CheckaudioQ 43 ");
        Logger.d(TAG, "giveUpAudioFocus");
        if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            System.out.println("CheckaudioQ 44 ");
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void configMediaPlayerState() {
        System.out.println("CheckaudioQ 45 ");
        Logger.d(TAG, "configMediaPlayerState. mAudioFocus=", mAudioFocus);
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            System.out.println("CheckaudioQ 46 ");
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                System.out.println("CheckaudioQ 47 ");
                mPlayOnFocusGain = false;
                System.out.println("checking pause 222");
                pause();
            }
        } else {  // we have audio focus:
            System.out.println("CheckaudioQ 48 ");
            registerAudioNoisyReceiver();
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                System.out.println("CheckaudioQ 49 ");
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                System.out.println("CheckaudioQ 50 ");
                if (mMediaPlayer != null) {
                    mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                } // else do something for remote client.
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                System.out.println("CheckaudioQ 51 ");
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    System.out.println("CheckaudioQ 52 ");
                    Logger.d(TAG, "configMediaPlayerState startMediaPlayer. seeking to ",
                            mCurrentPosition);
                    if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                        System.out.println("CheckaudioQ 53 ");
                        System.out.println("checking media player config 444 ");
//
                        mMediaPlayer.start();
                        mState = PlaybackStateCompat.STATE_PLAYING;
                    } else {
                        System.out.println("CheckaudioQ 54 ");
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mState = PlaybackStateCompat.STATE_BUFFERING;
                    }
                }
                System.out.println("CheckaudioQ 55 ");
                mPlayOnFocusGain = false;
            }
        }
        if (mCallback != null) {
            System.out.println("CheckaudioQ 56 ");
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        System.out.println("CheckaudioQ 57 ");
        Logger.d(TAG, "onAudioFocusChange. focusChange=", focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            System.out.println("CheckaudioQ 58 ");
            // We have gained focus:
            mAudioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            System.out.println("CheckaudioQ 59 ");
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (mState == PlaybackStateCompat.STATE_PLAYING && !canDuck) {
                System.out.println("CheckaudioQ 60 ");
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                mPlayOnFocusGain = true;
            }
        } else {
            System.out.println("CheckaudioQ 61 ");
            Logger.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: ", focusChange);
        }
        System.out.println("CheckaudioQ 62 ");
        System.out.println("checking media player config 2222 ");
        configMediaPlayerState();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        System.out.println("CheckaudioQ 63 ");
        Logger.d(TAG, "onSeekComplete from MediaPlayer:", mp.getCurrentPosition());
        mCurrentPosition = mp.getCurrentPosition();
        if (mState == PlaybackStateCompat.STATE_BUFFERING) {
            System.out.println("CheckaudioQ 64 ");
            registerAudioNoisyReceiver();
            mMediaPlayer.start();
            mState = PlaybackStateCompat.STATE_PLAYING;
        }
        if (mCallback != null) {
            System.out.println("CheckaudioQ 65 ");
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        System.out.println("CheckaudioQ 66 ");
        Logger.d(TAG, "onCompletion from MediaPlayer");
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        if (mCallback != null) {
            System.out.println("CheckaudioQ 67 ");
            mCallback.onCompletion();
        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        System.out.println("CheckaudioQ 68 ");
        Logger.d(TAG, "onPrepared from MediaPlayer");
        duration = player.getDuration();
        if (mCallback != null) {
            System.out.println("CheckaudioQ 69 ");
            mCallback.getDuration(duration);
        }
        System.out.println("CheckaudioQ 70 ");
        System.out.println("checking media player config 3333 ");
        configMediaPlayerState();
    }

    /**
     * Called when there's an error playing media. When this happens, the media
     * player goes to the Error state. We warn the user about the error and
     * reset the media player.
     *
     * @see OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("CheckaudioQ 71 ");
        Logger.e(TAG, "Media player error: what=" + what + ", extra=" + extra);
        if (mCallback != null) {
            System.out.println("CheckaudioQ 72 ");
            mCallback.onError("MediaPlayer error " + what + " (" + extra + ")");
        }
        System.out.println("CheckaudioQ 73 ");
        return true; // true indicates we handled the error
    }

    /**
     * Makes sure the media player exists and has been reset. This will create
     * the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded() {
        System.out.println("CheckaudioQ 74 ");
        Logger.d(TAG, "createMediaPlayerIfNeeded. needed? ", (mMediaPlayer == null));
        if (mMediaPlayer == null) {
            System.out.println("CheckaudioQ 75 ");
            mMediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        } else {
            System.out.println("CheckaudioQ 76 ");
            mMediaPlayer.reset();
        }
    }

    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also
     *                           be released or not
     */
    private void relaxResources(boolean releaseMediaPlayer) {
        System.out.println("CheckaudioQ 77 ");
        Logger.d(TAG, "relaxResources. releaseMediaPlayer=", releaseMediaPlayer);

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            System.out.println("CheckaudioQ 78 ");
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentMediaId = null;
            mCurrentPosition = 0;
        }

        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) {
            System.out.println("CheckaudioQ 79 ");
            mWifiLock.release();
        }
    }

    private void registerAudioNoisyReceiver() {
        System.out.println("CheckaudioQ 80 ");
        try {
            if (mAudioNoisyReceiver != null) {
                System.out.println("CheckaudioQ 81 ");
                mContext.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            }
        } catch (Exception e) {
            System.out.println("CheckaudioQ 84 ");
            e.printStackTrace();
        }
    }

    private void unregisterAudioNoisyReceiver() {
        System.out.println("CheckaudioQ 82 ");
        try {
            if (mAudioNoisyReceiver != null) {
                System.out.println("CheckaudioQ 83 ");
                mContext.unregisterReceiver(mAudioNoisyReceiver);
            }
        } catch (Exception e) {
            System.out.println("CheckaudioQ 85 ");
            e.printStackTrace();
        }
    }
}
