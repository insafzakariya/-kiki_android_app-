/**
 * Created by Chatura Dilan Perera on 14/10/2016.
 */
package lk.mobilevisions.kiki.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.annotation.NonNull;
import android.util.Base64;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.ServerError;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;
import lk.mobilevisions.kiki.modules.auth.exceptions.SocialAuthenticationFailedException;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import timber.log.Timber;

public class Utils {

    public static class SharedPrefUtil {

        private static final String SHARED_PREF_FILE = "susilatv_pref";

        public static final String PREF_AUTH_ACCESS_TOKEN = "PREF_AUTH_ACCESS_TOKEN";

        public static void saveStringToSharedPref(Context context, String key, String value) {
            if(context != null) {
                SharedPreferences sharedPref = context.getApplicationContext()
                        .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(key, Security.encrypt(value, context));
                editor.commit();
            }
        }

        public static String getStringFromSharedPref(Context context, String key, String defaultValue) {
            if(context != null){
                SharedPreferences sharedPref = context.getApplicationContext()
                        .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                return Security.decrypt(sharedPref.getString(key, defaultValue), context);
            }
            return null;
        }

        public static void saveIntToSharedPref(Context context, String key, int value) {
            if(context != null){
                SharedPreferences sharedPref = context.getApplicationContext()
                        .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(key, value);
                editor.commit();

            }
        }

        public static int getIntFromSharedPref(Context context, String key, int defaultValue) {

            if(context != null){
                try{
                    SharedPreferences sharedPref = context.getApplicationContext()
                            .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                    return sharedPref.getInt(key, defaultValue);
                }catch(ClassCastException e){
                    return 0;
                }

            }
           return 0;
        }

        public static int getEpisodeIntFromSharedPref(Context context, int key, int defaultValue) {

            if(context != null){
                try{
                    SharedPreferences sharedPref = context.getApplicationContext()
                            .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                    return sharedPref.getInt(String.valueOf(key),defaultValue);
                }catch(ClassCastException e){
                    return 0;
                }

            }
            return 0;
        }


        public static void saveLongToSharedPref(Context context, String key, long value) {
            if(context != null) {
                SharedPreferences sharedPref = context.getApplicationContext()
                        .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong(key, value);
                editor.commit();
            }
        }

        public static long getLongFromSharedPref(Context context, String key, long defaultValue) {
            if(context != null) {
                SharedPreferences sharedPref = context.getApplicationContext()
                        .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                return sharedPref.getLong(key, defaultValue);
            }
            return 0;
        }

        public static void saveBooleanToSharedPref(Context context, String key, boolean value) {
            SharedPreferences sharedPref = context.getApplicationContext()
                    .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }

        public static boolean getBooleanFromSharedPref(Context context, String key, boolean defaultValue) {
            SharedPreferences sharedPref = context.getApplicationContext()
                    .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
            return sharedPref.getBoolean(key, defaultValue);
        }
    }

    public static class App {

        public static Config getConfig(Application application) {
            return ((lk.mobilevisions.kiki.app.Application) application).getConfig();
        }
    }

    public static class Error {

        public static ApplicationException getServerError(Application application, Response response, Class exceptionClass) throws ErrorResponseException {
            Converter<ResponseBody, ServerError> errorConverter = ((lk.mobilevisions.kiki.app.Application) application).getRetrofit()
                    .responseBodyConverter(ServerError.class, ServerError.class.getAnnotations());
            ServerError serverError = null;
            try {
                serverError = errorConverter.convert(response.errorBody());
            } catch (IOException e) {
                Timber.e(e, "Invalid error response");
            }
            if (serverError == null) {
                throw new ErrorResponseException();
            }
            try {
                return (ApplicationException) exceptionClass.getConstructor(String.class, Integer.TYPE)
                        .newInstance(serverError.getErrorMessage(), serverError.getErrorCode());
            } catch (InstantiationException e) {
                Timber.e(e);
            } catch (IllegalAccessException e) {
                Timber.e(e);
            } catch (InvocationTargetException e) {
                Timber.e(e);
            } catch (NoSuchMethodException e) {
                Timber.e(e);
            }
            return null;
        }


        public static void onServiceCallFail(final Context context, Throwable t) {
            String message = "Fatal Error";

            if(t instanceof AuthenticationFailedException ||
                    t instanceof AuthenticationFailedWithAccessTokenException) {
                message = "Authentication Failed. You have been unauthorized from the app." +
                        " Please note your account can access KiKi only from one device at a time";

                Utils.Dialog.createOKDialog(context, message, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        AuthManager.logoutFromApp((lk.mobilevisions.kiki.app.Application) context.getApplicationContext());
                        Intent loginIntent = new Intent(context, SplashActivity.class);
                        context.startActivity(loginIntent);
                        ((Activity) context).finish();
                    }
                });
                return;
            }else if (t instanceof RemoteServerException ||
                    t instanceof SocialAuthenticationFailedException ||
                    t instanceof ApplicationException ||
                    t instanceof InvalidResponseException ||
                    t instanceof ErrorResponseException) {
                message = t.toString();
            } else {
                //message = context.getResources().getString(R.string.network_problem_message);
                message = t.toString();
            }

            Timber.e(t, "Error occurred : %s", message);

            Utils.Dialog.showOKDialog(context, message);
        }

        public static Throwable modifyErrorMessage(Throwable t, int errorCode, String message){
            if(t instanceof ApplicationException){
                if(((ApplicationException) t).getCode() == errorCode){
                    ((ApplicationException)t).setMessage(message);
                }
            }
            return t;
        }
    }


    public static class DateUtil {
        public static Date getDateOnly(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        public static String getDisplayableDate(String date){
            DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
            fromFormat.setLenient(false);
            SimpleDateFormat toFormat = new SimpleDateFormat("dd, MMM yyyy");
            toFormat.setLenient(false);
            try {
                return toFormat.format(fromFormat.parse(date));
            } catch (ParseException e) {
                Timber.e(e);
                return "";
            }
        }

        public static Date getDateFromString(String date){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return format.parse(date);
            } catch (ParseException e) {
                Timber.e(e);
                return null;
            }
        }

        public static String getDisplayableDate(Date date){
            SimpleDateFormat toFormat = new SimpleDateFormat("dd, MMM yyyy");
            toFormat.setLenient(false);
            return toFormat.format(date);
        }
    }


    public static class Text{

        public static String stripHtml(String html) {
            return html.replaceAll("\\<[^>]*>","");
        }
    }


    public static class Dialog {

        public static void showOKDialog(Context context, String message) {

            if(context!= null){
                new MaterialDialog.Builder(context)
                        .title(context.getString(R.string.app_name))
                        .content(message)
                        .positiveText(context.getString(R.string.ok))
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .show();
            }

        }

        public static void createOKDialog(Context context, String message,
                                          MaterialDialog.SingleButtonCallback callback) {
            if(context!=null){
                new MaterialDialog.Builder(context)
                        .title(context.getString(R.string.app_name))
                        .content(message)
                        .positiveText(context.getString(R.string.ok))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .onPositive(callback)
                        .show();
            }

        }

        public static void createOKDialog(Context context, String message,
                                          MaterialDialog.SingleButtonCallback callback,
                                          MaterialDialog.SingleButtonCallback cancelCallback,
                                          DialogInterface.OnCancelListener cancelListener) {

            if(context!=null){
                new MaterialDialog.Builder(context)
                        .title(context.getString(R.string.app_name))
                        .content(message)
                        .positiveText(context.getString(R.string.ok))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .onPositive(callback)
                        .onNegative(cancelCallback)
                        .cancelListener(cancelListener)
                        .show();
            }

        }

        public static MaterialDialog.Builder createDialog(Context context, String message) {

            return new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.app_name))
                    .content(message);
        }
    }


    public static class Auth {

        public static String getBearerToken(lk.mobilevisions.kiki.app.Application application) {
            return Utils.SharedPrefUtil.getStringFromSharedPref(application.getApplicationContext(),
                    Utils.SharedPrefUtil.PREF_AUTH_ACCESS_TOKEN, null);
        }

        public static String getBasicAuthToken(lk.mobilevisions.kiki.app.Application application) {
            String basicToken = Settings.APP_ID + ":" + getKeyHash(application);
            return "Basic " + Base64.encodeToString(basicToken.getBytes(), Base64.DEFAULT).trim();

        }

        public static String getKeyHash(Context context) {
            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA");
                    } catch (NoSuchAlgorithmException e) {
                        Timber.e(e);
                    }
                    md.update(signature.toByteArray());
                    String keyHash = new String(Base64.encode(md.digest(), 0)).
                            replace("\n", "").replace("\r", "");
                    return keyHash;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(e);
            }
            return null;
        }

        public static String getKeyHash(lk.mobilevisions.kiki.app.Application application) {
            Context context = application.getApplicationContext();
            return getKeyHash(context);
        }
    }


    public static class Security {

        private static final String ALGORITHM = "AES";
        private static final String KEY_PADDING = "susilamobile";

        public static String generateSha512Hash(String value) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            md.update(value.getBytes());
            byte[] digest = md.digest();

            return Base64.encodeToString(digest, Base64.DEFAULT);
        }


        public static String encrypt(String value, Context context) {
            if (value != null) {
                try {
                    return AESCrypt.encrypt(Auth.getKeyHash(context) + KEY_PADDING, value);
                } catch (GeneralSecurityException e) {
                    Timber.e(e);
                }
            }

            return null;
        }


        public static String decrypt(String value, Context context) {
            if (value != null && !value.isEmpty()) {
                try {
                    return AESCrypt.decrypt(Auth.getKeyHash(context) + KEY_PADDING, value);
                } catch (GeneralSecurityException e) {
                    Timber.e(e);
                }
            }

            return null;
        }


    }

    public static class Packages{

        public static boolean isAppInstalled(Context context, String packageName) {
            try {
                context.getPackageManager().getApplicationInfo(packageName, 0);
                return true;
            }
            catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
    }

}
