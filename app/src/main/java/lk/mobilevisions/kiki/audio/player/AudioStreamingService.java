/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URLDecoder;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Song;


public class AudioStreamingService extends Service implements NotificationManager.NotificationCenterDelegate {
    private static final String TAG = Logger.makeLogTag(AudioStreamingService.class);
    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    public static final String EXTRA_CONNECTED_CAST = "lk.mobilevisions.kiki.CAST_NAME";
    public static final String ACTION_CMD = "lk.mobilevisions.kiki.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";
    public static final String CMD_STOP_CASTING = "CMD_STOP_CASTING";
    private static final int STOP_DELAY = 30000;

    public static final String NOTIFY_PREVIOUS = "lk.mobilevisions.kiki.previous";
    public static final String NOTIFY_CLOSE = "lk.mobilevisions.kiki.close";
    public static final String NOTIFY_PAUSE = "lk.mobilevisions.kiki.pause";
    public static final String NOTIFY_PLAY = "lk.mobilevisions.kiki.play";
    public static final String NOTIFY_NEXT = "lk.mobilevisions.kiki.next";

    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    private RemoteControlClient remoteControlClient;
    private AudioManager audioManager;
    private AudioStreamingManager audioStreamingManager;
    private PhoneStateListener phoneStateListener;
    public PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private android.app.NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        audioStreamingManager = AudioStreamingManager.getInstance(AudioStreamingService.this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNotificationManager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.setAnyPendingIntent);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        try {
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        if (audioStreamingManager.isPlaying()) {
                            audioStreamingManager.handlePauseRequest();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        try {
            Song messageObject = AudioStreamingManager.getInstance(AudioStreamingService.this).getCurrentAudio();
            if (messageObject == null) {
                Handler handler = new Handler(AudioStreamingService.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
                return START_STICKY;
            }

            if (supportLockScreenControls) {
                ComponentName remoteComponentName = new ComponentName(getApplicationContext(), AudioStreamingReceiver.class.getName());
                try {
                    if (remoteControlClient == null) {
                        audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        mediaButtonIntent.setComponent(remoteComponentName);
                        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                        audioManager.registerRemoteControlClient(remoteControlClient);
                    }
                    remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                            | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP
                            | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                } catch (Exception e) {
                    Log.e("tmessages", e.toString());
                }
            }
            createNotification(messageObject);
        } catch (Exception e) {

        }
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    audioStreamingManager.handlePauseRequest();
                } else if (CMD_STOP_CASTING.equals(command)) {
                    //TODO FOR EXTERNAL DEVICE
                }
            }
        }
        return START_NOT_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NonNull
    private String getNotificationChannelId() {
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(TAG, getString(R.string.playback),
                    android.app.NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        return TAG;
    }

    private void createNotification(Song mSongDetail) {


//        String CHANNEL_ID = "my_channel_01";
        try {

            String channelId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = getNotificationChannelId();
            }


            String songName = mSongDetail.getName();
            String authorName = mSongDetail.getDescription();

            Song audioInfo = AudioStreamingManager.getInstance(AudioStreamingService.this).getCurrentAudio();
            int size = AudioStreamingManager.getInstance(AudioStreamingService.this).getListSize();

            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification1);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification1);
            }


            Notification notification = null;

            if (pendingIntent != null) {
                notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.player)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(songName)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.player)
                        .setContentTitle(songName)
                        .build();
            }

            notification.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification.bigContentView = expandedView;
            }

            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }

            Bitmap albumArt = null;
            try {
                ImageLoader imageLoader = ImageLoader.getInstance();
                albumArt = imageLoader.loadImageSync(URLDecoder.decode(audioInfo.getImage(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (albumArt != null) {
                notification.contentView.setImageViewBitmap(R.id.player_album_art, albumArt);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt);
                }
            } else {
                notification.contentView.setImageViewResource(R.id.player_album_art, R.drawable.bg_default_album_art);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewResource(R.id.player_album_art, R.drawable.bg_default_album_art);
                }
            }

            if (size == 1) {

                notification.contentView.setImageViewResource(R.id.player_next, R.drawable.ic_audio_bottom_view_next_grayout);
                notification.contentView.setImageViewResource(R.id.player_previous, R.drawable.ic_audio_bottom_view_previous_grayout);

            } else {
                notification.contentView.setImageViewResource(R.id.player_next, R.drawable.audio_next);
                notification.contentView.setImageViewResource(R.id.player_previous, R.drawable.audio_previous);
            }

            if (supportBigNotifications) {
                if (size == 1) {
                    notification.bigContentView.setImageViewResource(R.id.player_next, R.drawable.ic_audio_bottom_view_next_grayout);
                    notification.bigContentView.setImageViewResource(R.id.player_previous, R.drawable.ic_audio_bottom_view_previous_grayout);

                } else {
                    notification.bigContentView.setImageViewResource(R.id.player_next, R.drawable.audio_next);
                    notification.bigContentView.setImageViewResource(R.id.player_previous, R.drawable.audio_previous);

                }

                notification.bigContentView.setViewVisibility(R.id.player_progress_bar, View.GONE);
            }

            if (!AudioStreamingManager.getInstance(AudioStreamingService.this).isPlaying()) {
                notification.contentView.setViewVisibility(R.id.player_pause, View.GONE);
                notification.contentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.GONE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                }
            } else {
                notification.contentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.player_play, View.GONE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.GONE);
                }
            }

            notification.contentView.setTextViewText(R.id.player_song_name, songName);
            notification.contentView.setTextViewText(R.id.player_author_name, authorName);
            if (supportBigNotifications) {
                notification.bigContentView.setTextViewText(R.id.player_song_name, songName);
                notification.bigContentView.setTextViewText(R.id.player_author_name, authorName);
            }

            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(5, notification);

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
//                    mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
//                int importance = android.app.NotificationManager.IMPORTANCE_LOW;
//                NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, songName, importance);
//                channel.enableVibration(false);
//                mNotificationManager.createNotificationChannel(channel);
//            }


//            startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, notification);


            if (remoteControlClient != null) {
                RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, authorName);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songName);
                if (albumArt != null) {
                    metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, albumArt);
                }
                metadataEditor.apply();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Intent getIntentForNotification(@NonNull String action) {
        Intent intent = new Intent(action);
        intent.setClass(this, AudioStreamingReceiver.class);
        return intent;
    }

    public void setListeners(RemoteViews view) {

        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(NOTIFY_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(NOTIFY_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(NOTIFY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(NOTIFY_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(NOTIFY_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_play, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (remoteControlClient != null) {
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            audioManager.unregisterRemoteControlClient(remoteControlClient);
        }
        try {
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.setAnyPendingIntent) {
            PendingIntent pendingIntent = (PendingIntent) args[0];
            if (pendingIntent != null) {
                this.pendingIntent = pendingIntent;
            }
        } else if (id == NotificationManager.audioPlayStateChanged) {
            Song mSongDetail = AudioStreamingManager.getInstance(AudioStreamingService.this).getCurrentAudio();
            if (mSongDetail != null) {
                createNotification(mSongDetail);
            } else {
                stopSelf();
            }
        }
    }

    @Override
    public void newSongLoaded(Object... args) {

    }
}
