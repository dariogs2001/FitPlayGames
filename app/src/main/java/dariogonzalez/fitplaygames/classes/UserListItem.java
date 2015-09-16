package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

import com.parse.ParseUser;

/**
 * Created by Dario on 8/8/2015.
 */
public class UserListItem {
    private int mIconId;
    private Uri mImageUri;
    private long mSteps;
    private int mGamesPlayed;

    private ParseUser mUserObject;
    private ParseUser mFriendObject;

    private int mFriendStatusId;
    private String mUserFriendId;

    public UserListItem() {
        mIconId = -1;
        mImageUri = null;
        mSteps = 0;
        mGamesPlayed = 0;
        mFriendStatusId = 0;
        mUserFriendId = "";
        mUserObject = null;
        mFriendObject = null;
    }


    public int getmIconId() {
        return mIconId;
    }

    public void setmIconId(int mIconId) {
        this.mIconId = mIconId;
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

    public int getmGamesPlayed() {
        return mGamesPlayed;
    }

    public void setmGamesPlayed(int mGamesPlayed) {
        this.mGamesPlayed = mGamesPlayed;
    }

    public ParseUser getmUserObject() {
        return mUserObject;
    }

    public void setmUserObject(ParseUser mUserObject) {
        this.mUserObject = mUserObject;
    }

    public ParseUser getmFriendObject() {
        return mFriendObject;
    }

    public void setmFriendObject(ParseUser mFriendObject) {
        this.mFriendObject = mFriendObject;
    }

    public int getmFriendStatusId() {
        return mFriendStatusId;
    }

    public void setmFriendStatusId(int mFriendStatusId) {
        this.mFriendStatusId = mFriendStatusId;
    }

    public String getmUserFriendId() {
        return mUserFriendId;
    }

    public void setmUserFriendId(String mUserFriendId) {
        this.mUserFriendId = mUserFriendId;
    }
}