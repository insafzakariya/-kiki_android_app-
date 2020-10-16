/**
 * Created by Chatura Dilan Perera on 27/8/2016.
 */
package lk.mobilevisions.kiki.modules.auth;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.util.SharedPrefrenceUtils;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;
import lk.mobilevisions.kiki.modules.auth.exceptions.SocialAuthenticationFailedException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.facebook.FacebookSdk.getApplicationContext;
import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE;
import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE_POSITION;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE_POSITION;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_PROGRAM;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_PROGRAM_POSITION;
import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_PROGRAM_POSITION;
//import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE;
//import static lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity.FULLSCREEN_PLAYER_EPISODE_POSITION;
//import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE;
//import static lk.mobilevisions.kiki.ui.main.home.HomeFragment.TV_PLAYER_EPISODE_POSITION;

public class AuthManager {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SOCIAL_TYPE = "social_type";
    public static final String NAME = "name";
    public static final String SOCIAL_TOKEN = "social_token";
    public static final String VIEWER_TYPE = "viewer_type";
    public static final String SOCIAL_TOKEN_SECRET = "social_token_secret";
    public static final int HTTP_CREATED = 201;
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String GENDER = "gender";
    public static final String COUNTRY_CODE = "country";
    public static final String LANGUAGE = "language";
    public static final String SERVER_VALUE_FEMALE = "F";
    public static final String SERVER_VALUE_MALE = "M";
    public static final String SERVER_VALUE_SINHALA = "Si";
    public static final String SERVER_VALUE_TAMIL = "Ta";
    public static final String SERVER_VALUE_ENGLISH = "En";
    public static final String SMS_CODE = "smsCode";
    public static final String IS_WHITELISTED = "whitelisted";
    public static final String DEVICE_ID = "device_id";
    public static final String TV_PLAYER_EPISODE_TRAILER_POSITION = "TV_PLAYER_EPISODE_TRAILER_POSITION";
    API api;
    Application application;

    public AuthManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }

    ;

    public void loginWithFacebook(User user, final APIListener<AuthUser> listener) {
        socialLogin(user, listener);
    }

    public void loginWithGoogle(User user, final APIListener<AuthUser> listener) {
        socialLogin(user, listener);
    }

    public void loginWithDialog(User user, final APIListener<AuthUser> listener) {
        socialLogin(user, listener);
    }

    public void loginWithTwitter(User user, final APIListener<AuthUser> listener) {
        socialLogin(user, listener);
    }

    private void socialLogin(final User user, final APIListener<AuthUser> listener) {
        HashMap<String, Object> request = new HashMap<>();
        System.out.println("sdsdsds 2222 " + user.getSocialAccessToken());
        if (user.getProvider().toString().toLowerCase() != null) {
            request.put(SOCIAL_TYPE, user.getProvider().toString().toLowerCase());
        }
        if (user.getUsername() != null) {
            request.put(USERNAME, user.getUsername());
        } else {
            request.put(USERNAME, "");
        }
        if (user.getPassword() != null) {
            request.put(PASSWORD, user.getPassword());
        } else {
            request.put(PASSWORD, "");
        }
        if (user.getSocialAccessToken() != null) {
            request.put(SOCIAL_TOKEN, user.getSocialAccessToken());
            System.out.println("sdsdsds 1111 " + SOCIAL_TOKEN);
        }
        if (user.getSocialAccessTokenSecret() != null) {
            request.put(SOCIAL_TOKEN_SECRET, user.getSocialAccessTokenSecret());
        }

        if (FirebaseInstanceId.getInstance() != null && FirebaseInstanceId.getInstance().getToken() != null) {
            request.put(DEVICE_ID, FirebaseInstanceId.getInstance().getToken());
            System.out.println("checkingchecking " + FirebaseInstanceId.getInstance().getId());
        }
        if (user == null) {
            System.out.println("checking user status 111111");
        }

        if (user.getSession() == null) {
            System.out.println("checking user status 222222");
        }

        if (user.getSession().getHeaderResponse() == null) {
            System.out.println("checking user status 33333");
        }

        if (user.getSession().getHeaderResponse().getCarrier() == null) {
            System.out.println("checking user status 444444");
        }
        request.put(VIEWER_TYPE, user.getSession().getHeaderResponse().getCarrier().toString().toLowerCase());

        api.authRegister(Utils.Auth.getBasicAuthToken(application), request).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                switch (response.code()) {
                    case 200:
                    case 201:
                        if (response.body() != null) {
                            AuthUser authUser = response.body();
                            Timber.d("Login success with %s. id: %s, name: %s ", user.getProvider().toString(),
                                    authUser.getId(), authUser.getName());
                            List<Object> params = new ArrayList<Object>();
                            boolean isNewUser = false;
                            if (response.code() == HTTP_CREATED) {
                                Timber.i("User is a new user");
                                isNewUser = true;
                            }
                            params.add(isNewUser);
                            listener.onSuccess(authUser, params);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, SocialAuthenticationFailedException.class));
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
            public void onFailure(Call<AuthUser> call, Throwable t) {
                Timber.e(t, "Login with %s fails", user.getProvider().toString());
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void loginWithAccessToken(final APIListener<AuthUser> listener) {
        api.authLoginWithAccessToken(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                System.out.println("checking auth user 33333 " + response.code());
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            AuthUser authUser = response.body();
                            listener.onSuccess(authUser, null);
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
            public void onFailure(Call<AuthUser> call, Throwable t) {
                Timber.e(t, "API call failed");
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void loginWithUsername(User user, final APIListener<AuthUser> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(USERNAME, user.getUsername());
        request.put(PASSWORD, user.getPassword());
        request.put(SOCIAL_TYPE, user.getProvider().toString().toLowerCase());
        request.put(SOCIAL_TOKEN, "");
        if (FirebaseInstanceId.getInstance() != null && FirebaseInstanceId.getInstance().getToken() != null) {
            request.put(DEVICE_ID, FirebaseInstanceId.getInstance().getToken());
        }
        api.authLogin(Utils.Auth.getBasicAuthToken(application), request).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            AuthUser authUser = response.body();
                            Timber.d("Login success. id: %s, name: %s ", authUser.getId(), authUser.getName());
                            listener.onSuccess(authUser, null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, Application.class));
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
            public void onFailure(Call<AuthUser> call, Throwable t) {
                Timber.e(t, "API call failed");
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void registerWithUsername(User user, final APIListener<AuthUser> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(USERNAME, user.getUsername());
        request.put(PASSWORD, user.getPassword());
        request.put(NAME, user.getUsername());
        request.put(SOCIAL_TYPE, user.getProvider().toString().toLowerCase());
        request.put(VIEWER_TYPE, user.getSession().getHeaderResponse().getCarrier().toString().toLowerCase());

        if (FirebaseInstanceId.getInstance() != null && FirebaseInstanceId.getInstance().getToken() != null) {
            request.put(DEVICE_ID, FirebaseInstanceId.getInstance().getToken());
        }

//        //setting if the values are get from whitelisted url
//        if(user.getSession().getHeaderResponse().getCarrier() != null) {
//            request.put(IS_WHITELISTED, true);
//        }else{
//            request.put(IS_WHITELISTED, false);
//        }

        api.authRegister(Utils.Auth.getBasicAuthToken(application), request).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {

                System.out.println("checking register " + response.code());
                switch (response.code()) {
                    case 201:
                        if (response.body() != null) {
                            AuthUser authUser = response.body();
                            Timber.d("registration success. id: %s, name: %s ", authUser.getId(), authUser.getName());
                            listener.onSuccess(authUser, null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
            public void onFailure(Call<AuthUser> call, Throwable t) {
                Timber.e(t, "Registration fail");
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void updateUserMobileNumber(User user, final APIListener<AuthUser> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(MOBILE_NUMBER, user.getMobileNumber());
        if (user.getSession() != null && user.getSession().getHeaderResponse() != null) {
            request.put(IS_WHITELISTED, user.getSession().getHeaderResponse().isWhiteListed());
        }
        switch (user.getLanguage()) {
            case SINHALA:
                request.put(LANGUAGE, SERVER_VALUE_SINHALA);
                break;
            case TAMIL:
                request.put(LANGUAGE, SERVER_VALUE_TAMIL);
                break;
            case ENGLISH:
                request.put(LANGUAGE, SERVER_VALUE_ENGLISH);
        }
        api.updateCurrentUser(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<AuthUser>() {
                    @Override
                    public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Timber.d("Update user details success");
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                    public void onFailure(Call<AuthUser> call, Throwable t) {
                        Timber.e(t, "Update user details failed");
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }

    public void sendUserMobileNumber(String mobileNumber, final APIListener<ForgotPasswordUser> listener) {

        api.forgotPasswordMobileNumberRequest(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), mobileNumber)
                .enqueue(new Callback<ForgotPasswordUser>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordUser> call, Response<ForgotPasswordUser> response) {
                        switch (response.code()) {

                            case 200:
                                if (response.body() != null) {
                                    Timber.d("Update user details success");
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                    public void onFailure(Call<ForgotPasswordUser> call, Throwable t) {
                        Timber.e(t, "Update user details failed");
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }


    public void forgotPasswordOtp(int viwerid, String otp, final APIListener<ForgotPasswordUser> listener) {

        api.forgotPasswordOtpRequest(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), viwerid, otp)
                .enqueue(new Callback<ForgotPasswordUser>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordUser> call, Response<ForgotPasswordUser> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Timber.d("Update user details success");
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                    public void onFailure(Call<ForgotPasswordUser> call, Throwable t) {
                        Timber.e(t, "Update user details failed");
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }

    public void updateNewResetPassword(int viwerid, String password, final APIListener<ForgotPasswordUser> listener) {

        api.forgotNewPasswordUpdate(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), viwerid, password)
                .enqueue(new Callback<ForgotPasswordUser>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordUser> call, Response<ForgotPasswordUser> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Timber.d("Update user details success");
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                    public void onFailure(Call<ForgotPasswordUser> call, Throwable t) {
                        Timber.e(t, "Update user details failed");
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }


    public void updateUserDetails(User user, final APIListener<AuthUser> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(NAME, user.getName());
        if (user.getSession() != null && user.getSession().getHeaderResponse() != null) {
            request.put(IS_WHITELISTED, user.getSession().getHeaderResponse().isWhiteListed());
        }
        switch (user.getLanguage()) {
            case SINHALA:
                request.put(LANGUAGE, SERVER_VALUE_SINHALA);
                break;
            case TAMIL:
                request.put(LANGUAGE, SERVER_VALUE_TAMIL);
                break;
            case ENGLISH:
                request.put(LANGUAGE, SERVER_VALUE_ENGLISH);
        }
        api.updateCurrentUser(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<AuthUser>() {
                    @Override
                    public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Timber.d("Update user details success");
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                    public void onFailure(Call<AuthUser> call, Throwable t) {
                        Timber.e(t, "Update user details failed");
                        listener.onFailure(new NoInternetConnectionException());
                    }
                });
    }

    public void getUserDetails(final APIListener<AuthUser> listener) {
        api.getCurrentUser(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            Timber.d("Getting user details success");
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
            public void onFailure(Call<AuthUser> call, Throwable t) {
                Timber.e(t, "Getting user details failed");
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public void verifyUser(int verificationCode, final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(SMS_CODE, verificationCode);
        api.verifyUserFromSMS(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedException.class));
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
                Timber.e(t, "Verify user by SMS failed");
                listener.onFailure(new NoInternetConnectionException());
            }
        });
    }

    public static void loginToApp(String accessToken) {
        Utils.SharedPrefUtil.saveStringToSharedPref(getApplicationContext()
                , Utils.SharedPrefUtil.PREF_AUTH_ACCESS_TOKEN, accessToken);
        Timber.d("Social token is persisted : %s", accessToken);
    }

    public static void logoutFromApp(Application application) {
        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(application);
        utils.setCurrentItemStreamUrl("");
        utils.setCurrentItemTitle("");
        utils.setCurrentItemThumbnailUrl("");
        utils.setCurrentItemDiscription("");
        utils.setCurrentItemId(0);
        utils.setCurrentItemDuration(0);
        try {
            Badges.removeBadge(application);
        } catch (BadgesNotSupportedException e) {
            Timber.e(e);
        }
        Utils.SharedPrefUtil.saveStringToSharedPref(getApplicationContext()
                , Utils.SharedPrefUtil.PREF_AUTH_ACCESS_TOKEN, null);
        if (Application.PLAYER != null) {
            Application.PLAYER.setEpisodes(null);
            Application.PLAYER.setCurrentVideo(0);
            Application.PLAYER.setCurrentVideoPosition(0);
            Application.PLAYER.setPlayerPaused(false);
            application.setAuthUser(null);
        }

        Utils.SharedPrefUtil.saveIntToSharedPref(application, TV_PLAYER_EPISODE, 0);
        Utils.SharedPrefUtil.saveLongToSharedPref(application, TV_PLAYER_EPISODE_POSITION, 0);
        Utils.SharedPrefUtil.saveIntToSharedPref(application, FULLSCREEN_PLAYER_EPISODE, 0);
        Utils.SharedPrefUtil.saveLongToSharedPref(application, FULLSCREEN_PLAYER_EPISODE_POSITION, 0);
        Utils.SharedPrefUtil.saveIntToSharedPref(application, TV_PLAYER_PROGRAM, 0);
        Utils.SharedPrefUtil.saveLongToSharedPref(application, TV_PLAYER_PROGRAM_POSITION, 0);
        Utils.SharedPrefUtil.saveLongToSharedPref(application, TV_PLAYER_EPISODE_TRAILER_POSITION, 0);
        Utils.SharedPrefUtil.saveStringToSharedPref(application, "kids_mode_password", "");
    }


}
