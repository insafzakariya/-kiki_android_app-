/**
 * Created by Chatura Dilan Perera on 1/10/2016.
 */
package lk.mobilevisions.kiki.modules.player;

import android.view.View;

public interface MediaControllerListener {

    public void onMediaControllerFullScreenToggle(boolean isFullScreen);

    public void onMoreOptionsButtonClick(View view);

    public void onLikeButtonClick(boolean hasLiked,boolean islike);


    public void onShareButtonClick();



    public void onPlayButtonClick();

    public void onPauseButtonClick();

    public void onMediaControllerClick();

    public void onSubTitlesButtonClick(View view);
    public void onRotateToggle(boolean isRotationEnabled);
}
