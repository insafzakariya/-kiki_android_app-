package lk.mobilevisions.kiki.ui.auth;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.mobilevisions.kiki.ui.widgets.MaterialSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.databinding.FragmentUserDetailsBinding;
import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {

    public static final String USER_DETAILS = "USER_DETAILS";

    private String birthDate;
    private String birthYear;
    private String selectedGender = "Male";
    private String selectedLanguage = "English";
    private int monthOfYear;
    private int dayOfMonth;
    static final String[] Months = new String[]{"Jan", "Feb",
            "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};
    ArrayList<String> days;
    FragmentUserDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HeaderResponse headerResponse = ((Application) getActivity().getApplication()).getSession()
                .getHeaderResponse();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false);
        ((Application) getActivity().getApplication()).getAnalytics().setCurrentScreen(getActivity(),"UserDetailsFragment",null);

        binding.englishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageButtonStates("english");
            }
        });
        binding.tamilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageButtonStates("tamil");
            }
        });
        binding.sinhalaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageButtonStates("sinhala");
            }
        });

        binding.maleInnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGenderButtonStates("male");
            }
        });
        binding.femaleInnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGenderButtonStates("female");
            }
        });
//
        binding.confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monthOfYear == 0 || dayOfMonth == 0) {
                    Utils.Dialog.createOKDialog(getActivity(), "Please select your date of birth", null);

                } else {
                    System.out.println("fldbvklkdnb " + birthYear);
                    birthDate = String.valueOf(birthYear) + "-" + String.format("%02d", monthOfYear) + "-" + String.format("%02d", dayOfMonth);
                    User user = new User();
                    user.setDateOfBirth(birthDate);
                    user.setGender(AuthOptions.Gender.valueOf
                            (selectedGender.toUpperCase()));
                    user.setLanguage(AuthOptions.Language.valueOf
                            (selectedLanguage.toUpperCase()));
                    user.setSession(((Application) getActivity().getApplication()).getSession());
                    Application.BUS.post(new UserDetailsEvent(user));
                }

            }
        });
        binding.yearSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                birthYear = item.toString();

            }
        });
        binding.monthSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                monthOfYear = position + 1;
                binding.monthSpinner.setItems(Months);
                binding.monthLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
                binding.monthSpinner.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
                binding.monthSpinner.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
                binding.monthSpinner.setArrowColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            }
        });
        binding.daySpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                dayOfMonth = position + 1;
                binding.daySpinner.setItems(days);
                binding.dayLauout.setBackgroundResource(R.drawable.rounded_corner_white_background);
                binding.daySpinner.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
                binding.daySpinner.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
                binding.daySpinner.setArrowColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            }
        });



        int maxYear = Calendar.getInstance().get(Calendar.YEAR) - 4;
        List<String> years = new ArrayList<>();
        int year = maxYear;
        for (int i = 0; i < 101; i++) {
            years.add(String.valueOf(year--));
        }
        binding.yearSpinner.setItems(years);
        binding.yearSpinner.setSelectedIndex(20);
        birthYear = years.get(20);
        binding.monthSpinner.setItems(Months);


        days = new ArrayList<String>();
        for (int i = 1; i <= 31; i++) {
            days.add(Integer.toString(i));
        }

        binding.daySpinner.setItems(days);


        return binding.getRoot();
    }


    private void changeGenderButtonStates(String gender) {

        if (gender.equals("male")) {
            selectedGender = "Male";
            binding.maleLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.femaleLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.maleTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            binding.femaleTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.male_female_text_color));

        } else if (gender.equals("female")) {
            selectedGender = "Female";
            binding.femaleLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.maleLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.femaleTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            binding.maleTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.male_female_text_color));

        }
    }

    private void changeLanguageButtonStates(String language) {

        if (language.equals("english")) {
            selectedLanguage = "English";
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.englishTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            binding.tamilTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
        } else if (language.equals("tamil")) {
            selectedLanguage = "Tamil";
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.tamilTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
            binding.englishTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
        } else if (language.equals("sinhala")) {

            selectedLanguage = "Sinhala";
            binding.sinhalaLayout.setBackgroundResource(R.drawable.rounded_corner_white_background);
            binding.tamilLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);
            binding.englishLayout.setBackgroundResource(R.drawable.rounded_corner_inactive);

            binding.sinhalaTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_black_1000));
            binding.tamilTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
            binding.englishTextview.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_white_1000));
        }

    }

    public class UserDetailsEvent {

        private User user;


        public UserDetailsEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }


    }


}
