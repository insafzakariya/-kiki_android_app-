package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lk.mobilevisions.kiki.modules.auth.AuthOptions;

public class AuthUser {

    public enum Carrier {
        DIALOG, MOBITEL, OTHER
    }

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("verified")
    @Expose
    private boolean mobileNumberVerified;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("viewer_type")
    @Expose
    private String viewerType;
    @SerializedName("country")
    @Expose
    private String countryCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @param accessToken
     * The acess_token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     *
     * @return
     * The dateOfBirth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     *
     * @param dateOfBirth
     * The dateOfBirth
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     *
     * @return
     * The gender
     */
    public String getGender() {
        if("M".equals(gender.toUpperCase())){
            gender = AuthOptions.Gender.MALE.toString().toUpperCase();
        }
        if("F".equals(gender.toUpperCase())){
            gender = AuthOptions.Gender.FEMALE.toString().toUpperCase();
        }
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        if("SI".equals(language.toUpperCase())){
            language = AuthOptions.Language.SINHALA.toString().toUpperCase();
        }
        if("TA".equals(language.toUpperCase())){
            language = AuthOptions.Language.TAMIL.toString().toUpperCase();
        }
        if("EN".equals(language.toUpperCase())){
            language = AuthOptions.Language.ENGLISH.toString().toUpperCase();
        }
        return language;
    }

    public Carrier getCarrier() {
        if (mobileNumber!= null) {
            if (mobileNumber.startsWith("+9477") || mobileNumber.startsWith("+9476")) {
                return Carrier.DIALOG;
            } else if (mobileNumber.startsWith("+9471") || mobileNumber.startsWith("+9470")) {
                return Carrier.MOBITEL;
            }
        }
        return Carrier.OTHER;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     *
     * @param mobileNumber
     * The mobile_number
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    /**
     *
     * @return
     * The mobileNumber
     */
    public boolean isMobileNumberVerified() {
        return mobileNumberVerified;
    }

    /**
     *
     * @param mobileNumberVerified
     * The mobile_number
     */
    public void setMobileNumberVerified(boolean mobileNumberVerified) {
        this.mobileNumberVerified = mobileNumberVerified;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getViewerType() {
        return viewerType;
    }

    public void setViewerType(String viewerType) {
        this.viewerType = viewerType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}