package lk.mobilevisions.kiki.chat;

import android.app.Activity;
import android.content.Context;

import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;

import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.chat.listeners.TaskCompletionListener;
import lk.mobilevisions.kiki.chat.module.ChatManager;
import lk.mobilevisions.kiki.chat.module.dto.ChatToken;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;


public class ChatClientManager {

    @Inject
    ChatManager chatManager;

    private ChatClient chatClient;
    private Context context;
    private ChatClientBuilder chatClientBuilder;


    public ChatClientManager(Context context,Application application) {
        this.context = context;
        this.chatClientBuilder = new ChatClientBuilder(this.context);
//        Activity activity = (Activity) context;
//        application.getInjector().inject(this);
    }

    public void addClientListener(ChatClientListener listener) {
        if (this.chatClient != null) {
            this.chatClient.addListener(listener);
        }
    }

    public ChatClient getChatClient() {
        return this.chatClient;
    }

    public void setChatClient(ChatClient client) {
        this.chatClient = client;
    }

    public void connectClient(String token,final TaskCompletionListener<Void, String> listener) {
        ChatClient.setLogLevel(android.util.Log.DEBUG);
        buildClient(token, listener);
//        chatManager.getAccessToken(new APIListener<ChatToken>() {
//            @Override
//            public void onSuccess(ChatToken chatToken, List<Object> params) {
//                System.out.println("dydhdyhdhdhdh  66666 " + chatToken.getDataObject().getToken());
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//
//            }
//        });

    }


    private void buildClient(String token, final TaskCompletionListener<Void, String> listener) {
        System.out.println("rurufhufff 00000 ");

        chatClientBuilder.build(token, new TaskCompletionListener<ChatClient, String>() {
            @Override
            public void onSuccess(ChatClient chatClient) {
                System.out.println("rurufhufff 11111 ");
                ChatClientManager.this.chatClient = chatClient;
                listener.onSuccess(null);
            }

            @Override
            public void onError(String message) {
                System.out.println("rurufhufff 2222 " + message);
                listener.onError(message);
            }
        });
    }

    public void shutdown() {
        if (chatClient != null) {
            chatClient.shutdown();
        }
    }


}
