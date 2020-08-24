package lk.mobilevisions.kiki.modules.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * Created by Chatura Dilan Perera on 8/5/2017.
 */

public class HeaderResponse {


    public enum Carrier {
        DIALOG, MOBITEL, OTHER
    }

    @SerializedName("mobno")
    @Expose
    private String phoneNumber;

    private boolean mobileDataConnection;

    private boolean whiteListed;

    public Carrier getCarrier() {
        if (getPhoneNumber()!= null) {
            if (getPhoneNumber().startsWith("+9477") || getPhoneNumber().startsWith("+9476")) {
                return Carrier.DIALOG;
            } else if (getPhoneNumber().startsWith("+9471") || getPhoneNumber().startsWith("+9470")) {
                return Carrier.MOBITEL;
            }
        }

        return Carrier.OTHER;
    }

    public String getPhoneNumber() {

        if (phoneNumber != null) {
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+" + phoneNumber;
            }
            return phoneNumber;
        } else {
            return null;
        }
    }

    public boolean isMobileDataConnection() {
        return mobileDataConnection;
    }

    public void setMobileDataConnection(boolean mobileDataConnection) {
        Timber.d("Is mobile connection : " +  mobileDataConnection);
        this.mobileDataConnection = mobileDataConnection;
    }

    public boolean isWhiteListed() {
        if(phoneNumber != null) {
            return true;
        }
        return false;
    }

    public void setWhiteListed(boolean whiteListed) {
        this.whiteListed = whiteListed;
    }
}
