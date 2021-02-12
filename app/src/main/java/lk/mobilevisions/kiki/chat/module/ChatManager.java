package lk.mobilevisions.kiki.chat.module;

import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.chat.module.dto.ChannelDto;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;
import lk.mobilevisions.kiki.chat.module.dto.ChatToken;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
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

    public void getChannels(final APIListener<List<ChannelDto>> listener) {

        api.getChannels(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<List<ChannelDto>>() {

            @Override
            public void onResponse(Call<List<ChannelDto>> call, Response<List<ChannelDto>>response) {
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
            public void onFailure(Call<List<ChannelDto>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void getRoleDetail(final APIListener<ChannelDto> listener) {

        api.getRoleDetails(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<ChannelDto>() {

            @Override
            public void onResponse(Call<ChannelDto> call, Response<ChannelDto> response) {
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
            public void onFailure(Call<ChannelDto> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void createChatMember(String sid, String accountSid, String serviceSid, int roleId, int identity, String name, List<Integer> channelIdList,  final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("sid",sid);
        request.put("accountSid",accountSid);
        request.put("serviceSid",serviceSid);
        request.put("roleId",roleId);
        request.put("identity",identity);
        request.put("name",name);
        request.put("imagePath","");
        request.put("channelIds",channelIdList);

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

    public void getChatMembers(int id, String type, final APIListener<List<ChatMember>> listener) {

        api.getChatMembers(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), id, type).enqueue(new Callback<List<ChatMember>>() {

            @Override
            public void onResponse(Call<List<ChatMember>> call, Response<List<ChatMember>>response) {

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
            public void onFailure(Call<List<ChatMember>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }


}
