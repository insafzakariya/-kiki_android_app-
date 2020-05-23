package lk.mobilevisions.kiki.audio.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;

public class SharedPrefrenceUtils {

    private static final String PREF_NAME = "anyaudio_tasks";
    private static SharedPrefrenceUtils mInstance;
    private static int MODE = 0;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean firstPageLoadedStatus;


    public SharedPrefrenceUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE);
        editor = preferences.edit();
    }

    public static SharedPrefrenceUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefrenceUtils(context);
        }
        return mInstance;
    }


    public String getCurrentStreamingItem() {
        return preferences.getString("streaming", "");
    }

    public String getLastSearchTerm() {
        return preferences.getString(Constants.KEY_SEARCH_TERM, "");
    }

    public void setLastSearchTerm(String term) {
        editor.putString(Constants.KEY_SEARCH_TERM, term);
        editor.commit();
    }


    // Last Item

    public void setLastItemStreamUrl(String url) {
        editor.putString("lastItemStream", url);
        editor.commit();

        Log.d("PrefCheck", "setting Last Url : " + url);
    }

    public String getLastItemStreamUrl() {
        Log.d("PrefCheck", " returning Last Url : " + preferences.getString("lastItemStream", ""));
        return preferences.getString("lastItemStream", "");
    }

    public void setLastItemThumbnailUrl(String url) {
        editor.putString("lastItemThumbnail", url);
        editor.commit();
    }

    public String getLastItemThumbnail() {
        return preferences.getString("lastItemThumbnail", "");
    }
    public void setLastItemDuration(int duration) {
        editor.putInt("lastItemDuration", duration);
        editor.commit();
    }
    public void setLastItemId(int id) {
        editor.putInt("lastItemId", id);
        editor.commit();
    }
    public void setLastItemTitle(String title) {
        editor.putString("lastItemTitle", title);
        editor.commit();
    }
    public void setLastItemDescription(String description) {
        editor.putString("lastItemDescription", description);
        editor.commit();
    }
    public String getLastItemTitle() {
        return preferences.getString("lastItemTitle", "");
    }
    public String getLastItemDescription() {
        return preferences.getString("lastItemDescription", "");
    }
    public int getLastItemId() {
        return preferences.getInt("lastItemId", 0);
    }
    public int getLastItemDuration() {
        return preferences.getInt("lastItemDuration", 0);
    }
    public void setLastItemArtist(String artist) {
        editor.putString("lastItemArtist", artist);
        editor.commit();
    }

    public String getLastItemArtist() {
        return preferences.getString("lastItemArtist", "");
    }

    // Current Item

    public void setCurrentItemStreamUrl(String url) {
        editor.putString("currentItemStream", url);
        editor.commit();

        if (url!=null && url.length() > 0) {
            setLastItemStreamUrl(url);
        }

    }

    public String getCurrentItemStreamUrl() {
        return preferences.getString("currentItemStream", "");
    }

    public void setCurrentItemThumbnailUrl(String url) {
        editor.putString("currentItemThumbnail", url);
        editor.commit();

            setLastItemThumbnailUrl(url);

    }

    public String getCurrentItemThumbnail() {
        return preferences.getString("currentItemThumbnail", "");
    }
    public void setCurrentItemId(int id) {
        editor.putInt("currentItemId", id);
        editor.commit();

        setLastItemId(id);

    }
    public void setCurrentItemDuration(int duration) {
        editor.putInt("currentItemDuration", duration);
        editor.commit();

        setLastItemDuration(duration);

    }
    public void setCurrentItemTitle(String title) {
        editor.putString("currentItemTitle", title);
        editor.commit();

        if (title.length() > 0)
            setLastItemTitle(title);

    }
    public void setCurrentItemDiscription(String discription) {
        editor.putString("currentItemDiscription", discription);
        editor.commit();
if(discription!=null){
    if (discription.length() > 0)
        setLastItemDescription(discription);
}


    }
    public String getCurrentItemTitle() {
        return preferences.getString("currentItemTitle", "");
    }

    public void setCurrentItemArtist(String artist) {
        editor.putString("currentItemArtist", artist);
        editor.commit();

        if (artist.length() > 0)
            setLastItemArtist(artist);

    }

    public String getCurrentItemArtist() {
        return preferences.getString("currentItemArtist", "");
    }

    // Player State

    public void setPlayerState(int state) {

        editor.putInt("AnyAudioPlayerState", state);
        editor.commit();

        if (state == Constants.PLAYER.PLAYER_STATE_STOPPED) {

            setCurrentItemTitle("");
            setCurrentItemThumbnailUrl("");
            setCurrentItemArtist("");

        }

    }

    public int getPlayerState() {
        return preferences.getInt("AnyAudioPlayerState", Constants.PLAYER.PLAYER_STATE_STOPPED);
    }


    //Notification Tray Player
    public void setStreamContentLength(String trackLen) {
        editor.putString("trackLen", trackLen);
        editor.commit();
    }

    public String getStreamContentLength() {
        return preferences.getString("trackLen", "00:00");
    }

    public void setStreamUrlFetchedStatus(boolean fetchedStatus) {

        editor.putBoolean("fetchedStatus", fetchedStatus);
        editor.commit();

    }

    public boolean getStreamUrlFetchedStatus() {
        return preferences.getBoolean("fetchedStatus", false);
    }

    public void setTaskThumbnail(String taskID, String thumbanil) {
        editor.putString("taskThumbnail" + taskID, thumbanil);
        editor.commit();
    }


    public void setTaskArtist(String taskID, String artist) {
        editor.putString("taskArtist" + taskID, artist);
        editor.commit();
    }

    public String getTaskThumbnail(String taskId) {
        return preferences.getString("taskThumbnail" + taskId, "");
    }

    public String getTaskArtist(String taskId) {
        return preferences.getString("taskArtist" + taskId, "");
    }

    public void setTaskStatus(String taskID, String stateWaiting) {
        editor.putString("taskStatus" + taskID, stateWaiting);
        editor.commit();
    }


    public void setCurrentOngoingTask(String taskID) {
        editor.putString("currentOngoingTask", taskID);
        editor.commit();
    }

    public String getCurrentOngoingTask() {
        return preferences.getString("currentOngoingTask", "");
    }

    public void setMetadataDuration(String fileName, String duration) {
        editor.putString("mtd" + fileName, duration);
        editor.commit();
    }

    public String getMetadataDuration(String fileName) {
        return preferences.getString("mtd" + fileName, "<Unknown>");
    }

    public void setMetadataArtist(String filename, String artist) {
        editor.putString("mtar" + filename, artist);
        editor.commit();
    }

    public String getMetadataArtist(String fileName) {
        return preferences.getString("mtar" + fileName, "<Unknown>");
    }

    public boolean isNotificationPlayerControlReceiverRegistered() {
        return preferences.getBoolean("npcbr", false);
    }

    public void setNotificationPlayerControlReceiverRegistered(boolean registered) {
        editor.putBoolean("npcbr", registered);
        editor.commit();
    }

    public void setNewVersionName(String newVersionName) {
        editor.putString("versionNm", newVersionName);
        editor.commit();
    }

    public String getLatestVersionName(){
        return preferences.getString("versionNm","");
    }

    public void setNewVersionCode(int newVersion) {

        editor.putInt("versionCd", newVersion);
        editor.commit();
    }

    public int getLatestVersionCode(){
        return preferences.getInt("versionCd",0);
    }

    public void newSearch(boolean equals) {
        Log.d("SearchTest"," wrting sp "+equals);
        editor.putBoolean("ns",equals);
        editor.commit();
    }

    public boolean isNewSearch(){
        return preferences.getBoolean("ns",false);
    }

    public int getSelectedNavIndex() {
        return preferences.getInt("selectedNav",0);
    }


    public void setCurrentSongId(int id) {
        editor.putInt("currentSongId", id);
        editor.commit();
    }
    public int getCurrentSongId() {
        return preferences.getInt("currentSongId",0);
    }


    public void setSelectedNavIndex(int index){
        editor.putInt("selectedNav",index);
        editor.commit();
    }
}
