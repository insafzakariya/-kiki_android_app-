package lk.mobilevisions.kiki.ui.main.videos;

/**
 * Created by Chatura Dilan Perera on 21/12/2016.
 */

public enum VideoTabs {

    HOME_VIDEOS(0), HOT_VIDEOS(1), LIVE_VIDEOS(2), SUBSCRIBED_VIDEOS(3);

    private final int id;

    VideoTabs(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

}
