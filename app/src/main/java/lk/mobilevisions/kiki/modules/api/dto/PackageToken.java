/**
 * Created by Chatura Dilan Perera on 19/2/2017.
 */
package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PackageToken {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("tokenHash")
    @Expose
    private String tokenHash;

    @SerializedName("date")
    @Expose
    private Date expireDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
