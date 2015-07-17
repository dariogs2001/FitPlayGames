package dariogonzalez.fitplaygames.classes;

/**
 * Created by Dario on 6/3/2015.
 */
public class LeaderBoardListItem {
    private String mUserName;
    private String mSteps;
    private String mGamesPlayed;
    private int mIconId;

    public LeaderBoardListItem(String userName, String steps, String gamesPlayed, int iconId) {
        this.mUserName = userName;
        this.mSteps = steps;
        this.mGamesPlayed = gamesPlayed;
        this.mIconId = iconId;
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
}
