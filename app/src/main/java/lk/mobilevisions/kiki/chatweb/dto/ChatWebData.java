package lk.mobilevisions.kiki.chatweb.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatWebData implements Serializable {

    @SerializedName("token")
    @Expose
    private String chatToken;

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }
}