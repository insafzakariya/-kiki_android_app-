/**
 * Created by Chatura Dilan Perera on 1/3/2017.
 */
package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("messageType")
    @Expose
    private Integer messageType;
    @SerializedName("messageId")
    @Expose
    private Integer messageId;
    @SerializedName("messageDate")
    @Expose
    private String messageDate;
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
