package dariogonzalez.fitplaygames.utils;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dario on 10/31/2015.
 */
public class Utils {

    public static void trackData(String nameLog, String valueLog)
    {
        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put(nameLog, valueLog);
        FlurryAgent.logEvent(nameLog, dimensions);
        ParseAnalytics.trackEventInBackground(nameLog, dimensions);
    }
}
