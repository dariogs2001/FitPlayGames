package dariogonzalez.fitplaygames.classes;

import android.net.Uri;

/**
 * Created by ChristensenKC on 11/9/2015.
 */
public class GamesListItem {
    private int mIconId;
    private String mChallengeTitle;
    private int mNumberOfPlayers;

    public GamesListItem() {
        mIconId = -1;
        mChallengeTitle = "";
        mNumberOfPlayers = 0;
    }


    public int getmIconId() {
        return mIconId;
    }

    public void setmIconId(int mIconId) {
        this.mIconId = mIconId;
    }

    public String getmChallengeTitle() {
        return mChallengeTitle;
    }

    public void setmChallengeTitle(String mChallengeTitle) {
        this.mChallengeTitle = mChallengeTitle;
    }

    public int getmNumberOfPlayers() {
        return mNumberOfPlayers;
    }

    public void setmNumberOfPlayers(int mNumberOfPlayers) {
        this.mNumberOfPlayers = mNumberOfPlayers;
    }

}
