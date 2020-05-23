/**
 * Created by Chatura Dilan Perera on 6/11/2016.
 */
package lk.mobilevisions.kiki.modules.api.exceptions;

import timber.log.Timber;

public class NoInternetConnectionException extends Exception {

    public NoInternetConnectionException() {
        Timber.e("No Internet connection.");
    }

    @Override
    public String toString() {
        return "No Internet connection.";
    }
}
