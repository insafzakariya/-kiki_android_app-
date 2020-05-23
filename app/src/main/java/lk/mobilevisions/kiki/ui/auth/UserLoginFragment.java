package lk.mobilevisions.kiki.ui.auth;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.HashMap;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.databinding.FragmentUserLoginBinding;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import timber.log.Timber;

public class UserLoginFragment extends Fragment implements View.OnClickListener {


    public static final String PARAM_LOGIN_RESULT = "loginResult";

    FragmentUserLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_login, container, false);
        binding.signInLayout.setOnClickListener(this);
        binding.createAccountTextview.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.userNameEdittext.requestFocus();
        binding.forgotPasswordTextview.setOnClickListener(this);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(),"UserLoginFragment",null);

        LoginManager.getInstance().registerCallback(((LoginActivity) getActivity()).fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Timber.i("Facebook sign in was successful");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put(PARAM_LOGIN_RESULT, loginResult);
                Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.FACEBOOK, params));
            }

            @Override
            public void onCancel() {
                Timber.i("Facebook sign in canceled");
            }

            @Override
            public void onError(FacebookException e) {
                Timber.e(e, "Facebook sign in failed");
            }
        });

        toggleLoginOptions();

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_layout:
                UserLoginAuthEvent event = new UserLoginAuthEvent();
                User user = new User();
                user.setUsername(binding.userNameEdittext.getText().toString());
                user.setPassword(binding.passwordEdittext.getText().toString());
                user.setName(binding.userNameEdittext.getText().toString());
                user.setSession(((Application) getActivity().getApplication()).getSession());
                event.setUser(user);
                Application.BUS.post(event);
                break;
            case R.id.create_account_textview:
                Application.BUS.post(new UserCustomRegister());
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            case R.id.forgot_password_textview:
                Application.BUS.post(new UserCustomForgetPassword());
            default:
        }

    }


    private void toggleLoginOptions() {
        HeaderResponse headerResponse = ((Application) getActivity().getApplication()).getSession()
                .getHeaderResponse();

        if (headerResponse != null) {
            if (headerResponse.getCarrier() == HeaderResponse.Carrier.MOBITEL
                    && headerResponse.isMobileDataConnection()) {
            } else if (headerResponse.getCarrier() == HeaderResponse.Carrier.DIALOG
                    && headerResponse.isMobileDataConnection()) {
                //TODO VISIBILITY DISABLED DIALOG
                //binding.buttonLoginWithDialog.setVisibility(View.VISIBLE);
                //TODO END VISIBILITY DISABLED DIALOG
            } else {
                //TODO VISIBILITY DISABLED DIALOG
                //binding.buttonLoginWithDialog.setVisibility(View.VISIBLE);
                //TODO END VISIBILITY DISABLED DIALOG
            }
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }


    public class UserLoginAuthEvent {

        private HashMap<String, Object> params;
        private User user;


        public UserLoginAuthEvent() {
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

//    @Override
//    public void onResume() {
//        binding.userNameEdittext.requestFocus();
//        super.onResume();
//    }




    public class UserCustomRegister {
    }
    public class UserNavigatesBack {
    }

    public class UserCustomForgetPassword {
    }
}
