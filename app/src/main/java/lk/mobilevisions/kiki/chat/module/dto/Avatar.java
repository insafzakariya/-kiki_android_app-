package lk.mobilevisions.kiki.chat.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Avatar implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("url")
    @Expose
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
