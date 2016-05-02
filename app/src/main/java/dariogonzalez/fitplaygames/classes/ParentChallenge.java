package dariogonzalez.fitplaygames.classes;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
    private String inviteChallengeMessage; // A push message to be sent when a user gets invited to a challenge
    private int numberOfPlayers = 1; //Total number of players when the game is created.
    private int numberOfPlayersInvited = 1; //Total number of players when the game is created.

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

    public void createChallenge(final ParseUser user, String challengeName, int stepsGoal, Date startDate, Date endDate, final GetObjectIdCallback callback, int numberOfPlayers, int numberOfPlayersInvited) {
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
        challengeObject.put(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS_INVITED, numberOfPlayersInvited);

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


    public static void updateChallenges() {
        AsyncUpdateChallenges task = new  AsyncUpdateChallenges();
        task.execute();
    }

        /**
         * Update challenges
         *
         */
//    public static void updateChallenges() {
//        // Grab all the ChallengePlayers objects for the current user where the status is accepted
//        ParseQuery<ParseObject> challengePlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
//        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
//        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());
//        challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> challengePlayers, ParseException e) {
//                if (e == null) {
//                    //Loop through all the the Objects, get the challenge and update only the ones PLAYING OR PENDING
//                    for (final ParseObject challengePlayer : challengePlayers) {
//                        try {
//                            ParseObject challenge = challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();
//                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PLAYING ||
//                                    challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PENDING) {
//                                updateChallenge(challenge, challengePlayer);
//                            }
//                        } catch (Exception ex) {
//
//                        }
//                    }
//                }
//            }
//        });
//    }

    public static void updateChallengesSync() {
        try {
            // Grab all the ChallengePlayers objects for the current user where the status is accepted
            ParseQuery<ParseObject> challengePlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
            challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());

            List<ParseObject> challengePlayers = challengePlayerQuery.find();

            for (final ParseObject challengePlayer : challengePlayers) {
                try {
                    ParseObject challenge = challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PLAYING ||
                            challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PENDING) {
                        updateChallenge(challenge, challengePlayer);
                    }
                } catch (Exception ex) {

                }
            }
        }
        catch (Exception ex){}
    }

//    public static void updateChallenge(final ParseObject challenge, final ParseObject challengePlayer) {
//        Log.d("TEST", "You got into ParentChallenge.updateChallenge successfully");
//        // First, check to see what the status of the challenge is. If it hasn't started, check to see if it needs to start
//        int challengeStatus = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS);
//        int numOfPlayers = challenge.getInt(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS);
//        Date today = new Date();
//
//        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING && numOfPlayers > 1) {
//            Log.d("TEST", "PENDING");
//            Date startDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_START);
//            if (today.after(startDate)) {
//                challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PLAYING);
//                challenge.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null)
//                        {
//                            //Not need to implement this method in each class, both class work the same...
//                            chooseStartingPlayer(challenge);
//                        }
//                    }
//                });
//            }
//        }
//        // Then, check to see if it needs to end
//        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {
//            Log.d("TEST", "PLAYING");
//            Date endDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_END);
//            if (today.after(endDate)) {
//                challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_FINISHED);
//                challenge.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null)
//                        {
//                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
//                                HotPotatoChallenge.findLoser(challenge);
//                            } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                                CaptureTheCrownChallenge.findWinner(challenge);
//                            }
//                        }
//                    }
//                });
//            } else {
//                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO)
//                {
//                    Log.d("TEST", "HP Before end date");
//                    final int stepsGoal = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL);
//                    // If current user has an active "turn", check steps and see if they should pass it
//                    ParseQuery<ParseObject> challengeEventQuery = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                    challengeEventQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//                        @Override
//                        public void done(ParseObject parseObject, ParseException e) {
//                            if (e == null && parseObject != null) {
//                                Log.d("TEST", "Challenge Event");
//                                final ParseObject challengeEvent = parseObject;
//                                Log.d("TEST", "Challenge event inside");
//                                // Then, update the steps for this user, see if they have finished their "turn" and update the challenge event table
//                                Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
//                                ParseQuery<ParseObject> activityStepsQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_ACTIVITY_STEPS_15_MIN);
//                                // Where userId and where date >= startTime
//                                activityStepsQuery.whereEqualTo(ParseConstants.ACTIVITY_STEPS_USER_ID, ParseUser.getCurrentUser().getObjectId());
//                                Log.d("TEST", "Start time: " + startTime.toString());
//                                activityStepsQuery.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_STEPS_DATE, startTime);
//                                activityStepsQuery.findInBackground(new FindCallback<ParseObject>() {
//                                    @Override
//                                    public void done(List<ParseObject> list, ParseException e) {
//                                        if (e == null) {
//                                            int size = list.size();
//                                            Log.d("TEST", "size: " + size);
//                                            int stepsAmount = 0;
//                                            for (ParseObject data : list) {
//                                                //Adding first to be sure we get the last updated value,
//                                                int steps = data.getInt(ParseConstants.ACTIVITY_STEPS_STEPS);
//                                                stepsAmount += steps;
//
//                                                // If the added up steps are greater than the steps goal, set challenge event status to done and then get a new player
//                                                if (stepsAmount >= stepsGoal)
//                                                {
//                                                    //trying something different to see if we can find why sometimes this is called 2 or even 3 times
//                                                    try {
//                                                        Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
//                                                        Date endTime = new Date();
//                                                        //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
//                                                        final long timeDifference = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
//
//                                                        //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
//
//                                                        /*****FORM HERE******/
//                                                        challengeEvent.save();
//                                                        //Updating ChallengePlayer table with new values
//                                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
//
//                                                        challengePlayer.increment(ParseConstants.CHALLENGE_PLAYER_PASSES);
//                                                        int playerPasses = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
//
//                                                        long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
//                                                        gameTime += timeDifference;
//                                                        long avgTime = gameTime / playerPasses;
//
//                                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
//                                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);
//
//                                                        challengePlayer.save();
//                                                        sendPushNotification("Congrats! You just passed the potato in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", ParseUser.getCurrentUser());
//                                                        chooseNextPlayerHotPotato(challenge, challengePlayer);
//                                                        /******TO HERE******/
//
//                                                        break;
//                                                    }
//                                                    catch (Exception ex)
//                                                    {}
//                                                }
//                                            }
//                                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
//                                            challengeEvent.saveInBackground();
//                                        } else {
//                                            Log.d("TEST", e.toString());
//                                        }
//                                    }
//                                });
//                            } else {
//                                Log.d("TEST", e.getMessage());
//                            }
//                        }
//                    });
//                }
//                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                    final int stepsGoal = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL);
//                    Log.d("Crown: ", "You're in the CROWN if statement");
//                    // Query the player in a specific challenge if their status is playing (i.e. they are trying to capture the crown)
//                    ParseQuery<ParseObject> challengeEventQuery = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                    challengeEventQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//                        @Override
//                        public void done(final ParseObject challengeEvent, ParseException e) {
//                            if (e == null && challengeEvent != null) {
//                                // Loop through all of the players returned with status of playing
////                                for (final ParseObject challengeEvent : list)
//                                {
//                                    Log.d("TEST", "Challenge event inside");
//                                    // Then, update the steps for this user, see if they have walked enough steps to capture the crown and update the challenge event table
//                                    Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
//                                    ParseQuery<ParseObject> activityStepsQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_ACTIVITY_STEPS_15_MIN);
//                                    // Where userId and where date >= startTime
//                                    activityStepsQuery.whereEqualTo(ParseConstants.ACTIVITY_STEPS_USER_ID, ParseUser.getCurrentUser().getObjectId());
//                                    Log.d("TEST", "Start time: " + startTime.toString());
//                                    activityStepsQuery.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_STEPS_DATE, startTime);
//                                    activityStepsQuery.findInBackground(new FindCallback<ParseObject>() {
//                                        @Override
//                                        public void done(List<ParseObject> list, ParseException e) {
//                                            if (e == null) {
//                                                int size = list.size();
//                                                Log.d("TEST", "size: " + size);
//                                                int stepsAmount = 0;
//
//                                                //Added this boolean to avoid calling multiple times the save function, an async method
//                                                boolean isFinished = false;
//                                                for (ParseObject data : list) {
//
//                                                    if (isFinished) break;
//
//                                                    //Adding first to be sure we get the last updated value,
//                                                    Log.d(TAG, "Last updated value");
//                                                    int steps = data.getInt(ParseConstants.ACTIVITY_STEPS_STEPS);
//                                                    stepsAmount += steps;
//
//                                                    // If the added up steps are greater than the steps goal, set challenge event status to done for all crown players
//                                                    if (stepsAmount >= stepsGoal) {
//                                                        isFinished = true;
//                                                        Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
//                                                        Date endTime = new Date();
//                                                        //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
//                                                        final long timeDifference = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
//                                                        //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
//                                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
//                                                        challengeEvent.saveInBackground(new SaveCallback() {
//                                                            @Override
//                                                            public void done(ParseException e) {
//                                                                if (e == null) {
//                                                                    //Updating ChallengePlayer table with new values
//                                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
//                                                                    challengePlayer.increment(ParseConstants.CHALLENGE_PLAYER_PASSES);
//                                                                    int playerCaptures = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
//                                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_PASSES, playerCaptures);
//                                                                    long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
//                                                                    gameTime += timeDifference;
//                                                                    long avgTime = gameTime / playerCaptures;
//                                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
//                                                                    challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);
//                                                                    challengePlayer.saveEventually(new SaveCallback() {
//                                                                        @Override
//                                                                        public void done(ParseException e) {
//                                                                            if (e == null) {
//                                                                                updateCtCChallengeEventsToDone(challenge);
//                                                                                handOverCrown(challenge, challengePlayer);
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                        });
//                                                        // This query is supposed to find the player with the crown (aka not playing), change status to DONE, and update that players stats
//                                                        ParseQuery<ParseObject> challengeEventQuery = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                                                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                                                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
//                                                        challengeEventQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//                                                            @Override
//                                                            public void done(ParseObject parseObject, ParseException e) {
//                                                                if (e == null && parseObject != null) {
//                                                                    Log.d("Crown Query: ", "changing crown player status to done");
//                                                                    Date startTime = parseObject.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
//                                                                    Date endTime = new Date();
//                                                                    //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
//                                                                    final long timeDifference = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
//                                                                    //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
//                                                                    parseObject.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
//                                                                    parseObject.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
//                                                                    parseObject.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
//                                                                    parseObject.saveInBackground(new SaveCallback() {
//                                                                        @Override
//                                                                        public void done(ParseException e) {
//                                                                            if (e == null) {
//                                                                                int playerCaptures = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
//                                                                                long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
//                                                                                gameTime += timeDifference;
//                                                                                long avgTime = gameTime / playerCaptures;
//                                                                                challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
//                                                                                challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);
//                                                                                challengePlayer.saveInBackground();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                        });
//                                                        // break if code makes it to the end of this if statement
//                                                        break;
//                                                    } // END IF STATEMENT FOR IF STEPSAMOUNT >= STEPSGOAL
//                                                }
//                                                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
//                                                challengeEvent.saveInBackground();
//                                            } else {
//                                                Log.d("TEST", e.getMessage());
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }

    public static void updateChallenge(final ParseObject challenge, final ParseObject challengePlayer) {
        // First, check to see what the status of the challenge is. If it hasn't started, check to see if it needs to start
        int challengeStatus = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS);
        int numOfPlayers = challenge.getInt(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS);
        Date today = new Date();

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING && numOfPlayers > 1) {
            Log.d("TEST", "PENDING");
            Date startDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_START);
            if (today.after(startDate))
            {
                try {
                    challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PLAYING);
                    challenge.save();
                    chooseStartingPlayer(challenge);
                }
                catch (Exception ex) {}
            }
        }
        // Then, check to see if it needs to end
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {
            Log.d("TEST", "PLAYING");
            Date endDate = challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_END);
            if (today.after(endDate)) {
                try {
                    challenge.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_FINISHED);
                    challenge.save();
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        HotPotatoChallenge.findLoser(challenge);
                    } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                        CaptureTheCrownChallenge.findWinner(challenge);
                    }
                }
                catch (Exception ex) {}
            } else {
                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO)
                {
                    try {
                        Log.d("TEST", "HP Before end date");
                        final int stepsGoal = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL);
                        // If current user has an active "turn", check steps and see if they should pass it
                        ParseQuery<ParseObject> challengeEventQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);

                        ParseObject challengeEvent = challengeEventQuery.getFirst();

                        if (challengeEvent != null) {
                            Log.d("TEST", "Challenge event inside");
                            // Then, update the steps for this user, see if they have finished their "turn" and update the challenge event table
                            Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                            ParseQuery<ParseObject> activityStepsQuery = ParseQuery.getQuery(ParseConstants.CLASS_ACTIVITY_STEPS_15_MIN);
                            // Where userId and where date >= startTime
                            activityStepsQuery.whereEqualTo(ParseConstants.ACTIVITY_STEPS_USER_ID, ParseUser.getCurrentUser().getObjectId());
                            Log.d("TEST", "Start time: " + startTime.toString());
                            activityStepsQuery.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_STEPS_DATE, startTime);

                            List<ParseObject> list = activityStepsQuery.find();
                            int size = list.size();
                            Log.d("TEST", "size: " + size);
                            int stepsAmount = 0;
                            for (ParseObject data : list) {
                                //Adding first to be sure we get the last updated value,
                                int steps = data.getInt(ParseConstants.ACTIVITY_STEPS_STEPS);
                                stepsAmount += steps;

                                // If the added up steps are greater than the steps goal, set challenge event status to done and then get a new player
                                if (stepsAmount >= stepsGoal) {
                                    //trying something different to see if we can find why sometimes this is called 2 or even 3 times
                                    try {
                                        Date startTime2 = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                        Date endTime = new Date();
                                        //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
                                        final long timeDifference = (endTime.getTime() - startTime2.getTime()) / (1000 * 60);

                                        //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);

                                        challengeEvent.save();
                                        //Updating ChallengePlayer table with new values
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);

                                        challengePlayer.increment(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                        int playerPasses = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);

                                        long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
                                        gameTime += timeDifference;
                                        long avgTime = gameTime / playerPasses;

                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);

                                        challengePlayer.save();
                                        sendPushNotification("Congrats! You just passed the potato in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", ParseUser.getCurrentUser());
                                        chooseNextPlayerHotPotato(challenge, challengePlayer);

                                        break;
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
                            challengeEvent.save();
                        }
                    }
                    catch (Exception ex) {}
                }
                else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {

                    try {
                        final int stepsGoal = challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL);
                        Log.d("Crown: ", "You're in the CROWN if statement");
                        // Query the player in a specific challenge if their status is playing (i.e. they are trying to capture the crown)
                        ParseQuery<ParseObject> challengeEventQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);

                        ParseObject challengeEvent = challengeEventQuery.getFirst();
                        if (challengeEvent != null) {
                            // Loop through all of the players returned with status of playing
//                                for (final ParseObject challengeEvent : list)
                            {
                                Log.d("TEST", "Challenge event inside");
                                // Then, update the steps for this user, see if they have walked enough steps to capture the crown and update the challenge event table
                                Date startTime = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                ParseQuery<ParseObject> activityStepsQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_ACTIVITY_STEPS_15_MIN);
                                // Where userId and where date >= startTime
                                activityStepsQuery.whereEqualTo(ParseConstants.ACTIVITY_STEPS_USER_ID, ParseUser.getCurrentUser().getObjectId());
                                Log.d("TEST", "Start time: " + startTime.toString());
                                activityStepsQuery.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_STEPS_DATE, startTime);

                                List<ParseObject> list = activityStepsQuery.find();

                                int size = list.size();
                                Log.d("TEST", "size: " + size);
                                int stepsAmount = 0;

                                for (ParseObject data : list) {
                                    //Adding first to be sure we get the last updated value,
                                    Log.d(TAG, "Last updated value");
                                    int steps = data.getInt(ParseConstants.ACTIVITY_STEPS_STEPS);
                                    stepsAmount += steps;

                                    // If the added up steps are greater than the steps goal, set challenge event status to done for all crown players
                                    if (stepsAmount >= stepsGoal) {
                                        Date startTime3 = challengeEvent.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                        Date endTime = new Date();
                                        //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
                                        final long timeDifference = (endTime.getTime() - startTime3.getTime()) / (1000 * 60);
                                        //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference);
                                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
                                        challengeEvent.save();

                                        //Updating ChallengePlayer table with new values
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                                        challengePlayer.increment(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                        int playerCaptures = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_PASSES, playerCaptures);
                                        long gameTime = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
                                        gameTime += timeDifference;
                                        long avgTime = gameTime / playerCaptures;
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime);
                                        challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime);

                                        challengePlayer.save();

                                        // This query is supposed to find the player with the crown (aka not playing), change status to DONE, and update that players stats
                                        ParseQuery<ParseObject> challengeEventQuery2 = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
                                        challengeEventQuery2.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                                        challengeEventQuery2.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
                                        ParseObject parseObject = challengeEventQuery2.getFirst();

                                        if (parseObject != null) {
                                            Log.d("Crown Query: ", "changing crown player status to done");
                                            Date startTime2 = parseObject.getDate(ParseConstants.CHALLENGE_EVENTS_START_TIME);
                                            Date endTime2 = new Date();
                                            //Result is in miliseconds so I divided by 1000 to set the seconds and by 60 to set the minutes
                                            final long timeDifference2 = (endTime2.getTime() - startTime2.getTime()) / (1000 * 60);
                                            //Changing status to DONE, and preparing everything to set next player and create new challengeEvent
                                            parseObject.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                                            parseObject.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, endTime2);
                                            parseObject.put(ParseConstants.CHALLENGE_EVENTS_GAME_TIME, timeDifference2);
                                            parseObject.save();
//                                            int playerCaptures2 = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
//                                            long gameTime2 = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_GAME_TIME);
//                                            gameTime2 += timeDifference2;
//                                            long avgTime2 = gameTime2 / playerCaptures2;
//                                            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_GAME_TIME, gameTime2);
//                                            challengePlayer.put(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME, avgTime2);
//                                            challengePlayer.save();
                                        }

                                        updateCtCChallengeEventsToDone(challenge);
                                        handOverCrown(challenge, challengePlayer);

                                        // break if code makes it to the end of this if statement
                                        break;
                                    } // END IF STATEMENT FOR IF STEPSAMOUNT >= STEPSGOAL
                                }
                                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION, stepsAmount);
                                challengeEvent.save();
                            }
                        }
                    }
                    catch (Exception ex)
                    {}
                }
            }
        }
    }


    /**
     * Method used to choose next player in the HotPotato game.
     * At this point I already have set the previous status for the previous player, and I am ready to set everything for the new player.
     * @param challenge - object from Parse.com
     * @param challengePlayer - object from Parse.com
     */
//    public static void chooseNextPlayerHotPotato(final ParseObject challenge, final ParseObject challengePlayer) {
//
//        ParseQuery<ParseObject> nextPlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
//        nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
//
//        final int passes = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
//        nextPlayerQuery.whereLessThanOrEqualTo(ParseConstants.CHALLENGE_PLAYER_PASSES, passes);
//        nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
//        nextPlayerQuery.orderByAscending(ParseConstants.CHALLENGE_PLAYER_PASSES);
//
//        nextPlayerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                if (e == null && parseObject != null) {
//                    try {
//                        final ParseObject nextPlayer = parseObject;
//                        nextPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
//                        nextPlayer.saveInBackground();
//                        ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, nextPlayer);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                        challengeEvent.save();
//                        //Sending push notification...
//                        ParseUser nextPlayerUser = nextPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
//                        String object = "";
//                        if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
//                            object = "potato";
//                        } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                            //This else should never happens, this is a HOT POTATO game.
//                            object = "crown";
//                        }
//                        ParentChallenge.sendPushNotification("You have been passed the " + object + " in game '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", nextPlayerUser);
//                    } catch (Exception ex) {
//                    }
//
//                }
//            }
//        });
//    }

    public static void chooseNextPlayerHotPotato(final ParseObject challenge, final ParseObject challengePlayer) {
        try {
            ParseQuery<ParseObject> nextPlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);

            final int passes = challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
            nextPlayerQuery.whereLessThanOrEqualTo(ParseConstants.CHALLENGE_PLAYER_PASSES, passes);
            nextPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
            nextPlayerQuery.orderByAscending(ParseConstants.CHALLENGE_PLAYER_PASSES);

            ParseObject parseObject = nextPlayerQuery.getFirst();
            if (parseObject != null) {
                try {
                    final ParseObject nextPlayer = parseObject;
                    nextPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                    nextPlayer.save();
                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, nextPlayer);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                    challengeEvent.save();
                    //Sending push notification...
                    ParseUser nextPlayerUser = nextPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                    String object = "";
                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                        object = "potato";
                    } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                        //This else should never happens, this is a HOT POTATO game.
                        object = "crown";
                    }
                    ParentChallenge.sendPushNotification("You have been passed the " + object + " in game '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", nextPlayerUser);
                } catch (Exception ex) {
                }
            }
        }
        catch (Exception ex)
        {}
    }

//    public static void updateCtCChallengeEventsToDone(final ParseObject challenge)
//    {
//        ParseQuery<ParseObject> challengeEventQuery = new ParseQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
//        challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//        challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e == null) {
//                    for (ParseObject challengeEvent : list) {
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, new Date());
//                        challengeEvent.saveInBackground();
//                    }
//                }
//            }
//        });
//    }

    public static void updateCtCChallengeEventsToDone(final ParseObject challenge)
    {
        try {
            ParseQuery<ParseObject> challengeEventQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_EVENTS);
            challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);

            List<ParseObject> list =  challengeEventQuery.find();

            for (ParseObject challengeEvent : list) {
                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE);
                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_END_TIME, new Date());
                challengeEvent.save();
            }
        }
        catch (Exception ex)
        {

        }
    }

    /**
     * Method used to choose next player in the Capture The Crown game.
     * @param challenge - object from Parse.com
     * @param challengePlayer - object from Parse.com
     */

//    public static void handOverCrown(final ParseObject challenge, final ParseObject challengePlayer) {
//        // Query that returns all players in specific challenge, getting only those who have accepted the challenge
//        ParseQuery<ParseObject> moveTurnQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
//        moveTurnQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
//        moveTurnQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
//        moveTurnQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e == null && !list.isEmpty()) {
//                    // Loop through all players returned from the query
//                    for (final ParseObject crownPlayer : list) {
//                        ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, crownPlayer);
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
//                        // If current user earned the crown, change event status to having the crown
//                        if (crownPlayer == challengePlayer) {
//                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
////                            crownPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
//                            challengeEvent.saveInBackground(new SaveCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if (e == null) {
//                                        ParseUser crownTaker = crownPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);// .get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
//                                        sendPushNotification("You have captured the crown in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownTaker);
//                                    }
//                                }
//                            });
//                        }
//                        // All other players should be created with a playing status and turn set to false.  Turn = true is reserved for the crown holder.
//                        else {
//                            challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                            crownPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
//                            crownPlayer.saveInBackground();
//
//                            challengeEvent.saveInBackground(new SaveCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if (e == null) {
//                                        //Sending push notification only to players that don't have the crown.
//                                        ParseUser crownSeekerPlayerUser = crownPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
//                                        ParentChallenge.sendPushNotification("Another player captured the crown! Try to get it back in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownSeekerPlayerUser);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//        });
//    }

    public static void handOverCrown(final ParseObject challenge, final ParseObject challengePlayer) {

        try {
            // Query that returns all players in specific challenge, getting only those who have accepted the challenge
            ParseQuery<ParseObject> moveTurnQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            moveTurnQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
            moveTurnQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);

            List<ParseObject> list = moveTurnQuery.find();
            if (!list.isEmpty()) {
                // Loop through all players returned from the query
                for (final ParseObject crownPlayer : list) {
                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, crownPlayer);
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                    // If current user earned the crown, change event status to having the crown
                    if (crownPlayer == challengePlayer) {
                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
                        challengeEvent.save();

                        ParseUser crownTaker = crownPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);// .get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                        sendPushNotification("You have captured the crown in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownTaker);
                    }
                    // All other players should be created with a playing status and turn set to false.  Turn = true is reserved for the crown holder.
                    else {
                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                        crownPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
                        crownPlayer.save();

                        challengeEvent.save();
                        //Sending push notification only to players that don't have the crown.
                        ParseUser crownSeekerPlayerUser = crownPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                        ParentChallenge.sendPushNotification("Another player captured the crown! Try to get it back in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownSeekerPlayerUser);
                    }
                }
            }
        }
        catch (Exception ex) {}
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

    public int getNumberOfPlayersInvited()
    {
        return numberOfPlayersInvited;
    }

    public void setNumberOfPlayersInvited(int numOfPlayersInvited)
    {
        numberOfPlayersInvited = numOfPlayersInvited;
    }



//    public void updateChallengeStatusInDatabase(final String challengeId, final int challengeStatus)
//    {
//        ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
//        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengeId);
//        challengeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                if (e == null && parseObject != null) {
//                    parseObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, challengeStatus);
//                    parseObject.saveInBackground();
//                }
//            }
//        });
//    }

    public void updateChallengeStatusInDatabase(final String challengeId, final int challengeStatus)
    {
        try {
            ParseQuery<ParseObject> challengeQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGES);
            challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengeId);
            ParseObject parseObject = challengeQuery.getFirst();
            if (parseObject != null) {
                parseObject.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, challengeStatus);
                parseObject.save();
            }
        }
        catch (Exception ex)
        {}
    }

//    //Moving methods to this class
//    public static void chooseStartingPlayer(final ParseObject challenge) {
//        ParseQuery<ParseObject> startingPlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
//        startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
//        startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
//        startingPlayerQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(final List<ParseObject> list, ParseException e) {
//                if (e == null && !list.isEmpty()) {
//                    final ParseObject startingPlayer = list.get(0);
//                    ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, startingPlayer);
//                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
//                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                    }
//                    else if ( challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                        challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
//                    }
//                    challengeEvent.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null)
//                            {
//                                startingPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
//                                startingPlayer.saveInBackground(new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if (e == null) {
//                                            ParseUser startingPlayerUser = startingPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
//                                            String object = "";
//                                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
//                                                object = "potato";
//                                            } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                                                object = "crown";
//                                            }
//                                            ParentChallenge.sendPushNotification("You've started off with the " + object + " in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", startingPlayerUser);
//                                            // If Playing Capture the Crown
//                                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                                                // Query through everyone in the list that is not the starting player and add them to the database as playing with a turn set to false.
//                                                for (int idx = 1; idx < list.size(); idx++) {
//                                                    ParseObject crownSeekerPlayer = list.get(idx);
//                                                    ParseObject crownSeekerChallengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                                                    crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                                                    crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, crownSeekerPlayer);
//                                                    crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
//                                                    crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
//                                                    crownSeekerChallengeEvent.saveInBackground();
//                                                    crownSeekerPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
//                                                    crownSeekerPlayer.saveInBackground();
//                                                    ParseUser crownSeekerPlayerUser = crownSeekerPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
//                                                    ParentChallenge.sendPushNotification("Try to capture the crown in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownSeekerPlayerUser);
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }

    //Moving methods to this class
    public static void chooseStartingPlayer(final ParseObject challenge) {
        try {
            ParseQuery<ParseObject> startingPlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
            startingPlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);

            List<ParseObject> list = startingPlayerQuery.find();
            if (!list.isEmpty()) {
                final ParseObject startingPlayer = list.get(0);
                ParseObject challengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, startingPlayer);
                challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                    challengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);
                }
                challengeEvent.save();

                startingPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, true);
                startingPlayer.save();

                ParseUser startingPlayerUser = startingPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                String object = "";
                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                    object = "potato";
                } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                    object = "crown";
                }

                ParentChallenge.sendPushNotification("You've started off with the " + object + " in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", startingPlayerUser);
                // If Playing Capture the Crown
//                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
//                    // Query through everyone in the list that is not the starting player and add them to the database as playing with a turn set to false.
                for (int idx = 1; idx < list.size(); idx++) {
                        ParseObject crownSeekerPlayer = list.get(idx);
                        crownSeekerPlayer.put(ParseConstants.CHALLENGE_PLAYER_IS_TURN, false);
                        crownSeekerPlayer.save();

                        //Create new player event for every player in CTC games
                        if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                            ParseObject crownSeekerChallengeEvent = new ParseObject(ParseConstants.CLASS_CHALLENGE_EVENTS);
                            crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                            crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, crownSeekerPlayer);
                            crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_START_TIME, new Date());
                            crownSeekerChallengeEvent.put(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                            crownSeekerChallengeEvent.save();
                        }

                        ParseUser crownSeekerPlayerUser = crownSeekerPlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                        if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                            ParentChallenge.sendPushNotification("Try to capture the crown in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownSeekerPlayerUser);
                        }
                        else
                        {
                            ParentChallenge.sendPushNotification(startingPlayerUser.get(ParseConstants.USER_USERNAME) + " has started with the potato in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", crownSeekerPlayerUser);
                        }
                    }
//                }
            }
        }
        catch (Exception ex)
        {}
    }

    public static class AsyncUpdateChallenges extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            updateChallengesSync();
            return null;
        }
    }
}
