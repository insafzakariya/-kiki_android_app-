/**
 * Created by Chatura Dilan Perera on 11/11/2016.
 */
package lk.mobilevisions.kiki.modules.subscriptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;

public class SubscriptionsManager {

    API api;
    Application application;

    public SubscriptionsManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }

    public void subscribePromotion(final String scratchNumber, final APIListener<Package> listener) {
        api.subscribePromotion(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), scratchNumber).enqueue(new Callback<Package>() {

            @Override
            public void onResponse(Call<Package> call, Response<Package> response) {
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
            public void onFailure(Call<Package> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void getActivatedPackage(final APIListener<Package> listener) {

        api.getActivatedPackage(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<Package>() {

            @Override
            public void onResponse(Call<Package> call, Response<Package> response) {
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
            public void onFailure(Call<Package> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }


    public void getAllSubscriptionPackages(final APIListener<List<Package>> listener) {

        api.getAllSubscriptionPackages(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<List<Package>>() {

            @Override
            public void onResponse(Call<List<Package>> call, Response<List<Package>> response) {
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
            public void onFailure(Call<List<Package>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }


    public void generateSubscriptionToken(int packageId, final APIListener<PackageToken> listener) {

        api.generateSubscriptionToken(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), packageId).enqueue(new Callback<PackageToken>() {

            @Override
            public void onResponse(Call<PackageToken> call, Response<PackageToken> response) {
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
            public void onFailure(Call<PackageToken> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }
}
