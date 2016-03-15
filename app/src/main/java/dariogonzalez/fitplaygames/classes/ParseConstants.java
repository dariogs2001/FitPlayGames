package dariogonzalez.fitplaygames.classes;

/**
 * Created by dgonzalez on 4/9/2015.
 * Constants used to work with Parse.com tables and rows
 */
public final class ParseConstants
{
    //Class name
    public static final String CLASS_MESSAGES = "Messages";
    public static final String CLASS_USER = "User";
    public static final String CLASS_ACTIVITY_HISTORY = "ActivityHistory";
    public static final String CLASS_LAST_SEVEN_DAYS =  "LastSevenDays";
    public static final String CLASS_USER_FRIENDS =  "UserFriends";
    public static final String CLASS_CHALLENGE_PLAYERS =  "ChallengePlayers";
    public static final String CLASS_CHALLENGES =  "Challenges";
    public static final String CLASS_ACTIVITY_STEPS_BY_DAY = "ActivityStepsByDay";
    public static final String CLASS_ACTIVITY_STEPS_BY_DAY_15M = "ActivityStepsByDay15Min";

    //Field name
    public static final String OBJECT_ID = "objectId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";

    public static final String KEY_RECIPIENT_IDS = "recipientIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String CHALLENGE_ID = "challengeId";

    public static final String KEY_USER_ID = "userId";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    //User table
    public static final String USER_USERNAME = "username";
    public static final String USER_GENDER = "Gender";
    public static final String USER_AGE_RANGE = "AgeRange";
    public static final String USER_PROFILE_PICTURE = "ProfilePicture";
    public static final String USER_PERMISSION = "Permission";
    public static final String USER_LAST_SEVEN_DAYS = "lastSevenDays";

    public static final String ACTIVITY_HISTORY_DATE = "Date";
    public static final String ACTIVITY_HISTORY_TIME = "Time";
    public static final String ACTIVITY_HISTORY_STEPS = "Steps";
    public static final String ACTIVITY_HISTORY_ACTIVE_MINUTES = "ActiveMinutes";

    public static final String LAST_SEVEN_DAYS_STEPS = "Steps";
    public static final String LAST_SEVEN_DAYS_AVG = "Average";

    public static final String USER_FRIENDS_FRIEND_ID = "FriendId";
    public static final String USER_FRIENDS_STATUS = "UserFriendStatus";

    public static final String USER_OBJECT = "UserObject";
    public static final String FRIEND_OBJECT = "FriendObject";

    public static final String CHALLENGE_CHALLENGE_ID = "objectId";
    public static final String CHALLENGE_CHALLENGE_NAME = "ChallengeName";
    public static final String CHALLENGE_CHALLENGE_TYPE = "ChallengeType";
    public static final String CHALLENGE_CHALLENGE_STATUS = "ChallengeStatus";
    public static final String CHALLENGE_CHALLENGE_STEPS_GOAL = "ChallengeStepsGoal";
    public static final String CHALLENGE_CHALLENGE_PASS = "ChallengePass";
    public static final String CHALLENGE_CHALLENGE_START = "ChallengeStart";
    public static final String CHALLENGE_CHALLENGE_END = "ChallengeEnd";
    public static final String CHALLENGE_NUMBER_OF_DAYS = "NumberOfDays";
    public static final String CHALLENGE_ACTIVE_MINUTES_GOAL = "ActiveMinutesGoal";

    public static final String CHALLENGE_OBJECT = "ChallengeObject";



    public static final String CHALLENGE_PLAYER_STATUS = "ChallengePlayerStatus";
    public static final String CHALLENGE_PLAYER_DATE_JOINED = "DateJoined";
    public static final String CHALLENGE_PLAYER_USER_OBJECT = "UserObject";
    public static final String CHALLENGE_PLAYER_CHALLENGE_OBJECT = "ChallengeObject";
    public static final String CHALLENGE_PLAYER_OWNER = "IsOwner";
    public static final String CHALLENGE_PLAYER_IS_LOSER = "IsLoser";
    public static final String CHALLENGE_PLAYER_IS_WINNER = "IsWinner";
    public static final String CHALLENGE_PLAYER_IS_TURN = "IsTurn";
    public static final String CHALLENGE_PLAYER_PASSES = "Passes";
    public static final String CHALLENGE_PLAYER_GAME_TIME = "GameTime";
    public static final String CHALLENGE_PLAYER_AVERAGE_TIME = "AvgTime";
    public static final String CHALLENGE_NUMBER_OF_PLAYERS = "NumberOfPlayers";


    /*******************FRIEND STATUS************************************************************/
    public static int FRIEND_STATUS_SENT = 0;
    public static int FRIEND_STATUS_ACCEPTED = 1;
    public static int FRIEND_STATUS_DECLINED = 2;
    public static int FRIEND_STATUS_CANCELED = 3;
    /********************************************************************************************/

    /*******************CHALLENGE STATUS*********************************************************/
    public static final int CHALLENGE_STATUS_PENDING = 1;
    public static final int CHALLENGE_STATUS_PLAYING = 2;
    public static final int CHALLENGE_STATUS_FINISHED = 3;
    public static final int CHALLENGE_STATUS_CANCELLED = 4;
    /********************************************************************************************/

    /*******************CHALLENGE PLAYER STATUS**************************************************/
    public static int CHALLENGE_PLAYER_STATUS_PENDING = 0;
    public static int CHALLENGE_PLAYER_STATUS_ACCEPTED = 1;
    public static int CHALLENGE_PLAYER_STATUS_DECLINED = 2;
    /********************************************************************************************/


    /**********************CHALLENGE EVENT *****************************************************/
    public static String CLASS_CHALLENGE_EVENTS = "ChallengeEvent";
    public static String CHALLENGE_EVENTS_CHALLENGE_PLAYER = "ChallengePlayerObject";
    public static String CHALLENGE_EVENTS_CHALLENGE = "ChallengeObject";
    public static String CHALLENGE_EVENTS_FINAL_STATUS = "FinalStatus";
    public static String CHALLENGE_EVENTS_START_TIME = "StartTime";
    public static String CHALLENGE_EVENTS_END_TIME = "EndTime";
    public static String CHALLENGE_EVENTS_GAME_TIME = "GameTime";
    public static String CHALLENGE_EVENTS_STEP_PROGRESSION = "StepProgression";

    public static int CHALLENGE_EVENTS_FINAL_STATUS_PLAYING = 1;
    public static int CHALLENGE_EVENTS_FINAL_STATUS_DONE = 2;

    public static String KEY_ANALYTICS_HELP_HOT_POTATO = "HelpGameHotPotato";
    public static String KEY_ANALYTICS_HELP_CAPTURE_CROWN = "HelpGameCaptureCrown";
    /********************************************************************************************/

    /******************Activity Steps By Day 15 Min*********************************************/
    public static String CLASS_ACTIVITY_STEPS_15_MIN = "ActivityStepsByDay15Min";
    public static String ACTIVITY_STEPS_DATE = "Date";
    public static String ACTIVITY_STEPS_STEPS = "Steps";
    public static String ACTIVITY_STEPS_USER_ID = "userId";
    /*******************************************************************************************/

    /*******************PARSE.COM ANALYTICS******************************************************/
    public static String KEY_ANALYTICS_MAIN_ACTIVITY = "MainActivity";

    public static String KEY_ANALYTICS_PROFILE = "Profile";
    public static String KEY_ANALYTICS_SELECT_GAME_HOT_POTATO = "SelectGameHotPotato";
    public static String KEY_ANALYTICS_CREATE_GAME_HOT_POTATO = "CreateGameHotPotato";
    public static String KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO = "CancelGameHotPotato";
    public static String KEY_ANALYTICS_SELECT_GAME_CAPTURE_CROWN = "SelectGameCaptureCrown";
    public static String KEY_ANALYTICS_CREATE_GAME_CAPTURE_CROWN = "CreateGameCaptureCrown";
    public static String KEY_ANALYTICS_CANCEL_GAME_CAPTURE_CROWN = "CancelGameCaptureCrown";

    /********************************************************************************************/

    /***************************Parse Permission*************************************************/
    public static int PERMISSION_ALL = 0;
    public static int PERMISSION_HOT_POTATO = 1;
    public static int PERMISSION_CAPTURE_THE_CROWN = 2;
    /********************************************************************************************/
}
