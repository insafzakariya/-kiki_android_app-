/**
 * Created by Chatura Dilan Perera on 1/3/2017.
 */
package lk.mobilevisions.kiki.modules.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.modules.api.dto.Episode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Notification;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;

public class NotificationManager {

    API api;
    Application application;

    public NotificationManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }


    public void getNotifications(int limit, final APIListener<List<Notification>> listener) {
        api.getNotifications(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), limit)
                .enqueue(new Callback<List<Notification>>() {

                    @Override
                    public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
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
                    public void onFailure(Call<List<Notification>> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }



    public void getNotificationsCount(final APIListener<NotificationCountResponse> listener) {
        api.getNotificationCount(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application))
                .enqueue(new Callback<NotificationCountResponse>() {

                    @Override
                    public void onResponse(Call<NotificationCountResponse> call, Response<NotificationCountResponse> response) {
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
                    public void onFailure(Call<NotificationCountResponse> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }


    public void markNotificationAsRead(ArrayList<Integer> ids, final APIListener<Void> listener) {
        api.markNotificationsAsRead(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), ids)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
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
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }

    public void clearNotification(final int notificationId, final APIListener<List<Notification>> listener) {
        api.clearNotification(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
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
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }
    public void clearAllNotifications(final APIListener<List<Notification>> listener) {
        api.clearAllNotifications(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
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
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void sendDeviceId(String deviceID,  final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("deviceId",deviceID);
        api.sendDeviceId(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
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
