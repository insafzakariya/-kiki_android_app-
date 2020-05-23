package lk.mobilevisions.kiki.audio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistModel {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("ids")
    @Expose
    private List<Integer> idList;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }
}
