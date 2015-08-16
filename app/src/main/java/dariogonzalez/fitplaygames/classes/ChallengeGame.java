package dariogonzalez.fitplaygames.classes;

/**
 * Created by Dario on 8/15/2015.
 */
public class ChallengeGame {
    String mChallengeName;
    int mPhotoId;

    public ChallengeGame(String challengeName, int photoId) {
        this.mChallengeName = challengeName;
        this.mPhotoId = photoId;
    }

    public String getChallengeName() {
        return mChallengeName;
    }

    public void setChallengeName(String mChallengeName) {
        this.mChallengeName = mChallengeName;
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        this.mPhotoId = photoId;
    }
}
