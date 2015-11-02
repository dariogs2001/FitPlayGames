package dariogonzalez.fitplaygames.classes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import dariogonzalez.fitplaygames.InviteFriendsActivity;

/**
 * Created by Dario on 8/15/2015.
 */
public abstract class ParentChallenge {
    private static String TAG = ParentChallenge.class.getSimpleName();
    private ParseObject challengeObject; // The parseObject from the DB of the challenge
    private int challengeType; // This comes from the challenge type constants class
    private String name; // Name of the challenge
    private String userChallengeName; // Name that the user gives the challenge
    private String description; // The challenge description
    private int stepsGoal; // Step goal for the challenge
    private int challengeStatusType; // This is in the parse constants class
    private Drawable icon; // The challenge icon
    private Date startDate; // The user set startDate/time
    private Date endDate; // The system set endDate/time
    private ArrayList<String> playerIds; // A list of all the player ids of the challenge. This should be > 2 for challenge to start
    private ArrayList<String> activePlayers; // A list of all the active player ids of the challenge. This could be 1 or more
    private String startChallengeMessage; // A push message to be sent when a challenge starts
    private String endChallengeMessage; // A push message to be sent when a challenge ends
    private String inviteChallengeMessage; // A push message to be sent when a user gets invited to a challenge
    private String mainPushMessage = ""; // This is the message that will be sent as a push notification

    public void initialize() {
        playerIds = new ArrayList<>();
        activePlayers = new ArrayList<>();
    }

    public ParentChallenge() {}

    // This method will return a default challenge name based on the name of the challenge and possibly the date or something
    public String getDefaultChallengeName() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Date date = new Date();
        String dateStr = sdf.format(date);

        String challengeName = ChallengeTypeConstants.getChallengeName(challengeType);

        return challengeName + " " + dateStr;
    }

    // This method will do the sending of push notifications to the other players (playerIds)
    // This could be for inviting, starting, ending etc.
    public void sendPushNotification(/*String challengeId, String message*/ParseUser user) {
// Associate the device with a user
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", user);
        //installation.put(ParseConstants.CHALLENGE_ID,challengeId);
        installation.saveInBackground();

// Create our Installation query
        ParseQuery pushQuery = ParseInstallation.getQuery();
        //pushQuery.whereEqualTo("challengeId", challengeId);
        pushQuery.whereEqualTo("user", user);

// Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("This is my test message string to show push notifications working!");
        push.sendInBackground(new SendCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("push", "success!");
                } else {
                    Log.d("push", "failure");
                }
            }
        });
    }

    public void createChallenge(final String userId, String challengeName, int stepsGoal, Date startDate, Date endDate) {
        this.userChallengeName = challengeName;
        this.stepsGoal = stepsGoal;
        this.startDate = startDate;
        this.endDate = endDate;

        challengeObject = new ParseObject(ParseConstants.CLASS_CHALLENGES);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_NAME, challengeName);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_TYPE, challengeType);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PROCESSING);
        challengeObject.put(ParseConstants.CHALLENGE_DAILY_STEPS_GOAL, stepsGoal);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_START, startDate);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_END, endDate);

        challengeObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject challengePlayer = new ParseObject(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_USER_ID, userId);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID, challengeObject.getObjectId());
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_OWNER, true);
                    challengePlayer.saveInBackground();

                    createChallengePlayers(challengeObject.getObjectId());
                } else {
                    //TODO: show error message
                }
            }
        });
    }

    public void createChallengePlayers(String challengeId) {
        // Create a new challenge player object for each player id

        int size = playerIds.size();
        for (int i = 0; i < size; i++) {
            final ParseObject challengePlayer = new ParseObject(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_USER_ID, playerIds.get(i));
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID, challengeId);
            challengePlayer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        sendInvitation(challengePlayer.getObjectId());
                    }
                }
            });
        }
    }

    // This method will change the challenge status to be "started"
    public void startChallenge() {
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PLAYING);
        challengeObject.saveInBackground();
    }

    // This method will change the challenge status to be "ended" and will be called if the user cancels a challenge as well or if there are not more than one players
    public void endChallenge() {
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_FINISHED);
        challengeObject.saveInBackground();
    }

    // This method will have the logic needed to send invitations and will be calling sendPushNotification
    public void sendInvitation(String playerId) {
        startChallengeMessage = "[ChallengeName] has started";
        endChallengeMessage = "[ChallengeName] has ended";
        inviteChallengeMessage = "[username] has invited you to play to play " + ChallengeTypeConstants.getChallengeName(getChallengeType());
        /*if(starting challenge)
           mainPushMessage = startChallengeMessage; */
        /* if(inviting challenge )
            mainPushMessage = endChallengeMessage; */
        /* if(ending challenge)
            mainPushMessage = inviteChallengeMessage; */
//        sendPushNotification(/*playerId, mainPushMessage*/);
    }

    // To be called by children classes
    // This method will do the logic to update the challenge per user and will be called from checkPlayerStatus
    protected void updateChallenge() {}


    // This method will check the challenge status and the players status when they open up the challenge. It should be called for every challenge that the user is involved with.
    // It will hold all the logic to know what it needs to do every time a user opens up the app.
    protected void checkPlayerStatus() {}

    public int getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(int challengeType) {
        this.challengeType = challengeType;
    }

    public int getChallengeStatusType() {
        return challengeStatusType;
    }

    public void setChallengeStatusType(int challengeStatusType) {
        this.challengeStatusType = challengeStatusType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserChallengeName() {
        return userChallengeName;
    }

    public void setUserChallengeName(String userChallengeName) {
        this.userChallengeName = userChallengeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStepsGoal() {
        return stepsGoal;
    }

    public void setStepsGoal(int stepsGoal) {
        this.stepsGoal = stepsGoal;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<String> playerIds) {
        this.playerIds = playerIds;
    }

    public ArrayList<String> getActivePlayers() {
        return activePlayers;
    }

    public void setActivePlayers(ArrayList<String> activePlayers) {
        this.activePlayers = activePlayers;
    }
}
