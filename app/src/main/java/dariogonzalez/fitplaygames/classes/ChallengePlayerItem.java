package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

import com.parse.ParseUser;

/**
 * Created by Logan on 11/17/2015.
 */
public class ChallengePlayerItem {
    private int mIconId, mPasses, mStatus;
    private Uri mImageUri;
    private long mSteps;
    private boolean mIsOwner;
    private ParseUser mUserObject;
    private String mUserName;

    public int getmIconId() {
        return mIconId;
    }

    public void setmIconId(int mIconId) {
        this.mIconId = mIconId;
    }

    public int getmPasses() {
        return mPasses;
    }

    public void setmPasses(int mPasses) {
        this.mPasses = mPasses;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public Uri getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }

    public long getmSteps() {
        return mSteps;
    }

    public void setmSteps(long mSteps) {
        this.mSteps = mSteps;
    }

    public boolean ismIsOwner() {
        return mIsOwner;
    }

    public void setmIsOwner(boolean mIsOwner) {
        this.mIsOwner = mIsOwner;
    }

    public ParseUser getmUserObject() {
        return mUserObject;
    }

    public void setmUserObject(ParseUser mUserObject) {
        this.mUserObject = mUserObject;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }
}
