package lk.mobilevisions.kiki.ui.auth;

import java.util.HashMap;

import lk.mobilevisions.kiki.modules.auth.AuthOptions;

public class LoginOptionsEvent {

    private AuthOptions.AuthMethod method;

    private HashMap<String, Object> params;

    public LoginOptionsEvent(AuthOptions.AuthMethod method, HashMap<String, Object> params) {
        this.method = method;
        this.params = params;
    }

    public AuthOptions.AuthMethod getOptions() {
        return method;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }
}
