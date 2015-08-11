package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

/**
 * Created by Dario on 8/8/2015.
 */
public class FriendSearchListItem {
    private String mUserName;
    private int mIconId;
    private Uri mImageUri;

    private String mUserId;
    private String mFriendId;

    public FriendSearchListItem(String userName, int iconId, Uri imageUri, String userId, String friendId) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
        this.mUserId = userId;
        this.mFriendId = friendId;
    }

    public String getUserName() { return mUserName; }
    public int getIconId() {
        return mIconId;
    }
    public Uri getImageUri() {return mImageUri; }

    public String getUserId() { return mUserId; }
    public String getFriendId() { return mFriendId; }

}
