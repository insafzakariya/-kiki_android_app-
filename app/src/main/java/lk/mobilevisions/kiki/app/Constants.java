/**
 * Created by Chatura Dilan Perera on 6/11/2016.
 */
package lk.mobilevisions.kiki.app;

public class Constants {

    public static final String MAIN_PACKAGE_NAME = "MAIN_PACKAGE_NAME";
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503;
    public static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY";
    public static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET";
    public static final String TWITTER_CALLBACK_URL = "TWITTER_CALLBACK_URL";

    public static final String MENU_FACEBOOK_PAGE_URL = "MENU_FACEBOOK_PAGE_URL";

    public static final String FAQ_URL = "FAQ_URL";
    public static final String CONTACT_TELEPHONE = "CONTACT_TELEPHONE";
    public static final String CONTACT_EMAIL = "CONTACT_EMAIL";

    public static final String DISABLE_SCREEN_CAPTURING = "DISABLE_SCREEN_CAPTURING";

    public static final String DEFAULT_VIDEO_RESOLUTION = "DEFAULT_VIDEO_RESOLUTION";

    public static final String WOWZA_SECURITY_TOKEN_NAME = "tokenHash";

    public static final String MOBILE_PAYMENT_GATEWAY_URL = "MOBILE_PAYMENT_GATEWAY_URL";

    public static final String SHARE_URL = "SHARE_URL";
    public static final  String KEY_SEARCH_TERM = "searchTerm";
    public static final String PLAYER_START_PLAY = "start";
    public static final String PLAYER_STOP_PLAY = "stop";
    public static final String PLAYER_PAUSE_PLAY = "pause";
    public static final String PLAYER_AUDIO_START_PLAY = "song_start";
    public static final String PLAYER_AUDIO_STOP_PLAY = "song_stop";
    public static final String PLAYER_AUDIO_PAUSE_PLAY = "song_pause";

    public static final int PLAYER_AUDIO_PLAY_START = 1;
    public static final int PLAYER_AUDIO_PLAY_STOP = 3;
    public static final int PLAYER_AUDIO_PLAY_PAUSE = 3;

    public static final String AUTH_DIALOG_AUTH = "DIALOG_AUTH";
    public static final String AUTH_DIALOG_PACKAGE_NAME = "com.dialog.dialoggo";

    public static final int PROGRAM_LIST_LIMIT = 30;
    public static final String USER_AGREEMENT_PAGE_URL = "USER_AGREEMENT_PAGE_URL" ;
    public static final String PAID_PACKAGE_ID = "PAID_PACKAGE_ID" ;

    public static final String COMING_FROM_SPLASH_SCREEN = "COMING_FROM_SPLASH_SCREEN";
    public static final String CURRENT_APP = "CURRENT_APP";

    public static final class Errors{
        public static final int DUPLICATE_ACTION = 1014;
    }

    public static final String NAVIGATE_NOWPLAYING = "navigate_nowplaying";

    public static interface DIRECTION {
        int VERTICAL = 1;
        int HORIZONTAL = 2;
    }

    public static interface ACTIONS {

        public static final String SWIPE_TO_CANCEL = "com.anyaudio.in.action.swipe_to_cancel";
        public static final String PLAY_TO_PAUSE = "com.anyaudio.in.action.play_to_pause";
        public static final String PAUSE_TO_PLAY = "com.anyaudio.in.action.pause_to_play";
        public static final String MAIN_ACTION = "com.anyaudio.in.action.main";
        public static final String INIT_ACTION = "com.anyaudio.in.action.init";
        public static final String PLAY_ACTION = "com.anyaudio.in.action.play";
        public static final String START_FOREGROUND_ACTION = "com.anyaudio.in.action.startforeground";
        public static final String STOP_FOREGROUND_ACTION = "com.anyaudio.in.action.stopforeground";

        public static final String PLAYING = "notification_state_player";
        public static final String STOP_PLAYER = "notification_stop_player";
        public static final String STOP_FOREGROUND_ACTION_BY_STREAMSHEET = "com.anyaudio.in.action.stopforeground_from_user";

        public static final String AUDIO_OPTIONS = "com.anyaudio.in.action.songplayoncard";
        public static final String SONG_DOWNLOAD_ON_CARD = "com.anyaudio.in.action.downloadoncard";
        public static final String SONG_SHOWALL_ON_CARD = "com.anyaudio.in.action.showalloncard";

        public static final String NEXT_ACTION = "com.anyaudio.in.action.nextPlay";
        public static final String DN = "donothing";
    }

    public static interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
    // languagephone
    public static final String KEY_LANGUAGE_PHONE = "LangPhone";
    public static final String KEY_LANGUAGE_PHONE_FULLNAME = "LangPhoneFullName";
    public static final String LANG_EN_PHONE = "en";
    public static final String LANG_SI_PHONE = "si";
    public static final String LANG_TA_PHONE = "ta";

    public static final String LANG_EN_PHONE_FULL = "English";
    public static final String LANG_SI_PHONE_FULL = "Sinhala";
    public static final String LANG_TA_PHONE_FULL = "Tamil";

    public static interface PLAYER {

        boolean PLAYING = false;
        public static final String AUDIO_TITLE = "";
        public static final String THUMBNAIL_URL = "";
        public static final String AUDIO_SUBTITLE = "audio_subtitle";
        public static final String EXTRAA_PLAYER_STATE = "player_state_extraa";

        int PLAYER_STATE_PLAYING = 2;
        int PLAYER_STATE_PAUSED = 1;
        int PLAYER_STATE_STOPPED = -1;


    }
    public static final String KEY_STREAM_CONTENT_LEN = "stream_content_len";
}
