package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chatura Dilan Perera on 12/3/2017.
 */

public class NotificationCountResponse {

    @SerializedName("counts")
    @Expose
    private int counts;


    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }
}
