package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordUser {

    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("viwerId")
    @Expose
    private int viwerId;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getViwerId() {
        return viwerId;
    }

    public void setViwerId(int viwerId) {
        this.viwerId = viwerId;
    }

    public String getMobileNo() {


        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}

