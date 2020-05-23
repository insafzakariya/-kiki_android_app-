/**
 * Created by Chatura Dilan Perera on 1/10/2016.
 */
package lk.mobilevisions.kiki.modules.player;

import android.content.Context;
import android.view.KeyEvent;

public class KeyCompatibleMediaController extends MediaControllerView {

    private static CustomPlayerControl playerControl;

    public KeyCompatibleMediaController(Context context, MediaControllerListener listener, MediaControllerListener playerListener) {
        super(context, listener, playerListener);
    }

    @Override
    public void setMediaPlayer(CustomPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        this.playerControl = playerControl;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
                show();
            }
            return true;
        } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}