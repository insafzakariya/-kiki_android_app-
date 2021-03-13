package lk.mobilevisions.kiki.modules.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatWebDTO implements Serializable {

    @SerializedName("chatId")
    @Expose
    private int chatId;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;
    @SerializedName("chatName")
    @Expose
    private String chatName;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}