package dariogonzalez.fitplaygames.classes;

/**
 * Created by Logan on 10/7/2015.
 * This class is used to store the game types that are used in the parse column for games
 */
public class ChallengeTypeConstants {
    private static final int HOT_POTATO = 0;


    public static String getChallengeName(int gameType) {
        switch (gameType) {
            case HOT_POTATO:
                return "Hot Potato";
            default:
                return "";
        }
    }
}
