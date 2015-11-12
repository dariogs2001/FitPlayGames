package dariogonzalez.fitplaygames.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.FitbitApi;
import dariogonzalez.fitplaygames.classes.NamesIds;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by Dario on 7/21/2015.
 */

public class FitbitHelper {

    private static String TAG = FitbitHelper.class.getSimpleName();

    private FitbitAccountInfo mUserInfo;

    private OAuthService mService;
    private Token mAccessToken;

    public FitbitHelper(Context context)
    {
        getUserInfo(context);
    }

    public FitbitAccountInfo getUserInfo(Context context)
    {
        if (mUserInfo == null)
        {
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, NamesIds.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            mUserInfo = complexPreferences.getObject(NamesIds.FITBIT_ACCOUNT_INFO, FitbitAccountInfo.class);
        }

        return mUserInfo;
    }
    private String apiKey = "a2f813cf8c7420eff5629382ae6a25a4";
    private String apiSecret = "0129ef7d53df74e1bb428fdaec8df9c1";

    private String executeQuery(String url)
    {
//        if (mService == null)
//        {
//            mService = new ServiceBuilder().provider(FitbitApi.class)
//                    .apiKey(Resources.getSystem().getString(R.string.fitbit_api_key))
//                    .apiSecret(Resources.getSystem().getString(R.string.fitbit_api_secret))
//                    .callback(Resources.getSystem().getString(R.string.fitbit_callback)).build();
            mService = new ServiceBuilder().provider(FitbitApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("http://localhost").build();
            mAccessToken = new Token(mUserInfo.getAccessToken(), mUserInfo.getSecret());
//        }

        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        mService.signRequest(mAccessToken, request); // the access token from step
        // 4
        final Response response = request.send();
        final String result = response.getBody();
        return result;
    }

    private void executeQuery2(final String url)
    {
        if (mService == null) {
            mService = new ServiceBuilder().provider(FitbitApi.class)
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .callback("http://localhost").build();
            mAccessToken = new Token(mUserInfo.getAccessToken(), mUserInfo.getSecret());
        }

        new Thread(new Runnable() {
            public void run() {
                OAuthRequest request = new OAuthRequest(Verb.GET, url);
                mService.signRequest(mAccessToken, request); // the access token from step

                final Response response = request.send();
                final String result = response.getBody();

                Log.d(TAG, result);
            }
        }).start();

    }

    public void getUserLastMonthData()
    {
        FitbitLastMonthTask task = new FitbitLastMonthTask();
        task.execute("https://api.fitbit.com/1/user/-/activities/steps/date/today/1m.json");
    }

    public String getStepsRangeDateTime()
    {
//        executeQuery2("https://api.fitbit.com/1/user/-/activities/steps/date/2015-10-20/1d/15min.json");
//        executeQuery2("https://api.fitbit.com/1/user/-/activities/steps/date/2015-11-11/1d/time/3:30/22:45.json");
        executeQuery2("https://api.fitbit.com/1/user/-/activities/steps/date/2015-11-11/2015-11-11/time/3:30/22:45.json");
        return "";
    }


    public boolean isFitbitUserAlive()
    {
        return (mUserInfo != null && mUserInfo.getAccessToken().length() > 0 && mUserInfo.getSecret().length() > 0);
    }

    //I have to create this because Parse.com does not support aggregate queries, so to avoid going through all the HistoryActivity table to try
    //to calculate the last 7 days created a new table with this info. So each time HistoryActivity table is updated I should update this new table
    //to keep track of the last 7 days steps.
    public void lastSevenDaySumAndAverage(final String parseUserId)
    {
        final Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
        today.add(Calendar.DAY_OF_YEAR, -7);
        Date sevenDaysBeforeToday = today.getTime();

        //For the user returning the 7 last days of activity
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_ACTIVITY_HISTORY);
        query.whereEqualTo(ParseConstants.KEY_USER_ID, parseUserId);
        query.whereGreaterThanOrEqualTo(ParseConstants.ACTIVITY_HISTORY_DATE, sevenDaysBeforeToday);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null)
                {
                    double totalSteps = 0;
                    double avgSteps = 0;

                    for (ParseObject obj : list)
                    {
                        totalSteps = totalSteps + obj.getInt(ParseConstants.ACTIVITY_HISTORY_STEPS);
                    }

                    if (list.size() > 0) {
                        avgSteps = totalSteps / list.size();
                    }

                    final double totalStepsFinal = totalSteps;
                    final double avgStepsFinal = avgSteps;

                    //Now save the value in the table
                    ParseQuery<ParseObject> queryLast = ParseQuery.getQuery(ParseConstants.CLASS_LAST_SEVEN_DAYS);
//                    queryLast.whereEqualTo(ParseConstants.KEY_USER_ID, parseUserId);
                    queryLast.whereEqualTo(ParseConstants.USER_OBJECT, ParseUser.getCurrentUser());
                    queryLast.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, com.parse.ParseException e) {
                            if (e == null)
                            {
                                parseObject.put(ParseConstants.LAST_SEVEN_DAYS_STEPS, totalStepsFinal);
                                parseObject.put(ParseConstants.LAST_SEVEN_DAYS_AVG, avgStepsFinal);
                                parseObject.saveInBackground();
                            }
                            else
                            {
                                ParseObject lastSevenDays = new ParseObject(ParseConstants.CLASS_LAST_SEVEN_DAYS);
                                lastSevenDays.put(ParseConstants.KEY_USER_ID, parseUserId);
                                lastSevenDays.put(ParseConstants.LAST_SEVEN_DAYS_STEPS, totalStepsFinal);
                                lastSevenDays.put(ParseConstants.LAST_SEVEN_DAYS_AVG, avgStepsFinal);
                                lastSevenDays.put(ParseConstants.USER_OBJECT, ParseUser.getCurrentUser());

                                lastSevenDays.saveInBackground();
                            }
                        }
                    });
                }
            }
        });
    }

//    public void testRelationalQuery()
//    {
//        final ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery(ParseConstants.CLASS_USER);
//        innerQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, com.parse.ParseException e) {
//                if (e == null) {
//                    ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_ACTIVITY_HISTORY);
//                    query.whereMatchesQuery(ParseConstants.CLASS_USER, innerQuery);
//                    query.getFirstInBackground(new GetCallback<ParseObject>() {
//                        @Override
//                        public void done(ParseObject parseObject, com.parse.ParseException e) {
//                            if (e == null)
//                            {
//                                parseObject
//                            }
//                            else
//                            {
//
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//    }

    public class FitbitLastMonthTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return executeQuery(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "Result = " + s);

            try {
                //Save daa in Database
                JSONObject jsonRootObject = new JSONObject(s);
                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("activities-steps");
                final String parseUserId = ParseUser.getCurrentUser().getObjectId();
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dateTime = jsonObject.optString("dateTime").toString();
                        final int value = Integer.parseInt(jsonObject.optString("value").toString());
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        final Date date = df.parse(dateTime);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_ACTIVITY_HISTORY);
                        query.whereEqualTo(ParseConstants.KEY_USER_ID, parseUserId);
                        query.whereEqualTo(ParseConstants.ACTIVITY_HISTORY_DATE, date);

                        //Check if the value already exists. If exists update it with the new steps count, if it does not exist, create it.
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, com.parse.ParseException e) {
                                if (e == null) {
                                    if (parseObject.getInt(ParseConstants.ACTIVITY_HISTORY_STEPS) != value) {
                                        parseObject.put(ParseConstants.ACTIVITY_HISTORY_STEPS, value);
                                        parseObject.saveInBackground();
                                    }
                                } else {
                                    ParseObject activityHistory = new ParseObject(ParseConstants.CLASS_ACTIVITY_HISTORY);
                                    activityHistory.put(ParseConstants.KEY_USER_ID, parseUserId);
//                                    activityHistory.put("userId", ParseObject.createWithoutData(ParseConstants.CLASS_USER, parseUserId));

                                    activityHistory.put(ParseConstants.ACTIVITY_HISTORY_DATE, date);
                                    activityHistory.put(ParseConstants.ACTIVITY_HISTORY_STEPS, value);
                                    activityHistory.saveInBackground();
                                }
                            }
                        });

                    } catch (ParseException ex) {}
                }
            }
            catch (org.json.JSONException ex)
            {
                Log.d(TAG, ex.getMessage());
            }
        }
    }
}

