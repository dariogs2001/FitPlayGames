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

    //Field name
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";

    public static final String KEY_RECIPIENT_IDS = "recipientIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final String KEY_USER_ID = "userId";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    //User table
    public static final String USER_USERNAME = "username";
    public static final String USER_GENDER = "Gender";
    public static final String USER_AGE_RANGE = "AgeRange";
    public static final String USER_PROFILE_PICTURE = "ProfilePicture";

    public static final String ACTIVITY_HISTORY_DATE = "Date";
    public static final String ACTIVITY_HISTORY_STEPS = "Steps";
    public static final String ACTIVITY_HISTORY_ACTIVE_MINUTES = "ActiveMinutes";

    public static final String LAST_SEVEN_DAYS_STEPS = "Steps";
    public static final String LAST_SEVEN_DAYS_AVG = "Average";

    public static final String USER_FRIENDS_FRIEND_ID = "FriendId";
    public static final String USER_FRIENDS_STATUS = "UserFriendStatus";

    public static final String USER_OBJECT = "UserObject";
    public static final String FRIEND_OBJECT = "FriendObject";





    /********************************************************************************************/
    public static int FRIEND_STATUS_SENT = 0;
    public static int FRIEND_STATUS_ACCEPTED = 1;
    public static int FRIEND_STATUS_DECLINED = 2;
    public static int FRIEND_STATUS_CANCELED = 3;
    /********************************************************************************************/
}
