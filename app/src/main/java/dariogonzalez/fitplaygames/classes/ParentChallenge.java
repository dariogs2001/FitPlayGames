package dariogonzalez.fitplaygames.classes;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import dariogonzalez.fitplaygames.FitPlayGamesApplication;

/**
 * Created by Dario on 8/15/2015.
 */
public abstract class ParentChallenge {
    private static String TAG = ParentChallenge.class.getSimpleName();
    private ParseObject challengeObject, challengePlayer; // The parseObject from the DB of the challenge
    private String challengeId;
    private int challengeType; // This comes from the challenge type constants class
    private String name; // Name of the challenge
    private String userChallengeName; // Name that the user gives the challenge
    private String description; // The challenge description
    private int stepsGoal; // Step goal for the challenge
    private int challengeStatusType; // This is in the parse constants class
    private Drawable icon; // The challenge icon
    private Date startDate; // The user set startDate/time
    private Date endDate; // The system set endDate/time
    private ArrayList<ParseUser> playerObjects; // A list of all the player ids of the challenge. This should be > 2 for challenge to start
    private ArrayList<String> activePlayers; // A list of all the active player ids of the challenge. This could be 1 or more
    private String startChallengeMessage; // A push message to be sent when a challenge starts
    private String endChallengeMessage; // A push message to be sent when a challenge ends
    private String inviteChallengeMessage; // A push message to be sent when a user gets invited to a challenge
    private String mainPushMessage = ""; // This is the message that will be sent as a push notification
    private int numberOfPlayers = 1; //Total number of players when the game is created.

    public void initialize() {
        playerObjects = new ArrayList<>();
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
    public static void sendPushNotification(String message, ParseUser user) {
        FitPlayGamesApplication.sendPushNotification(message, user);
    }

    public void createChallenge(final ParseUser user, String challengeName, int stepsGoal, Date startDate, Date endDate, final GetObjectIdCallback callback, int numberOfPlayers) {
        this.userChallengeName = challengeName;
        this.stepsGoal = stepsGoal;
        this.startDate = startDate;
        this.endDate = endDate;

        challengeObject = new ParseObject(ParseConstants.CLASS_CHALLENGES);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_NAME, challengeName);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_TYPE, challengeType);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PENDING);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL, stepsGoal);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_START, startDate);
        challengeObject.put(ParseConstants.CHALLENGE_CHALLENGE_END, endDate);
        challengeObject.put(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS, numberOfPlayers);

        challengeObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    setChallengeId(challengeObject.getObjectId());
                    callback.done(challengeObject.getObjectId());
                    ParseObject challengePlayer = new ParseObject(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, user);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challengeObject);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_OWNER, true);
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_DATE_JOINED, new Date());
                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_PASSES, 0);
                    challengePlayer.saveInBackground();

                    createChallengePlayers(user.getString(ParseConstants.USER_USERNAME));
                }
            }
        });
    }

    public void createChallengePlayers(final String ownerUsername) {
        // Create a new challenge player object for each player id
        for (ParseUser player : playerObjects) {
            final ParseObject challengePlayer = new ParseObject(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, player);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challengeObject);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_OWNER, false);
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_DATE_JOINED, new Date());
            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_PASSES, 0);

            challengePlayer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        sendInvitation(challengePlayer, ownerUsername);
                    }
                }
            });
        }
    }

    // This method will have the logic needed to send invitations and will be calling sendPushNotification
    public void sendInvitation(ParseObject challengePlayer, String invitersUsername) {
        inviteChallengeMessage = invitersUsername + " has invited you to play " + ChallengeTypeConstants.getChallengeName(getChallengeType());
        ParseUser user = challengePlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
        Log.d("TEST", user.getUsername());
        sendPushNotification(inviteChallengeMessage, user);
    }

    /**
     * Update challenges
     *
     */
    public static void updateChallenges() {
        // Grab all the ChallengePlayers objects for the current user where the status is accepted
        ParseQuery<ParseObject> challengePlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
//        challengePlayerQuery.whereNotEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());
        //challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
        challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> challengePlayers, ParseException e) {
                if (e == null) {
                    //Loop through all the the Objects, get the challenge and update only the ones PLAYING OR PENDING
                    for (final ParseObject challengePlayer : challengePlayers) {
                        try {
                            ParseObject challenge = challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();
                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PLAYING ||
                                    challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PENDING) {
                                updateChallenge(challenge, challengePlayer);
                            }
                        } catch (Exception ex) {

                        }

//                        ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
//                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).getObjectId());
//                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PLAYING);
//                        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
//                            @Override
//                            public void done(List<ParseObject> challenges, ParseException e) {
//                                if (e == null) {
//                                    for (ParseObject challenge : challenges) {
//                                        updateChallenge(challenge, challengePlayer);
//                                    }
//                                }
//                            }
//                        });
                    }
                }
            }
        });
    }

    public static void updateChallenge(final ParseObject challenge, final ParseObject challengePlayer) {
        Log.d("TEST", "You got into ParentChallenge.updateChallenge successfully");
        // First, check to see what the status of the challenge is. If it hasn't started, check to see if it needs to start
        int challengeStatus = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS);
        int numOfPlayers = challenge.getInt(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS);
        Date today = new Date();
        Log.d("TODAY", today.toString());

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING && numOfPlayers > 1) {
            Log.d("TEST", "PENDING");
            Date startDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_START);
            if (today.after(startDate)) {
                challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PLAYING);
                challenge.saveInBackground();

                //Not need to implement this method in each class, both class work the same...
                chooseStartingPlayer(challenge);
            }
        }
        // Then, check to see if it needs to end
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {
            Log.d("TEST", "PLAYING");
            Date endDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_END);
            if (today.after(endDate)) {
                challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_FINISHED);
                challenge.saveInBackground();
                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                    HotPotatoChallenge.findLoser(challenge);
                }
                else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                    CaptureTheCrownChallenge.findWinner(challenge);
                }
            }
            else {
                Log.d("TEST", "Before end date");
                final int stepsGoal = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL);
                // If current user has an active "turn", check steps and see if they should pass it
                ParseQuery<ParseObject> challengeEventQuery = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, challengePlayer);
                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            Log.d("TEST", "Challenge Event");
                            if (list.size() > 0) {
                                final ParseObject challengeEvent = list.get(0);
                                Log.d("TEST", "Challenge event inside");
                                // Then, update the steps for this user, see if they have finished their "turn" and update the challenge event table
                                Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                ParseQuery<ParseObject> activityStepsQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_ACTIVITY_STEPS_15_MIN);
                                // Where userId and where date >= startTime
                                activityStepsQuery.whereEqualTo(ParseConstants.ACTIVITY_STEPS_USER_ID, ParseUser.getCurrentUser().getObjectId());
                                Log.d("TEST", "Start time: " + startTime.toString());
                                activityStepsQuery.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_STEPS_DATE, startTime);
                                activityStepsQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            int size = list.size();
                                            Log.d("TEST", "size: " + size);
                                            int stepsAmount = 0;
                                            for (int i = 0; i < size; i++) {

                                                //Adding first to be sure we get the last updated value,
                                                ParseObject data = list.get(i);
                                                int steps = data.getInt(ParseConstants.ACTIVITY_STEPS_STEPS);
                                                stepsAmount += steps;

                                                // If the added up steps are greater than the steps goal, set challenge event status to done and then get a new player
                                                if (stepsAmount >= stepsGoal) {
                                                    Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                                    Date endTime = new Date();
                                                    //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
                                                    long timeDifference = (endTime.getTime() - startTime.getTime()) / (1000 * 60);

                                                    //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
                                                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                                                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
                                                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
                                                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
                                                    challengeEvent.saveInBackground();

                                                    //Updating ChallengePlayer table with new values
                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
                                                    int playerPasses = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                                    playerPasses++;
                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_PASSES, playerPasses);

                                                    long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
                                                    gameTime += timeDifference;
                                                    long avgTime = gameTime / playerPasses;

                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);

                                                    challengePlayer.saveInBackground();

                                                    chooseNextPlayerHotPotato(challenge, challengePlayer);
                                                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                                                        sendPushNotification("Congrats! You just passed the potato in '"+challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME)+"'!", ParseUser.getCurrentUser());
                                                    }
                                                    else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                                                        sendPushNotification("You have lost the crown in '"+challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME)+"'!", ParseUser.getCurrentUser());
                                                    }
                                                    break;
                                                }
                                            }
                                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
                                            challengeEvent.saveInBackground();
                                        }
                                        else {
                                            Log.d("TEST", e.toString());
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            Log.d("TEST", e.getMessage());
                        }
                    }
                });
            }
        }
    }

    /**
     * Method used to choose next player in the HotPotato game.
     * At this point I already have set the previous status for the previous player, and I am ready to set everything for the new player.
     * @param challenge - object from Parse.com
     * @param challengePlayer - object from Parse.com
     */
    public static void chooseNextPlayerHotPotato(final ParseObject challenge, final ParseObject challengePlayer) {

        ParseQuery<ParseObject> nextPlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
        nextPlayerQuery.whereNotEqualTo(ParseConstants.OBJECT_ID, challengePlayer.getObjectId());
        nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);

        final int passes = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
        nextPlayerQuery.whereLessThanOrEqualTo(ParseConstants.CHALLENGE_PLAYER_PASSES, passes);
        nextPlayerQuery.orderByAscending(ParseConstants.CHALLENGE_PLAYER_PASSES);

        nextPlayerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    ParseObject nextPlayer = list.get(0);
                    nextPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                    nextPlayer.saveInBackground();
                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE, challenge);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, nextPlayer);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                    challengeEvent.saveInBackground();

                    //Sending push notification...
                    ParseUser nextPlayerUser = (ParseUser) nextPlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                    String object = "";
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        object = "potato";
                    }
                    else if ( challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                        object = "crown";
                    }
                    ParentChallenge.sendPushNotification("You have been passed the " + object + " in game '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", nextPlayerUser);
                }
            }
        });
    }

    /**
     * Method used to choose next player in the Capture The Crown game.
     * @param challenge - object from Parse.com
     * @param challengePlayer - object from Parse.com
     */
    public static void chooseNextPlayerCaptureTheCrown(final ParseObject challenge, final ParseObject challengePlayer) {

        ParseQuery<ParseObject> nextPlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
        nextPlayerQuery.whereNotEqualTo(ParseConstants.OBJECT_ID, challengePlayer.getObjectId());
        nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);

        final int passes = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
        nextPlayerQuery.whereLessThanOrEqualTo(ParseConstants.CHALLENGE_PLAYER_PASSES, passes);
        nextPlayerQuery.orderByAscending(ParseConstants.CHALLENGE_PLAYER_PASSES);

        nextPlayerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    ParseObject nextPlayer = list.get(0);
                    nextPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                    nextPlayer.saveInBackground();
                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE, challenge);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, nextPlayer);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                    challengeEvent.saveInBackground();

                    //Sending push notification...
                    ParseUser nextPlayerUser = (ParseUser) nextPlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                    String object = "";
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        object = "potato";
                    }
                    else if ( challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                        object = "crown";
                    }
                    ParentChallenge.sendPushNotification("You have been passed the " + object + " in game '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", nextPlayerUser);
                }
            }
        });
    }



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

    public ArrayList<ParseUser> getPlayerObjects() {
        return playerObjects;
    }

    public void setPlayerObjects(ArrayList<ParseUser> playerObjects) {
        this.playerObjects = playerObjects;
    }

    public ArrayList<String> getActivePlayers() {
        return activePlayers;
    }

    public void setActivePlayers(ArrayList<String> activePlayers) {
        this.activePlayers = activePlayers;
    }

    public ParseObject getChallengeObject() {
        return challengeObject;
    }

    public void setChallengeObject(ParseObject challengeObject) {
        this.challengeObject = challengeObject;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public ParseObject getChallengePlayer() { return this.challengePlayer; }

    public void setChallengePlayer(ParseObject challengePlayer) {
        this.challengePlayer = challengePlayer;
    }

    public interface GetObjectIdCallback {
        void done(String objectId);
    }

    public int getNumberOfPlayers()
    {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numOfPlayers)
    {
        numberOfPlayers = numOfPlayers;
    }

    public void updateChallengeStatusInDatabase(final String challengeId, final int challengeStatus)
    {
        ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengeId);
        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null  && !list.isEmpty()) {
                    final ParseObject challenge = list.get(0);
                    challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, challengeStatus);
                    challenge.saveInBackground();
                }
            }
        });
    }

    //Moving methods to this class
    public static void chooseStartingPlayer(final ParseObject challenge) {
        ParseQuery<ParseObject> startingPlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
        startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
        startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_OWNER, true);
        startingPlayerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    ParseObject startingPlayer = list.get(0);
                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE, challenge);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, startingPlayer);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                    }
                    else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN){
                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                        for (int i = 1; i < list.size(); i++ ) {
                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                        }
                    }
                    challengeEvent.saveInBackground();
                    startingPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                    startingPlayer.saveInBackground();
                    ParseUser startingPlayerUser = (ParseUser) startingPlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                    String object = "";
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        object = "potato";
                    }
                    else if ( challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                        object = "crown";
                    }
                    ParentChallenge.sendPushNotification("You've started off with the " + object + " in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", startingPlayerUser);
                }
            }
        });
    }
}
