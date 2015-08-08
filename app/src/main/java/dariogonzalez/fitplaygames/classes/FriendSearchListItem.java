package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

/**
 * Created by Dario on 8/8/2015.
 */
public class FriendSearchListItem {
    private String mUserName;
    private int mIconId;
    private Uri mImageUri;

    public FriendSearchListItem(String userName, int iconId, Uri imageUri) {
        this.mUserName = userName;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
    }

    public String getUserName() { return mUserName; }
    public int getIconId() {
        return mIconId;
    }
    public Uri getImageUri() {return mImageUri; }
}
