/**
 * Created by Chatura Dilan Perera on 21/8/2016.
 */
package lk.mobilevisions.kiki.ui.auth;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments = new ArrayList<>();

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new SocialAuthFragment());
        fragments.add(new UserRegisterFragment());
        fragments.add(new UserLoginFragment());
        fragments.add(new MobileNumberFragment());
        fragments.add(new ForgotPasswordMobileFragment());
        fragments.add(new PasswordResetOtpFragment());
        fragments.add(new NewPasswordResetFragment());
        fragments.add(new ResetPasswordCompleteFragment());

//        fragments.add(new UserDetailsFragment());
//        fragments.add(new UserFreeTrailFragment());
//        fragments.add(new RegisterCompleteFragment());

//        fragments.add(new SMSVerifyFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
