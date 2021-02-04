package lk.mobilevisions.kiki.chat.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Channels implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("sid")
    @Expose
    private String sid;
    @SerializedName("accountSid")
    @Expose
    private String accountSid;
    @SerializedName("serviceSid")
    @Expose
    private String serviceSid;
    @SerializedName("friendlyName")
    @Expose
    private String friendlyName;
    @SerializedName("uniqueName")
    @Expose
    private String uniqueName;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("block")
    @Expose
    private boolean block;
    @SerializedName("member")
    @Expose
    private boolean member;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getServiceSid() {
        return serviceSid;
    }

    public void setServiceSid(String serviceSid) {
        this.serviceSid = serviceSid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }
}
