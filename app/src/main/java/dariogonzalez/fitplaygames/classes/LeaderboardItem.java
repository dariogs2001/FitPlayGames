package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

import dariogonzalez.fitplaygames.R;

/**
 * Created by Logan on 2/10/2016.
 */
public class LeaderboardItem {

    private String mUsername;
    private int mNumOfGames,  mAvgTime;
    private Uri mImageUri;

    public LeaderboardItem(String username, int numOfGames, int avgTime, Uri imageUri) {
        this.mUsername = username;
        this.mNumOfGames = numOfGames;
        this.mAvgTime = avgTime;
        this.mImageUri = imageUri;
    }

    public int getmIconId() {
        return R.drawable.ic_account_circle_white_24px;
    }

    public String getmUsername() {
        return mUsername;
    }

    public int getmNumOfGames() {
        return mNumOfGames;
    }

    public int getmAvgTime() {
        return mAvgTime;
    }

    public Uri getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }

}
