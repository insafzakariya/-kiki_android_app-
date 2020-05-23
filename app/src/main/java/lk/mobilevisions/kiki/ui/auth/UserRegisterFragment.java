package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.FragmentUserRegisterBinding;
import lk.mobilevisions.kiki.modules.auth.User;

public class UserRegisterFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_NAME = "name";
    public static final String CONFIRM_PASSWORD_VALUE = "CONFIRM_PASSWORD_VALUE";
    public static final String EMAIL = "EMAIL";

    boolean isRegister;

    public static final String PARAM_IS_REGISTER = "isRegister";


    public static final String PARAM_LOGIN_RESULT = "loginResult";


    FragmentUserRegisterBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_register, container, false);
        binding.signUpLayout.setOnClickListener(this);
        binding.loginWithEmailTextview.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.usernameEdittext.clearFocus();
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(), "UserRegisterFragment", null);


        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_layout:

                UserRegisterAuthEvent event = new UserRegisterAuthEvent();
                event.getParams().put(CONFIRM_PASSWORD_VALUE, binding.confirmPasswordEdittext.getText().toString());
                event.getParams().put(EMAIL, binding.usernameEdittext.getText().toString().trim());
                User user = new User();
                user.setUsername(binding.usernameEdittext.getText().toString().trim());
                user.setPassword(binding.passwordEdittext.getText().toString());
                user.setName(binding.usernameEdittext.getText().toString());
                user.setSession(((Application) getActivity().getApplication()).getSession());
                event.setUser(user);
                Application.BUS.post(event);
                break;
            case R.id.login_with_email_textview:
                Application.BUS.post(new UserCustomLogin());
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            default:
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }


    public class UserRegisterAuthEvent {

        private HashMap<String, Object> params;
        private User user;


        public UserRegisterAuthEvent() {
            params = new HashMap<>();
        }

        public HashMap<String, Object> getParams() {
            return params;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public class UserCustomLogin {
    }
    public class UserNavigatesBack {
    }
}
