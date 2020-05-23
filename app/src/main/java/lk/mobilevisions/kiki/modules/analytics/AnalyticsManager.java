/**
 * Created by Chatura Dilan Perera on 11/11/2016.
 */
package lk.mobilevisions.kiki.modules.analytics;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;

public class AnalyticsManager {

    API api;
    Application application;

    public AnalyticsManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }

    public void publishAnalytics(final String id, final String action, final APIListener listener) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());;
        System.out.println("jvnvnvnvnvn 1111 " + id);
        System.out.println("jvnvnvnvnvn 2222 " + action);
        api.publishAnalytics(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), action, id, time).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("jvnvnvnvnvn 3333 " + response.message());
                switch (response.code()) {
                    case 200:
                            listener.onSuccess(response.body(), null);
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
                listener.onFailure(new RemoteServerException());
            }
        });
    }

    public void publishActionAnalytics(final String id, final String action, String time, final APIListener listener) {

        api.publishAnalytics(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), action, id, time).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("jvnvnvnvnvn 3333 " + response.message());
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(response.body(), null);
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
                listener.onFailure(new RemoteServerException());
            }
        });
    }


    public void sendActionAnalytics(int contentId,int screenId,int actionId,String screenTime, final APIListener listener) {

        api.sendActionAnalytics(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), contentId, screenId, actionId,screenTime).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("jvnvnvnvnvn 3333 " + response.message());
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(response.body(), null);
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
                listener.onFailure(new RemoteServerException());
            }
        });
    }


}
