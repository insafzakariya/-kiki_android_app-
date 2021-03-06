package lk.mobilevisions.kiki.audio.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;

//import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;

public class NavigationUtils {

//    @TargetApi(21)
//    public static void navigateToAlbum(Activity context, long albumID, Pair<View, String> transitionViews) {
//
//        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//        Fragment fragment;
//
//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
//        fragment = AlbumDetailFragment.newInstance(albumID, false, null);
//
//        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
//        transaction.add(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null).commit();
//
//    }
//
//    @TargetApi(21)
//    publi static void navigateToArtist(Activity context, long artistID, Pair<View, String> transitionViews) {
//
//        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//        Fragment fragment;
//
//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
//        fragment = ArtistDetailFragment.newInstance(artistID, false, null);
//
//        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
//        transaction.add(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null).commit();
//
//    }

//    public static void goToArtist(Context context, long artistId) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(Constants.NAVIGATE_ARTIST);
//        intent.putExtra(Constants.ARTIST_ID, artistId);
//        context.startActivity(intent);
//    }
//
//    public static void goToAlbum(Context context, long albumId) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(Constants.NAVIGATE_ALBUM);
//        intent.putExtra(Constants.ALBUM_ID, albumId);
//        context.startActivity(intent);
//    }
//
//    public static void goToLyrics(Context context) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(Constants.NAVIGATE_LYRICS);
//        context.startActivity(intent);
//    }

    public static void navigateToNowplaying(Activity context, boolean withAnimations) {

        final Intent intent = new Intent(context, AudioPlayerActivity.class);
        context.startActivity(intent);
    }

    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        return intent;
    }

//    public static void navigateToSettings(Activity context) {
//        final Intent intent = new Intent(context, SettingsActivity.class);
//        intent.setAction(Constants.NAVIGATE_SETTINGS);
//        context.startActivity(intent);
//    }

//    public static void navigateToSearch(Activity context) {
//        final Intent intent = new Intent(context, SearchActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        intent.setAction(Constants.NAVIGATE_SEARCH);
//        context.startActivity(intent);
//    }


//    @TargetApi(21)
//    public static void navigateToPlaylistDetail(Activity context, String action, long firstAlbumID, String playlistName, int foregroundcolor, long playlistID, ArrayList<Pair> transitionViews) {
//        final Intent intent = new Intent(context, PlaylistDetailActivity.class);
//        intent.setAction(action);
//        intent.putExtra(Constants.PLAYLIST_ID, playlistID);
//        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR, foregroundcolor);
//        intent.putExtra(Constants.ALBUM_ID, firstAlbumID);
//        intent.putExtra(Constants.PLAYLIST_NAME, playlistName);
//        intent.putExtra(Constants.ACTIVITY_TRANSITION, transitionViews != null);
//
//        if (transitionViews != null && TimberUtils.isLollipop()) {
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0), transitionViews.get(1), transitionViews.get(2));
//            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST, options.toBundle());
//        } else {
//            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST);
//        }
//    }

//    public static void navigateToEqualizer(Activity context) {
//        try {
//            // The google MusicFX apps need to be started using startActivityForResult
//            context.startActivityForResult(TimberUtils.createEffectsIntent(), 666);
//        } catch (final ActivityNotFoundException notFound) {
//            Toast.makeText(context, "Equalizer not found", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public static Intent getNavigateToStyleSelectorIntent(Activity context, String what) {
//        final Intent intent = new Intent(context, SettingsActivity.class);
//        intent.setAction(Constants.SETTINGS_STYLE_SELECTOR);
//        intent.putExtra(Constants.SETTINGS_STYLE_SELECTOR_WHAT, what);
//        return intent;
//    }


public static String convertMinutesToFormat(String minute){
    SimpleDateFormat sdf = new SimpleDateFormat("mm");
String value = null;
    try {
        Date dt = sdf.parse(minute);
        sdf = new SimpleDateFormat("mm:ss");
        value = sdf.format(dt);
        System.out.println(sdf.format(dt));
    } catch (ParseException e) {
        e.printStackTrace();
    }
    return value;
}


}
