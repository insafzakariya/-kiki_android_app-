/**
 * Created by Chatura Dilan Perera on 14/10/2016.
 */
package lk.mobilevisions.kiki.modules.auth.exceptions;

import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;

public class AuthenticationFailedWithAccessTokenException extends ApplicationException {

    public AuthenticationFailedWithAccessTokenException(String message, int code) {
        super(message, code);
    }

}
