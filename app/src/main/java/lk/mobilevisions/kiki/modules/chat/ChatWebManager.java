package lk.mobilevisions.kiki.modules.chat;

import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.chatweb.dto.ChatWebToken;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.ChatAPI;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChatWebManager {

    ChatAPI chatAPI;
    Application application;

    public ChatWebManager(Application application, ChatAPI chatAPI){
        this.application = application;
        this.chatAPI = chatAPI;
    }

    public void getChatChannels(String username, String secret, final APIListener<List<ChatWebDTO>> listener) {

        HashMap<String, Object> request = new HashMap<>();
        request.put("username", username);
        request.put("secret", secret);

        chatAPI.getChatChannels("application/json", request).enqueue(new Callback<List<ChatWebDTO>>() {

            @Override
            public void onResponse(Call<List<ChatWebDTO>> call, Response<List<ChatWebDTO>> response) {

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);

                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<ChatWebDTO>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void logintoChatWeb(String name, int viewerId, String username, String secret, final APIListener<ChatWebToken> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("identity", viewerId);
        request.put("username", username);
        request.put("secret", secret);

        chatAPI.chatLogin("application/json", request).enqueue(new Callback<ChatWebToken>() {

                    @Override
                    public void onResponse(Call<ChatWebToken> call, Response<ChatWebToken> response) {
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);

                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatWebToken> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }
}
