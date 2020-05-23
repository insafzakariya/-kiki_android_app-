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

import java.util.Arrays;
import java.util.HashMap;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.databinding.FragmentSocialAuthBinding;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialAuthFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_LOGIN_RESULT = "loginResult";

    FragmentSocialAuthBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_social_auth, container, false);
        binding.createAccountTextview.setOnClickListener(this);
        binding.loginWithEmailTextview.setOnClickListener(this);
        binding.gmailLoginLayout.setOnClickListener(this);
        binding.facebookLoginLayout.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(),"SocialAuthFragment",null);


        LoginManager.getInstance().registerCallback(((LoginActivity) getActivity()).fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("facebook login checkin 111111 ");
                Timber.i("Facebook sign in was successful");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put(PARAM_LOGIN_RESULT, loginResult);
                Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.FACEBOOK, params));
            }

            @Override
            public void onCancel() {
                System.out.println("facebook login checkin 22222 ");
                Timber.i("Facebook sign in canceled");
            }

            @Override
            public void onError(FacebookException e) {
                System.out.println("facebook login checkin 3333333 " + e);
                Timber.e(e, "Facebook sign in failed");
            }
        });
        String selectedLanguage = Utils.SharedPrefUtil.getStringFromSharedPref(getActivity(),Constants.KEY_LANGUAGE_PHONE,null);
        if(selectedLanguage!= null){
          binding.btnBack.setVisibility(View.GONE);
        }else{
            binding.btnBack.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_account_textview:
                Application.BUS.post(new UserCustomRegister());
                break;
            case R.id.login_with_email_textview:
                Application.BUS.post(new UserCustomLogin());
                break;
            case R.id.gmail_login_layout:
                Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.GOOGLE, null));
                break;
            case R.id.facebook_login_layout:
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                break;
            case R.id.btn_back:
                Application.BUS.post(new UserNavigatesBack());
                break;
            default:
        }

    }

    public class UserCustomLogin {
    }
    public class UserCustomRegister {
    }
    public class UserNavigatesBack {
    }
}



