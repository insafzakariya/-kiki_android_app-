package lk.mobilevisions.kiki.ui.widgets.countryselect;

import android.content.Context;
import android.content.res.TypedArray;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import lk.mobilevisions.kiki.R;

public class IntlPhoneInput extends RelativeLayout {
    private final String DEFAULT_COUNTRY = Locale.getDefault().getCountry();

    // UI Views
    private Spinner mCountrySpinner;
    private EditText mPhoneEdit;

    //Adapters
    private CountrySpinnerAdapter mCountrySpinnerAdapter;
    private ThreeDigitPhoneFormatWatcher mPhoneNumberWatcher = new ThreeDigitPhoneFormatWatcher();

    //Util
    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    // Fields
    private Country mSelectedCountry;
    private CountriesFetcher.CountryList mCountries;
    private IntlPhoneInputListener mIntlPhoneInputListener;

    /**
     * Constructor
     *
     * @param context Context
     */
    public IntlPhoneInput(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public IntlPhoneInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Init after constructor
     */
    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_phone_input, this);

        /**+
         * Country spinner
         */
        mCountrySpinner = (Spinner) findViewById(R.id.intl_phone_edit__country);
        mCountrySpinnerAdapter = new CountrySpinnerAdapter(getContext());
        mCountrySpinner.setAdapter(mCountrySpinnerAdapter);

        mCountries = CountriesFetcher.getCountries(getContext());
        mCountrySpinnerAdapter.addAll(mCountries);
        mCountrySpinner.setOnItemSelectedListener(mCountrySpinnerListener);

        setFlagDefaults(attrs);

        /**
         * Phone text field
         */
        mPhoneEdit = (EditText) findViewById(R.id.intl_phone_edit__phone);
        mPhoneEdit.addTextChangedListener(mPhoneNumberWatcher);
        mPhoneEdit.setImeOptions(mPhoneEdit.getImeOptions() | EditorInfo.IME_ACTION_DONE);
        setDefault();
        setEditTextDefaults(attrs);
    }

    private void setFlagDefaults(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IntlPhoneInput);
        int paddingEnd = a.getDimensionPixelSize(R.styleable.IntlPhoneInput_flagPaddingEnd, getResources().getDimensionPixelSize(R.dimen.flag_default_padding_right));
        int paddingStart = a.getDimensionPixelSize(R.styleable.IntlPhoneInput_flagPaddingStart, getResources().getDimensionPixelSize(R.dimen.flag_default_padding));
        int paddingTop = a.getDimensionPixelSize(R.styleable.IntlPhoneInput_flagPaddingTop, getResources().getDimensionPixelSize(R.dimen.flag_default_padding));
        int paddingBottom = a.getDimensionPixelSize(R.styleable.IntlPhoneInput_flagPaddingBottom, getResources().getDimensionPixelSize(R.dimen.flag_default_padding));
        mCountrySpinner.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
        a.recycle();
    }

    private void setEditTextDefaults(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IntlPhoneInput);
        int textSize = a.getDimensionPixelSize(R.styleable.IntlPhoneInput_textSize, getResources().getDimensionPixelSize(R.dimen.text_size_default));
        mPhoneEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int color = a.getColor(R.styleable.IntlPhoneInput_textColor, -1);
        if (color != -1) {
            mPhoneEdit.setTextColor(color);
        }
        int hintColor = a.getColor(R.styleable.IntlPhoneInput_textColorHint, -1);
        if (hintColor != -1) {
            mPhoneEdit.setHintTextColor(hintColor);
        }
        a.recycle();
    }

    /**
     * Hide keyboard from phoneEdit field
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mPhoneEdit.getWindowToken(), 0);
    }

    /**
     * Set default value
     * Will try to retrieve phone number from device
     */
    public void setDefault() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String phone = telephonyManager.getLine1Number();
            if (phone != null && !phone.isEmpty()) {
                this.setNumber(phone);
            } else {
                String iso = telephonyManager.getNetworkCountryIso();
                setEmptyDefault(iso);
            }
        } catch (SecurityException e) {
            setEmptyDefault();
        }
    }

    /**
     * Set default value with
     *
     * @param iso ISO2 of country
     */
    public void setEmptyDefault(String iso) {
        if (iso == null || iso.isEmpty()) {
            iso = DEFAULT_COUNTRY;
        }

        System.out.println("ndndndndn 111 " + iso);
        int defaultIdx = mCountries.indexOfIso(iso);
        System.out.println("ndndndndn 222  " + iso);
        mSelectedCountry = mCountries.get(defaultIdx);
        mCountrySpinner.setSelection(defaultIdx);

    }

    /**
     * Alias for setting empty string of default settings from the device (using locale)
     */
    private void setEmptyDefault() {
        setEmptyDefault(null);
    }

    /**
     * Set hint number for country
     */
    private void setHint() {
        if (mPhoneEdit != null && mSelectedCountry != null && mSelectedCountry.getIso() != null) {
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.getExampleNumberForType(mSelectedCountry.getIso(), PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                //mPhoneEdit.setHint(mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
                mPhoneEdit.setHint("70 123 456 7");
            }
        }
    }

    /**
     * Spinner listener
     */
    private AdapterView.OnItemSelectedListener mCountrySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //mPhoneEdit.setText("");
            mSelectedCountry = mCountrySpinnerAdapter.getItem(position);
            mPhoneNumberWatcher = new ThreeDigitPhoneFormatWatcher();
            setHint();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Phone number watcher
     */



    /**
     * Set Number
     *
     * @param number E.164 format or national format
     */
    public void setNumber(String number) {
        System.out.println("checking number 111 " + number);
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                if (number.equals("+94")) {
                    iso = "LK";
                } else {
                    iso = mSelectedCountry.getIso();
                }

                System.out.println("checking number 2222 " + iso);
            }
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.parse(number, iso);
            System.out.println("checking number 3333 " + phoneNumber.toString());
            int countryIdx = mCountries.indexOfIso(mPhoneUtil.getRegionCodeForNumber(phoneNumber));
            System.out.println("checking number 4444 " + countryIdx);
            mSelectedCountry = mCountries.get(countryIdx);
            System.out.println("checking number 5555 " + mSelectedCountry);
            mCountrySpinner.setSelection(countryIdx);

            System.out.println("checking number 6666 " + mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
//            mPhoneEdit.setText(mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
            mPhoneEdit.setSelection(mPhoneEdit.getText().length());
        } catch (NumberParseException ignored) {
            System.out.println("checking number 7777 " + ignored);
            System.out.println("checking number 8888 " + ignored.toString());
        }
    }
    public void setNumberDataConnection(String number) {
        System.out.println("checking number 111 " + number);
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                if (number.equals("+94")) {
                    iso = "LK";
                } else {
                    iso = mSelectedCountry.getIso();
                }

                System.out.println("checking number 2222 " + iso);
            }
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.parse(number, iso);
            System.out.println("checking number 3333 " + phoneNumber.toString());
            int countryIdx = mCountries.indexOfIso(mPhoneUtil.getRegionCodeForNumber(phoneNumber));
            System.out.println("checking number 4444 " + countryIdx);
            mSelectedCountry = mCountries.get(countryIdx);
            System.out.println("checking number 5555 " + mSelectedCountry);
            mCountrySpinner.setSelection(countryIdx);

            System.out.println("checking number 6666 " + mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
            mPhoneEdit.setText(mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).substring(1));
            mPhoneEdit.setSelection(mPhoneEdit.getText().length());
        } catch (NumberParseException ignored) {
            System.out.println("checking number 7777 " + ignored);
            System.out.println("checking number 8888 " + ignored.toString());
        }
    }
    public void setNumberEdit(String number) {
        System.out.println("checking number 111 " + number);
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                if (number.equals("+94")) {
                    iso = "LK";
                } else {
                    iso = mSelectedCountry.getIso();
                }

                System.out.println("checking number 2222 " + iso);
            }
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.parse(number, iso);
            System.out.println("checking number 3333 " + phoneNumber.toString());
            int countryIdx = mCountries.indexOfIso(mPhoneUtil.getRegionCodeForNumber(phoneNumber));
            System.out.println("checking number 4444 " + countryIdx);
            mSelectedCountry = mCountries.get(countryIdx);
            System.out.println("checking number 5555 " + mSelectedCountry);
            mCountrySpinner.setSelection(countryIdx);
            String mobileNumber = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).substring(1);

            mPhoneEdit.setText(mobileNumber);
            mPhoneEdit.setSelection(mPhoneEdit.getText().length());
        } catch (NumberParseException ignored) {
        }
    }

    /**
     * Get number
     *
     * @return Phone number in E.164 format | null on error
     */
    @SuppressWarnings("unused")
    public String getNumber() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();

        if (phoneNumber == null) {
            return null;
        }

        return mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public String getText() {
        return getNumber();
    }

    /**
     * Get PhoneNumber object
     *
     * @return PhonenUmber | null on error
     */
    @SuppressWarnings("unused")
    public Phonenumber.PhoneNumber getPhoneNumber() {
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                iso = mSelectedCountry.getIso();
            }
            return mPhoneUtil.parse(mPhoneEdit.getText().toString(), iso);
        } catch (NumberParseException ignored) {
            return null;
        }
    }

    /**
     * Get selected country
     *
     * @return Country
     */
    @SuppressWarnings("unused")
    public Country getSelectedCountry() {
        return mSelectedCountry;
    }

    /**
     * Check if number is valid
     *
     * @return boolean
     */
    @SuppressWarnings("unused")
    public boolean isValid() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    /**
     * Add validation listener
     *
     * @param listener IntlPhoneInputListener
     */
    public void setOnValidityChange(IntlPhoneInputListener listener) {
        mIntlPhoneInputListener = listener;
    }


    /**
     * Simple validation listener
     */
    public interface IntlPhoneInputListener {
        void done(View view, boolean isValid);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mPhoneEdit.setEnabled(enabled);
        mCountrySpinner.setEnabled(enabled);
    }

    /**
     * Set keyboard done listener to detect when the user click "DONE" on his keyboard
     *
     * @param listener IntlPhoneInputListener
     */
    public void setOnKeyboardDone(final IntlPhoneInputListener listener) {
        mPhoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    listener.done(IntlPhoneInput.this, isValid());
                }
                return false;
            }
        });
    }
}
