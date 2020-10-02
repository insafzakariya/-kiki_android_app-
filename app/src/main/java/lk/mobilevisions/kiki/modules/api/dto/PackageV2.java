package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PackageV2 {

    @SerializedName("buttonEnable")
    @Expose
    private boolean buttonEnable;

    @SerializedName("body")
    @Expose
    private String messageBody;

    @SerializedName("subscribe")
    @Expose
    private boolean subStatus;

    @SerializedName("title")
    @Expose
    private String titleText;

    @SerializedName("packageId")
    @Expose
    private int packageId;

    @SerializedName("expireDate")
    @Expose
    private long expireDate;

    @SerializedName("trial")
    @Expose
    private boolean trialStatus;

    public boolean isButtonEnable() {
        return buttonEnable;
    }

    public void setButtonEnable(boolean buttonEnable) {
        this.buttonEnable = buttonEnable;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public boolean isSubStatus() {
        return subStatus;
    }

    public void setSubStatus(boolean subStatus) {
        this.subStatus = subStatus;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isTrialStatus() {
        return trialStatus;
    }

    public void setTrialStatus(boolean trialStatus) {
        this.trialStatus = trialStatus;
    }
}
