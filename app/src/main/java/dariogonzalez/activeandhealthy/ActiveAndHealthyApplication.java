package dariogonzalez.activeandhealthy;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import dariogonzalez.activeandhealthy.utils.ParseConstants;

/**
 * Created by Dario on 4/25/2015.
 */
public class ActiveAndHealthyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "Udu33BkI2Sz0W7I4q15eWouVdqGVONLurmEkD8O8", "14mV2PxkubXaQnGJr7muaOFA3umrRbLGOrNLci1c");
    }

    public static void updateParseInstallation(ParseUser parseUser)
    {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, parseUser.getObjectId());

        installation.saveInBackground( );
    }
}
