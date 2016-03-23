package dariogonzalez.fitplaygames.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Logan on 10/21/2015.
 * Hot Potato Challenge class
 */
public class HotPotatoChallenge extends ParentChallenge implements Parcelable {

    private int totalSteps, totalPasses;
    private Map<Integer, Integer> hours = new HashMap<>();
    private Map<Integer, HashMap<Integer, Integer>> passes = new HashMap<>();

    private static final long HOUR_IN_MILLI = 3600000;

    public HotPotatoChallenge(int type) {
        setChallengeType(type);
        createEndDateMapping();
        initialize();

        totalSteps = 0;
        totalPasses = 0;
    }

    public void createEndDateMapping() {
        // Initialize hours map
        // This is a relationship of stepGoal to hours
        hours.put(100, 2);
        hours.put(1000, 2);
        hours.put(2000, 4);
        hours.put(3000, 6);
        hours.put(4000, 8);
        hours.put(5000, 12);

        // Initialize passes map
        // This is a relationship of people to # of passes for the game
        HashMap<Integer, Integer> twoPeople = new HashMap<>();
        twoPeople.put(100, 24);
        twoPeople.put(1000, 24);
        twoPeople.put(2000, 12);
        twoPeople.put(3000, 8);
        twoPeople.put(4000, 6);
        twoPeople.put(5000, 4);
        passes.put(2, twoPeople);

        HashMap<Integer, Integer> threePeople = new HashMap<>();
        threePeople.put(100, 12);
        threePeople.put(1000, 12);
        threePeople.put(2000, 9);
        threePeople.put(3000, 7);
        threePeople.put(4000, 6);
        threePeople.put(5000, 6);
        passes.put(3, threePeople);

        HashMap<Integer, Integer> fourPeople = new HashMap<>();
        fourPeople.put(100, 12);
        fourPeople.put(1000, 12);
        fourPeople.put(2000, 8);
        fourPeople.put(3000, 8);
        fourPeople.put(4000, 4);
        fourPeople.put(5000, 4);
        passes.put(4, fourPeople);

        HashMap<Integer, Integer> fivePeople = new HashMap<>();
        fivePeople.put(100, 15);
        fivePeople.put(1000, 15);
        fivePeople.put(2000, 10);
        fivePeople.put(3000, 10);
        fivePeople.put(4000, 5);
        fivePeople.put(5000, 5);
        passes.put(5, fivePeople);
    }

    protected HotPotatoChallenge(Parcel source) {
        this.setUserChallengeName(source.readString());
        this.setStartDate(new Date(source.readLong()));
        this.setEndDate(new Date(source.readLong()));
        this.setStepsGoal(source.readInt());
        this.setTotalPasses(source.readInt());
        this.setTotalSteps(source.readInt());
        this.setChallengeId(source.readString());
        this.setChallengeStatusType(source.readInt());
    }

    public Date generateRandomEndDate(int stepsGoal, int numOfPlayers, Date startDate) {

        int hourAmt = hours.get(stepsGoal);
        int passesAmt = passes.get(numOfPlayers).get(stepsGoal);

        long hoursInMilli = hourAmt * HOUR_IN_MILLI;

        long timeToAddInMilli = hoursInMilli * passesAmt;
        long today = startDate.getTime();

        // Randomness
        Date date = new Date(timeToAddInMilli + today);
        return date;
    }

    public static void findLoser(final ParseObject challenge) {
        ParseQuery<ParseObject> challengeEventQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_EVENTS);
        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
        challengeEventQuery.include(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT);
        challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null  && !list.isEmpty()) {
                    ParseObject challengeEvent = list.get(0);

                    Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                    Date endTime = new Date();
                    //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
                    long timeDifference = endTime.getTime() - startTime.getTime() / 1000 * 60;
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
                    challengeEvent.saveInBackground();

                    ParseObject challengePlayer = challengeEvent.getParseObject(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_LOSER, true);
                    challengePlayer.saveInBackground();
                    ParseUser loserPlayerUser = (ParseUser) challengePlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                    ParentChallenge.sendPushNotification("Oh no! The potato has exploded in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", loserPlayerUser);
                }
            }
        });
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
        parcel.writeString(this.getChallengeId());
        parcel.writeInt(this.getChallengeStatusType());
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

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        String timeStr = simpleTimeFormat.format(date);

        keyVal.put("date", dateStr);
        keyVal.put("time", timeStr);

        return keyVal;
    }

    public void refresh() {
        ParentChallenge.updateChallenge(getChallengeObject(), getChallengePlayer());
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
