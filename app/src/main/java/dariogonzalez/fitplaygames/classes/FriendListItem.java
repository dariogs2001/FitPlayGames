package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

import com.parse.ParseUser;

/**
 * Created by Dario on 8/8/2015.
 */
public class FriendListItem {
    private String mUserName;
    private int mIconId;
    private Uri mImageUri;
    private long mSteps;

    private String mUserId;
    private String mFriendId;

    private ParseUser mUserObject;
    private ParseUser mFriendObject;

    private int mFriendStatusId;

    public FriendListItem(String userName, int iconId, Uri imageUri, String userId, String friendId, ParseUser userObject, ParseUser friendObject, int friendStatusId) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
        this.mUserId = userId;
        this.mFriendId = friendId;
        this.mUserObject = userObject;
        this.mFriendObject = friendObject;
        this.mFriendStatusId = friendStatusId;
    }

    public FriendListItem(String userName, int iconId, Uri imageUri, String userId, String friendId, long steps, ParseUser userObject, ParseUser friendObject, int mFriendStatusId) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
        this.mUserId = userId;
        this.mFriendId = friendId;
        this.mSteps = steps;
        this.mUserObject = userObject;
        this.mFriendObject = friendObject;
        this.mFriendStatusId = mFriendStatusId;
    }

    public String getUserName() {
        return mUserName;
    }

    public int getIconId() {
        return mIconId;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getFriendId() {
        return mFriendId;
    }

    public long getSteps() {
        return mSteps;
    }

    public ParseUser getUserObject () {
        return mUserObject;
    }

    public ParseUser getFriendObject () {
        return mFriendObject;
    }

    public int getFriendStatusId() { return mFriendStatusId; }

    public void setFriendStatusId(int friendStatusId) { this.mFriendStatusId = friendStatusId; }
}
