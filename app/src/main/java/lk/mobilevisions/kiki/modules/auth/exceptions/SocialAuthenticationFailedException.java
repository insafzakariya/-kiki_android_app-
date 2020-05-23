/**
 * Created by Chatura Dilan Perera on 7/10/2016.
 */
package lk.mobilevisions.kiki.modules.auth.exceptions;

import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;

public class SocialAuthenticationFailedException extends ApplicationException {

    public SocialAuthenticationFailedException(String message, int code) {
        super(message, code);
    }
}
