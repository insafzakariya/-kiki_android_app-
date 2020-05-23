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

import lk.mobilevisions.kiki.databinding.FragmentUserAuthBinding;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAuthFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_NAME = "name";
    public static final String CONFIRM_PASSWORD_VALUE = "CONFIRM_PASSWORD_VALUE";

    boolean isRegister;

    public static final String PARAM_IS_REGISTER = "isRegister";


    public static final String PARAM_LOGIN_RESULT = "loginResult";

    FragmentUserAuthBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_auth, container, false);
        binding.buttonAuth.setOnClickListener(this);
        binding.textViewRegister.setOnClickListener(this);
        binding.editTextUsername.clearFocus();


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
            case R.id.buttonAuth:
                UserAuthEvent event = new UserAuthEvent();
                event.getParams().put(PARAM_IS_REGISTER, isRegister);
                event.getParams().put(CONFIRM_PASSWORD_VALUE, binding.editTextConfirmPass.getText().toString());
                User user = new User();
                user.setUsername(binding.editTextUsername.getText().toString());
                user.setPassword(binding.editTextPassword.getText().toString());
                user.setName(binding.editTextName.getText().toString());
                user.setSession(((Application) getActivity().getApplication()).getSession());
                event.setUser(user);
                Application.BUS.post(event);
                break;
            case R.id.textViewRegister:
                toggleLoginAndRegister(isRegister);
                break;
//            case R.id.buttonLoginWithGoogle:
//                Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.GOOGLE, null));
//                break;
//            case R.id.buttonLoginWithTwitter:
//                Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.TWITTER, null));
//                break;
//            case R.id.buttonLoginWithDialog:
//                if (Utils.Packages.isAppInstalled(getActivity(), Constants.AUTH_DIALOG_PACKAGE_NAME)) {
//                    ViuAuth viuAuth = new ViuAuth(getActivity().getApplicationContext());
//                    String dialogAuthTokenValue = null;
//                    try {
//                        dialogAuthTokenValue = viuAuth.getViuAutherizationToken();
//                    } catch (Exception e) {
//                        Timber.e(e);
//                    }
//
//                    if(dialogAuthTokenValue != null){
//                        Utils.SharedPrefUtil.saveBooleanToSharedPref(getActivity(), Constants.AUTH_DIALOG_AUTH, true);
//                        Application.BUS.post(new LoginOptionsEvent(AuthOptions.AuthMethod.DIALOG, null));
//                    } else {
//                        Utils.Dialog.createOKDialog(getContext(), "Please click on KiKi icon in Dialog ViU Apps after you login to ViU", new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(Constants.AUTH_DIALOG_PACKAGE_NAME);
//                                startActivity(LaunchIntent);
//                                Utils.SharedPrefUtil.saveBooleanToSharedPref(getActivity(), Constants.AUTH_DIALOG_AUTH, true);
//                                getActivity().finish();
//                            }
//                        });
//                    }
//
//                } else {
//                    Utils.Dialog.createOKDialog(getActivity(), "Dialog ViU is not installed in your device", null);
//                }
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

    public void toggleLoginAndRegister(boolean isForRegister) {

        if (isForRegister) {
            binding.tableRowName.setVisibility(View.GONE);
            binding.tableRowNameLabel.setVisibility(View.GONE);
            binding.tableRowConfirmPass.setVisibility(View.GONE);
            binding.tableRowConfirmPassLabel.setVisibility(View.GONE);
            binding.textViewRegister.setText(getString(R.string.create_an_account));
            binding.buttonAuth.setText(getString(R.string.login));
            binding.textViewAuthMessage.setText(getString(R.string.enter_login));
            binding.textViewRegister.setVisibility(View.VISIBLE);
            binding.textViewLoginSignIn.setVisibility(View.VISIBLE);
            isRegister = false;
        } else {
            binding.tableRowName.setVisibility(View.GONE); //Removed from Registration
            binding.tableRowNameLabel.setVisibility(View.GONE); //Removed from Registration
            binding.tableRowConfirmPass.setVisibility(View.VISIBLE);
            binding.tableRowConfirmPassLabel.setVisibility(View.VISIBLE);
            binding.textViewRegister.setText(getString(R.string.back_to_login));
            binding.textViewRegister.setVisibility(View.GONE);
            binding.buttonAuth.setText(R.string.register);
            binding.textViewAuthMessage.setText(getString(R.string.enter_register));
            binding.textViewLoginSignIn.setVisibility(View.GONE);
            isRegister = true;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            toggleLoginAndRegister(true);
        }

    }



    public class UserAuthEvent {

        private HashMap<String, Object> params;
        private User user;


        public UserAuthEvent() {
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

}
