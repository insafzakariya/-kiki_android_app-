/**
 * Created by Chatura Dilan Perera on 7/10/2016.
 */
package lk.mobilevisions.kiki.modules.api.exceptions;

import timber.log.Timber;

public class RemoteServerException extends Exception {


    public RemoteServerException() {
        Timber.e("Remote Server Exception");
    }

    @Override
    public String toString() {
        return "Remote server exception.";
    }
}
