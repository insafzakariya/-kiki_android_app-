package lk.mobilevisions.kiki.service.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lk.mobilevisions.kiki.audio.model.dto.PlayList;

public class ServiceModel {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("imagePath")
    @Expose
    private String image;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("landingUrl")
    @Expose
    private String landingUrl;
    @SerializedName("hasChild")
    @Expose
    private boolean hasChild;
    @SerializedName("childs")
    @Expose
    private List<ServiceModel> childList;
    @SerializedName("referanceCode")
    @Expose
    private String referanceCode;

    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public String getReferanceCode() {
        return referanceCode;
    }

    public void setReferanceCode(String referanceCode) {
        this.referanceCode = referanceCode;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public List<ServiceModel> getChildList() {
        return childList;
    }

    public void setChildList(List<ServiceModel> childList) {
        this.childList = childList;
    }
}
