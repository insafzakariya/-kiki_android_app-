/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class AudioStreamingReceiver extends BroadcastReceiver {

    private AudioStreamingManager audioStreamingManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("checking Notification  000 ");
        this.audioStreamingManager = AudioStreamingManager.getInstance(context);
        if(this.audioStreamingManager ==null){
            System.out.println("checking Notification  1111 ");
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            System.out.println("checking Notification  2222 ");
            System.out.println("NOTIFICATINNNNNN 0000 ");
            if (intent.getExtras() == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                    if (this.audioStreamingManager.isPlaying()) {
                        this.audioStreamingManager.onPause();
                    } else {
                        this.audioStreamingManager.onPlay(this.audioStreamingManager.getCurrentAudio());
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:

                    this.audioStreamingManager.onPlay(this.audioStreamingManager.getCurrentAudio());
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:

                    this.audioStreamingManager.onPause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:

                    this.audioStreamingManager.onSkipToNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

                    this.audioStreamingManager.onSkipToPrevious();
                    break;
            }
        } else {
            System.out.println("checking Notification  33333 ");
            System.out.println("NOTIFICATINNNNNN 1111 ");
            this.audioStreamingManager = AudioStreamingManager.getInstance(context);
            switch (intent.getAction()) {
                case AudioStreamingService.NOTIFY_PLAY:
                    this.audioStreamingManager.onPlay(this.audioStreamingManager.getCurrentAudio());
                    break;
                case AudioStreamingService.NOTIFY_PAUSE:
                case android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    this.audioStreamingManager.onPause();
                    break;
                case AudioStreamingService.NOTIFY_NEXT:
                    System.out.println("checking Notification  4444 ");
                    this.audioStreamingManager.onSkipToNext();
                    break;
                case AudioStreamingService.NOTIFY_CLOSE:
                    this.audioStreamingManager.cleanupPlayer(context, true, true);
                    break;
                case AudioStreamingService.NOTIFY_PREVIOUS:
                    this.audioStreamingManager.onSkipToPrevious();
                    break;
            }
        }
    }
}
