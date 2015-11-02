package dariogonzalez.fitplaygames.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Logan on 10/21/2015.
 */
public class HotPotatoChallenge extends ParentChallenge implements Parcelable{

    private int totalSteps, totalPasses;

    public HotPotatoChallenge(int type) {
        setChallengeType(type);
        initialize();

        totalSteps = 0;
        totalPasses = 0;
    }

    protected HotPotatoChallenge(Parcel source) {
        this.setUserChallengeName(source.readString());
        this.setStartDate(new Date(source.readLong()));
        this.setEndDate(new Date(source.readLong()));
        this.setStepsGoal(source.readInt());
        this.setTotalPasses(source.readInt());
        this.setTotalSteps(source.readInt());
    }

    public Date generateRandomEndDate(int stepsGoal, int numOfPlayers) {
        Date date = new Date(1446006440060L);
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getUserChallengeName());
        parcel.writeLong(this.getStartDate().getTime());
        parcel.writeLong(this.getEndDate().getTime());
        parcel.writeInt(this.getStepsGoal());
        parcel.writeInt(this.getTotalPasses());
        parcel.writeInt(this.getTotalSteps());
    }

    public static final Creator<HotPotatoChallenge> CREATOR = new Creator<HotPotatoChallenge>() {
        @Override
        public HotPotatoChallenge createFromParcel(Parcel in) {
            return new HotPotatoChallenge(in);
        }

        @Override
        public HotPotatoChallenge[] newArray(int size) {
            return new HotPotatoChallenge[size];
        }
    };

    public Map splitDateAndTime(Date date) {
        Map<String, String> keyVal = new HashMap<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateStr = simpleDateFormat.format(date);

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm");
        String timeStr = simpleTimeFormat.format(date);

        keyVal.put("date", dateStr);
        keyVal.put("time", timeStr);

        return keyVal;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getTotalSteps() {
        return this.totalSteps;
    }

    public void setTotalPasses(int totalPasses) {
        this.totalPasses = totalPasses;
    }

    public int getTotalPasses() {
        return this.totalPasses;
    }
}
