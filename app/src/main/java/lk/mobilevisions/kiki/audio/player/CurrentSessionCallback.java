/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package lk.mobilevisions.kiki.audio.player;


import lk.mobilevisions.kiki.audio.model.dto.Song;

public interface CurrentSessionCallback {
    void updatePlaybackState(int state);

    void playSongComplete();

    void currentSeekBarPosition(int progress);

    void currentSongDuration(int duration);

    void playCurrent(int indexP, Song currentAudio);

    void playNext(int indexP, Song currentAudio);

    void playPrevious(int indexP, Song currentAudio);

    void showErrorDialog(int indexP, Song currentAudio);


}
