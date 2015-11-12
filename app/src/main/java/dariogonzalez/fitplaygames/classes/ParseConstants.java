package dariogonzalez.fitplaygames.classes;

/**
 * Created by dgonzalez on 4/9/2015.
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
    public static final String CHALLENGE_DAILY_STEPS_GOAL = "DailyStepsGoal";
    public static final String CHALLENGE_CHALLENGE_STEPS_GOAL = "ChallengeStepsGoal";
    public static final String CHALLENGE_CHALLENGE_PASS = "ChallengePass";
    public static final String CHALLENGE_CHALLENGE_START = "ChallengeStart";
    public static final String CHALLENGE_CHALLENGE_END = "ChallengeEnd";
    public static final String CHALLENGE_NUMBER_OF_DAYS = "NumberOfDays";
    public static final String CHALLENGE_ACTIVE_MINUTES_GOAL = "ActiveMinutesGoal";

    public static final String CHALLENGE_OBJECT = "ChallengeObject";



    public static final String CHALLENGE_PLAYER_STATUS = "ChallengePlayerStatus";
    public static final String CHALLENGE_PLAYER_DATE_JOINED = "DateJoined";
    public static final String CHALLENGE_PLAYER_USER_ID = "UserObject";
    public static final String CHALLENGE_PLAYER_CHALLENGE_ID = "ChallengeObject";
    public static final String CHALLENGE_PLAYER_OWNER = "IsOwner";



    /*******************FRIEND STATUS************************************************************/
    public static int FRIEND_STATUS_SENT = 0;
    public static int FRIEND_STATUS_ACCEPTED = 1;
    public static int FRIEND_STATUS_DECLINED = 2;
    public static int FRIEND_STATUS_CANCELED = 3;
    /********************************************************************************************/

    /*******************CHALLENGE STATUS*********************************************************/
    public static int CHALLENGE_STATUS_PROCESSING = 0;
    public static int CHALLENGE_STATUS_PENDING = 1;
    public static int CHALLENGE_STATUS_PLAYING = 2;
    public static int CHALLENGE_STATUS_FINISHED = 3;
    public static int CHALLENGE_STATUS_CANCELLED = 4;
    /********************************************************************************************/

    /*******************CHALLENGE PLAYER STATUS**************************************************/
    public static int CHALLENGE_PLAYER_STATUS_PENDING = 0;
    public static int CHALLENGE_PLAYER_STATUS_ACCEPTED = 1;
    public static int CHALLENGE_PLAYER_STATUS_DECLINED = 2;
    /********************************************************************************************/


    /*******************PARSE.COM ANALYTICS******************************************************/
    public static String KEY_ANALYTICS_MAIN_ACTIVITY = "MainActivity";
    public static String KEY_ANALYTICS_PROFILE = "Profile";

    public static String KEY_ANALYTICS_SELECT_GAME_HOT_POTATO = "SelectGameHotPotato";
    public static String KEY_ANALYTICS_CREATE_GAME_HOT_POTATO = "CreateGameHotPotato";
    public static String KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO = "CancelGameHotPotato";
    public static String KEY_ANALYTICS_HELP_HOT_POTATO = "HelpGameHotPotato";

    /********************************************************************************************/

}
