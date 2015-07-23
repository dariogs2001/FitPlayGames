package dariogonzalez.fitplaygames.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.FitbitApi;
import dariogonzalez.fitplaygames.classes.NamesIds;

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
        if (mService == null)
        {
//            mService = new ServiceBuilder().provider(FitbitApi.class)
//                    .apiKey(Resources.getSystem().getString(R.string.fitbit_api_key))
//                    .apiSecret(Resources.getSystem().getString(R.string.fitbit_api_secret))
//                    .callback(Resources.getSystem().getString(R.string.fitbit_callback)).build();
            mService = new ServiceBuilder().provider(FitbitApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("http://localhost").build();
            mAccessToken = new Token(mUserInfo.getAccessToken(), mUserInfo.getSecret());
        }

        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        mService.signRequest(mAccessToken, request); // the access token from step
        // 4
        final Response response = request.send();
        final String result = response.getBody();
        return result;
    }

    public void getUserLastMonthData()
    {
        FitbitLastMonthTask task = new FitbitLastMonthTask();
        task.execute("https://api.fitbit.com/1/user/-/activities/steps/date/today/1m.json");

//        String result = executeQuery("https://api.fitbit.com/1/user/-/activities/steps/date/today/1m.json");
//        Log.d(TAG, "Result = " + result);
    }

    public boolean isFitbitUserAlive()
    {
        return (mUserInfo != null && mUserInfo.getAccessToken().length() > 0 && mUserInfo.getSecret().length() > 0);
    }

    public class FitbitLastMonthTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return executeQuery(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String result = s;
            Log.d(TAG, "Result = " + result);
        }
    }
}

