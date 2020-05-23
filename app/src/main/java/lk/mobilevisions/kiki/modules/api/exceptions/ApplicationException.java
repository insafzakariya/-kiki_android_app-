package lk.mobilevisions.kiki.modules.api.exceptions;

import timber.log.Timber;

/**
 * Created by dilan on 10/25/16.
 */

public class ApplicationException extends Exception {

    private String message;
    private int code;

    public ApplicationException(String message, int code){
        this.message = message;
        this.code = code;
        Timber.e("Application Exception");
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
