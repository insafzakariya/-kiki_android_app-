/**
 * Created by Chatura Dilan Perera on 23/9/2016.
 */
package lk.mobilevisions.kiki.modules.info;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.ComAPI;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class InfoManager {

    ComAPI api;
    Application application;

    public InfoManager(Application application, ComAPI api) {
        this.application = application;
        this.api = api;
    }

    public void getHeaders(final APIListener<HeaderResponse> listener, final Context context) {

        api.getInfoHeaders().enqueue(new Callback<HeaderResponse>() {
            @Override
            public void onResponse(Call<HeaderResponse> call, Response<HeaderResponse> response) {
                System.out.println("gkadfgadfg 1111  " + response.body());
                System.out.println("gkadfgadfg 2222  " + response.code());
                switch (response.code()) {

                    case 200:
                        HeaderResponse headerResponse = response.body();
                        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            System.out.println("gkadfgadfg 3333  " + response);
                            headerResponse.setMobileDataConnection(true);
                        }else {
                            System.out.println("gkadfgadfg 4444  " + response);
                            headerResponse.setMobileDataConnection(false);
                        }
                        listener.onSuccess(headerResponse, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response,
                                    AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response,
                                    ApplicationException.class));
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
            public void onFailure(Call<HeaderResponse> call, Throwable t) {
                System.out.println("gkadfgadfg 3333  " + t.getMessage());
                System.out.println("gkadfgadfg 44444  " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

}

