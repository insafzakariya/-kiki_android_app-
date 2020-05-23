package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("repeats")
    @Expose
    private int repeats;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @SerializedName("webURL")
    @Expose
    private String webURL;
    @SerializedName("stopMainContent")
    @Expose
    private boolean stopMainContent;
    @SerializedName("clickAction")
    @Expose
    private boolean clickAction;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("startTime")
    @Expose
    private int startTime;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("type")
    @Expose
    private String type;

    private boolean shown;

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public boolean isStopMainContent() {
        return stopMainContent;
    }

    public void setStopMainContent(boolean stopMainContent) {
        this.stopMainContent = stopMainContent;
    }

    public boolean isClickAction() {
        return clickAction;
    }

    public void setClickAction(boolean clickAction) {
        this.clickAction = clickAction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
