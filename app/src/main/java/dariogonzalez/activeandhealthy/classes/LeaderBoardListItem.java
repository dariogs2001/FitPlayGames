package dariogonzalez.activeandhealthy.classes;

/**
 * Created by Dario on 6/3/2015.
 */
public class LeaderBoardListItem {
    private String mSteps;
    private String mGamesPlayed;
    private int mIconId;

    public LeaderBoardListItem(String steps, String gamesPlayed, int iconId) {
        this.mSteps = steps;
        this.mGamesPlayed = gamesPlayed;
        this.mIconId = iconId;
    }

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
