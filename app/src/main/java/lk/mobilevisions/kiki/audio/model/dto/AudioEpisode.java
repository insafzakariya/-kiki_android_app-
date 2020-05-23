package lk.mobilevisions.kiki.audio.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AudioEpisode implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("video_link")
    @Expose
    private String videoLink;
    @SerializedName("preview_link")
    @Expose
    private String previewLink;
    @SerializedName("is_liked")
    @Expose
    private boolean isLiked;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("startDate")
    @Expose
    private String startDate;

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     *     The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The videoLink
     */
    public String getVideoLink() {
        return videoLink;
    }

    /**
     *
     * @param videoLink
     *     The video_link
     */
    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    /**
     *
     * @return
     *     The previewLink
     */
    public String getPreviewLink() {
        return previewLink;
    }

    /**
     *
     * @param previewLink
     *     The preview_link
     */
    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    /**
     *
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
