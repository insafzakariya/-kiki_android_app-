package lk.mobilevisions.kiki.audio.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {

    @SerializedName("dailymix")
    @Expose
    private List<DailyMix> dailymix = null;
    @SerializedName("recently")
    @Expose
    private RecentlyPlayed recently;
    @SerializedName("youmight")
    @Expose
    private YouAlsoMightLike youmight;

    public List<DailyMix> getDailymix() {
        return dailymix;
    }

    public void setDailymix(List<DailyMix> dailymix) {
        this.dailymix = dailymix;
    }

    public RecentlyPlayed getRecently() {
        return recently;
    }

    public void setRecently(RecentlyPlayed recently) {
        this.recently = recently;
    }

    public YouAlsoMightLike getYoumight() {
        return youmight;
    }

    public void setYoumight(YouAlsoMightLike youmight) {
        this.youmight = youmight;
    }

}