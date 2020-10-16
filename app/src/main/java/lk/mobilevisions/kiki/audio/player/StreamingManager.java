/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;


import lk.mobilevisions.kiki.audio.model.dto.Song;

public abstract class StreamingManager {

    public abstract void onPlay(Song infoData);

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onSeekTo(long position);

    public abstract int lastSeekPosition();

    public abstract void onReplayAgain();

    public abstract void onSkipToNext(boolean isToCheckRepeat);

    public abstract void onSkipToPrevious();

}
