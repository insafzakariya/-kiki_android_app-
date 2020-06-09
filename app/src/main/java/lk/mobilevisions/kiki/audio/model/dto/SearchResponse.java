package lk.mobilevisions.kiki.audio.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("songs")
    @Expose
    private List<Song> songList;
    @SerializedName("artistLists")
    @Expose
    private List<Artist> artistList;
    @SerializedName("playlists")
    @Expose
    private List<PlayList> playlistList;

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<Artist> artistList) {
        this.artistList = artistList;
    }

    public List<PlayList> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(List<PlayList> playlistList) {
        this.playlistList = playlistList;
    }
}
