package dariogonzalez.fitplaygames;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import dariogonzalez.fitplaygames.Helper.ParseUtils;
import dariogonzalez.fitplaygames.classes.FlurryConstants;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by Dario on 4/25/2015.
 */
public class FitPlayGamesApplication extends Application {
    private static String TAG = FitPlayGamesApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "Udu33BkI2Sz0W7I4q15eWouVdqGVONLurmEkD8O8", "14mV2PxkubXaQnGJr7muaOFA3umrRbLGOrNLci1c");
        FlurryAgent.init(this, FlurryConstants.FitPlayFlurryKey);
    }

    public static void updateParseInstallation(ParseUser parseUser)
    {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, parseUser.getObjectId());

        installation.saveInBackground();
        ParsePush.subscribeInBackground(PushConfig.PARSE_CHANNEL);
    }

    public static void unsubscribeFromPush() {
        ParsePush.unsubscribeInBackground(PushConfig.PARSE_CHANNEL);
    }

    public static void sendPushNotification(String message, ParseUser user) {
// Create our Installation query
        ParseQuery pushQuery = ParseInstallation.getQuery();
        //pushQuery.whereEqualTo("challengeId", challengeId);
        pushQuery.whereEqualTo("user", user);

// Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(message);
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

}
