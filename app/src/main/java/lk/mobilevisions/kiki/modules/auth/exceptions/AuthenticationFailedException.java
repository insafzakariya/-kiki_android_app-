/**
 * Created by Chatura Dilan Perera on 4/10/2016.
 */
package lk.mobilevisions.kiki.modules.auth.exceptions;

import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;

public class AuthenticationFailedException extends ApplicationException {

    public AuthenticationFailedException(String message, int code) {
        super(message, code);
    }
}
