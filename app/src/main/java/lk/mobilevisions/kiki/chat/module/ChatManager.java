package lk.mobilevisions.kiki.chat.module;

import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.chat.module.dto.Channels;
import lk.mobilevisions.kiki.chat.module.dto.ChatToken;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Package;
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

public class ChatManager {

    API api;
    Application application;

    public ChatManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }

    public void getAccessToken(final APIListener<ChatToken> listener) {

        api.getAccessToken(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<ChatToken>() {

            @Override
            public void onResponse(Call<ChatToken> call, Response<ChatToken> response) {
                System.out.println("EPID: "+ response.code()+" "+response.body());
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
            public void onFailure(Call<ChatToken> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void getChannels(final APIListener<List<Channels>> listener) {

        api.getChannels(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<List<Channels>>() {

            @Override
            public void onResponse(Call<List<Channels>> call, Response<List<Channels>>response) {
                System.out.println("EPID: "+ response.code()+" "+response.body());
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
            public void onFailure(Call<List<Channels>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void getRoleDetail(final APIListener<Channels> listener) {

        api.getRoleDetails(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<Channels>() {

            @Override
            public void onResponse(Call<Channels> call, Response<Channels> response) {
                System.out.println("EPID: "+ response.code()+" "+response.body());
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
            public void onFailure(Call<Channels> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void createChatMember(String sid, String accountSid, String serviceSid, int roleId,  final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("sid",sid);
        request.put("accountSid",accountSid);
        request.put("serviceSid",serviceSid);
        request.put("roleId",roleId);

        api.createMember(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
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
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }


}
