package dariogonzalez.fitplaygames.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dgonzalez on 7/15/15.
 */
public class FitbitAccountInfo implements Parcelable {
    private String mAccessToken;
    private String mSecret;
    private String mUserId;

    protected FitbitAccountInfo(Parcel in) {
        mAccessToken = in.readString();
        mSecret = in.readString();
        mUserId = in.readString();
    }

    public static final Creator<FitbitAccountInfo> CREATOR = new Creator<FitbitAccountInfo>() {
        @Override
        public FitbitAccountInfo createFromParcel(Parcel in) {
            return new FitbitAccountInfo(in);
        }

        @Override
        public FitbitAccountInfo[] newArray(int size) {
            return new FitbitAccountInfo[size];
        }
    };

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(final String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(final String mSecret) {
        this.mSecret = mSecret;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(final String mUserId) {
        this.mUserId = mUserId;
    }

    public FitbitAccountInfo(final String mAccessToken, final String mSecret, final String mUserId) {
        this.mAccessToken = mAccessToken;
        this.mSecret = mSecret;
        this.mUserId = mUserId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mAccessToken);
        dest.writeString(mSecret);
        dest.writeString(mUserId);
    }
}
