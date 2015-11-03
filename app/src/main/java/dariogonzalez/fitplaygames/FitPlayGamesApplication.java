package dariogonzalez.fitplaygames;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        ParsePush.subscribeInBackground("FitPlayChannel");
    }

}
