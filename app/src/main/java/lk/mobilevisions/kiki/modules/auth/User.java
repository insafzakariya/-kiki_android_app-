/**
 * Created by Chatura Dilan Perera on 27/8/2016.
 */
package lk.mobilevisions.kiki.modules.auth;

import lk.mobilevisions.kiki.app.Session;

public class User {

    private String username;
    private String password;
    private String name;
    private String dateOfBirth;
    private AuthOptions.AuthMethod provider;
    private AuthOptions.Gender gender;
    private AuthOptions.Language language;
    private String accessToken;
    private String socialAccessToken;
    private String socialAccessTokenSecret;
    private String mobileNumber;
    private String type;
    private String viewerType;
    private String countryCode;
    private Session session;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public AuthOptions.AuthMethod getProvider() {
        return provider;
    }

    public void setProvider(AuthOptions.AuthMethod provider) {
        this.provider = provider;
    }

    public AuthOptions.Gender getGender() {
        return gender;
    }

    public void setGender(AuthOptions.Gender gender) {
        this.gender = gender;
    }

    public AuthOptions.Language getLanguage() {
        return language;
    }

    public void setLanguage(AuthOptions.Language language) {
        this.language = language;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }

    public String getSocialAccessTokenSecret() {
        return socialAccessTokenSecret;
    }

    public void setSocialAccessTokenSecret(String socialAccessTokenSecret) {
        this.socialAccessTokenSecret = socialAccessTokenSecret;
    }

    public String getSocialAccessToken() {
        return socialAccessToken;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
