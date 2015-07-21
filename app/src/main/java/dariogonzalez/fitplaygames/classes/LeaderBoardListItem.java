package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

/**
 * Created by Dario on 6/3/2015.
 */
public class LeaderBoardListItem {
    private String mUserName;
    private String mSteps;
    private String mGamesPlayed;
    private int mIconId;
    private Uri mImageUri;

    public LeaderBoardListItem(String userName, String steps, String gamesPlayed, int iconId, Uri imageUri) {
        this.mUserName = userName;
        this.mSteps = steps;
        this.mGamesPlayed = gamesPlayed;
        this.mIconId = iconId;
        this.mImageUri = imageUri;
    }

    public String getUserName() { return mUserName; }
    public String getSteps() {
        return mSteps;
    }
    public String getGamesPlayed() {
        return mGamesPlayed;
    }
    public int getIconId() {
        return mIconId;
    }
    public Uri getImageUri() {return mImageUri; }
}
