/**
 * Created by Chatura Dilan Perera on 1/10/2016.
 */
package lk.mobilevisions.kiki.app;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.player.exo.ExoPlayer;
import lk.mobilevisions.kiki.modules.player.exo.HlsRendererBuilder;
import timber.log.Timber;

public class GlobalPlayer {

    private ExoPlayer exoPlayer;

    private List<Episode> episodes;

    private int currentVideo;

    private long currentVideoPosition;

    private long releasedVideoPosition;

    private static GlobalPlayer globalPlayer;

    private Context context;

    private int subtitles = -1;

    private int videoResolution = 0;

    private boolean playerPaused;

    public enum VideoType{
        TV, TV_EPISODES, HOT_VIDEOS, LIVE_VIDEOS, SUBSCRIBE_VIDEOS
    }
    private VideoType videoType;

    private GlobalPlayer(){

    }

    public static GlobalPlayer getInstance(Context context){
        if(globalPlayer == null){
            globalPlayer = new GlobalPlayer();
            //setting video resolution
            globalPlayer.videoResolution = (int) Utils.App.getConfig((Application)context)
                    .getDefaultVideoResolution();
            globalPlayer.context = context;
        }

        return globalPlayer;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }


    public ExoPlayer getNewExoPlayer() {
        exoPlayer = new ExoPlayer(getRendererBuilder());
        return exoPlayer;
    }

    public void setExoPlayer(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public int getCurrentVideo() {
        return currentVideo;
    }

    public void setCurrentVideo(int currentVideo) {
        this.currentVideo = currentVideo;
    }

    public long getCurrentVideoPosition() {
        return currentVideoPosition;
    }

    public void setCurrentVideoPosition(long currentVideoPosition) {
        this.currentVideoPosition = currentVideoPosition;
    }

    public Uri getCurrentVideoContentURI(){
        if(episodes != null && episodes.size() > 0 && currentVideo < episodes.size()){
            AuthUser authUser = ((Application) context).getAuthUser();
            String language = "eng";
            if(authUser != null){
                if (AuthOptions.Language.SINHALA.toString().equals(authUser.getLanguage())) {
                    language = "sin";
                }else if (AuthOptions.Language.TAMIL.toString().equals(authUser.getLanguage())) {
                    language = "tam";
                }else {
                    language = "eng";
                }
            }


            String securityHash = Utils.Security.generateSha512Hash(Utils.Auth.getKeyHash((Application) context));
            System.out.println("ksfhfhhff  1111  " + securityHash);
            String secureURL = episodes.get(currentVideo).getVideoLink();
            System.out.println("ksfhfhhff  2222  " + secureURL);
            if(secureURL.contains("%3F")){
                secureURL = secureURL + "&" + Constants.WOWZA_SECURITY_TOKEN_NAME + "=" +
                        securityHash.trim().replaceAll("[^A-Za-z0-9]", "1");
            }else{
                secureURL = secureURL + "?" + Constants.WOWZA_SECURITY_TOKEN_NAME + "=" +
                        securityHash.trim().replaceAll("[^A-Za-z0-9]", "1");
            }

            //secureURL += "&wowzacaptionlanguages=" + language; Disabled
            Timber.d("Playing secure Video %s", secureURL);
            try {
                System.out.println("ksfhfhhff  3333  " + URLDecoder.decode(secureURL , "UTF-8"));
                System.out.println("ksfhfhhff  4444  " + Uri.parse(URLDecoder.decode(secureURL , "UTF-8")));

                return Uri.parse(URLDecoder.decode(secureURL , "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Timber.e(e);
            }
        }

        return null;
    }

    private ExoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(context, "ExoPlayer");
        return new HlsRendererBuilder(context, userAgent,
                getCurrentVideoContentURI().toString());
    }
    public Episode getCurrentEpisodeItem(){

        Episode episode = episodes.get(currentVideo);

        return episode;
    }

    public boolean checkSubscription(){

        Episode episode = episodes.get(currentVideo);
        if(!episode.isTrailerOnly()){
            return true;
        }
        return false;
    }
    public void prepareForNextVideo(){
        currentVideoPosition = 0;
        currentVideo++;
        if(currentVideo == episodes.size()){
            currentVideo = 0;
        }
    }

    public int getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(int value) {
        this.subtitles = value;
    }

    public int getCurrentEpisode() {
        if(episodes != null && episodes.size() > 0 && currentVideo < episodes.size()){
            return Integer.parseInt(episodes.get(currentVideo).getId());
        }else{
            return 0;
        }

    }

    public int getCurrentVideoByEpisodeId(int episodeId) {
        if(episodes != null && episodes.size() > 0 && currentVideo < episodes.size()){
            int i = 0;
            for(Episode episode: episodes){
                if(Integer.parseInt(episode.getId()) == episodeId){
                   return i;
                }
                i++;
            }
        }
        return 0;
    }

    public long getReleasedVideoPosition() {
        return releasedVideoPosition;
    }

    public void setReleasedVideoPosition(long releasedVideoPosition) {
        this.releasedVideoPosition = releasedVideoPosition;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public void setVideoResolution(int videoResolution) {
        this.videoResolution = videoResolution;
    }

    public int getVideoResolution() {
        return videoResolution;
    }

    public void setPlayerPaused(boolean playerPaused) {
        this.playerPaused = playerPaused;

    }
    public void play() {
        this.exoPlayer.setPlayWhenReady(true);

    }
    public boolean isPlayerPaused() {
        return playerPaused;
    }
}
