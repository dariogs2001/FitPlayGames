package dariogonzalez.fitplaygames.classes;

import java.util.Date;

/**
 * Created by Logan on 10/21/2015.
 */
public class HotPotatoChallenge extends ParentChallenge {

    public HotPotatoChallenge(int type) {
        setChallengeType(type);
        initialize();
    }

    public Date generateRandomEndDate(int stepsGoal, int numOfPlayers) {
        Date date = new Date(1446006440060L);
        return date;
    }
}
