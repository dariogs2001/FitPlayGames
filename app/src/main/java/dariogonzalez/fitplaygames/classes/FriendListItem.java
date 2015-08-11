package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

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

    public FriendListItem(String userName, int iconId, Uri imageUri, String userId, String friendId) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
        this.mUserId = userId;
        this.mFriendId = friendId;
    }

    public FriendListItem(String userName, int iconId, Uri imageUri, String userId, String friendId, long steps) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
        this.mUserId = userId;
        this.mFriendId = friendId;
        this.mSteps = steps;
    }

    public String getUserName() { return mUserName; }
    public int getIconId() {
        return mIconId;
    }
    public Uri getImageUri() {return mImageUri; }

    public String getUserId() { return mUserId; }
    public String getFriendId() { return mFriendId; }
    public long getSteps() {return mSteps;}

}
