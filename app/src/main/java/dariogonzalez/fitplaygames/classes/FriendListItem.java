package dariogonzalez.fitplaygames.classes;

/**
 * Created by Dario on 6/6/2015.
 */
public class FriendListItem {
    private String mSteps;
    private String mUsername;

    public String getSteps() {
        return mSteps;
    }

    public void setSteps(String mSteps) {
        this.mSteps = mSteps;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public FriendListItem(String mSteps, String mUsername) {
        this.mSteps = mSteps;
        this.mUsername = mUsername;
    }
}
