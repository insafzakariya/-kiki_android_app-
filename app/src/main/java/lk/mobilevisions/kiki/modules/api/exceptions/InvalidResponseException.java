package lk.mobilevisions.kiki.modules.api.exceptions;

import timber.log.Timber;

/**
 * Created by dilan on 10/31/16.
 */

public class InvalidResponseException extends Exception {

    public InvalidResponseException() {
        Timber.e("Invalid response received from the server.");
    }

    @Override
    public String toString() {
        return "Invalid response received from the server.";
    }
}
