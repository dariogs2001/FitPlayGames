package dariogonzalez.fitplaygames.utils;

import android.content.Context;
import android.content.Intent;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.parse.ParseAnalytics;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import dariogonzalez.fitplaygames.LoginActivity;

/**
 * Created by Dario on 10/31/2015.
 */
public class Utils {

    public static void trackData(String nameLog, String valueLog)
    {
        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put(nameLog, valueLog);
        dimensions.put("ParseUserId", ParseUser.getCurrentUser().getObjectId());
        FlurryAgent.logEvent(nameLog, dimensions);
        ParseAnalytics.trackEventInBackground(nameLog, dimensions);
    }

    public static boolean isParseUserLoggedIn()
    {
//        return (ParseSession.getCurrentSessionInBackground().getResult().getObjectId() != null);
        return (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getObjectId() != null);
    }

    public static void logoutRedirect(Context context)
    {
        if (!isParseUserLoggedIn())
        {
            Intent intent = new Intent(context, LoginActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            context.startActivity(intent);
        }
    }
}
