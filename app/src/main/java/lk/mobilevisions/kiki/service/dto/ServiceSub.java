package lk.mobilevisions.kiki.service.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceSub {

    @SerializedName("id")
    @Expose
    private int subId;
    @SerializedName("name")
    @Expose
    private String subName;
    @SerializedName("description")
    @Expose
    private String subDescription;
    @SerializedName("imagePath")
    @Expose
    private String subImage;
    @SerializedName("url")
    @Expose
    private String subUrl;

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public String getSubImage() {
        return subImage;
    }

    public void setSubImage(String subImage) {
        this.subImage = subImage;
    }

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }
}
