package lk.mobilevisions.kiki.audio.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayList  implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("number_of_songs")
    @Expose
    private String songCount;
    @SerializedName("image")
    @Expose
    private String image;

    private boolean isDefault;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @SerializedName("artistName")
    @Expose
    private String artistName;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSongCount() {
        return songCount;
    }

    public void setSongCount(String songCount) {
        this.songCount = songCount;
    }

    private List<Song> songsList = new ArrayList<Song>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public List<Song> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<Song> songs) {
        this.songsList = songs;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}