package lk.mobilevisions.kiki.chat.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMember implements Serializable {

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
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("viewerId")
    @Expose
    private int viewerId;
    @SerializedName("roleId")
    @Expose
    private int roleId;
    @SerializedName("colour")
    @Expose
    private String colour;
    @SerializedName("artistId")
    @Expose
    private int artistId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getViewerId() {
        return viewerId;
    }

    public void setViewerId(int viewerId) {
        this.viewerId = viewerId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
