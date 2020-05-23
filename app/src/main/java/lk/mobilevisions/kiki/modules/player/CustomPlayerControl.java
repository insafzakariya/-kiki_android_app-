/**
 * Created by Chatura Dilan Perera on 4/9/2016.
 */
package lk.mobilevisions.kiki.modules.player;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;

public class CustomPlayerControl extends PlayerControl implements MediaControllerView.MediaPlayerControl {

    public CustomPlayerControl(ExoPlayer exoPlayer) {
        super(exoPlayer);
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {


    }

}
