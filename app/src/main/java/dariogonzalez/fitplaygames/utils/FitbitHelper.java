package dariogonzalez.fitplaygames.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

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

    private static FitbitHelper mFitbitHelper;
    private static FitbitAccountInfo mUserInfo;

    private static OAuthService mService;
    private static Token mRequestToken;
    private static Token mAccessToken;

    public FitbitHelper getInstance()
    {
        if (mFitbitHelper == null) mFitbitHelper = new FitbitHelper();
        return  mFitbitHelper;
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

    public boolean isFitbitUserAlive()
    {
        return (mUserInfo != null && mUserInfo.getAccessToken().length() > 0 && mUserInfo.getSecret().length() > 0);
    }

    public class FitbitTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (mService == null)
            {
                mService = new ServiceBuilder().provider(FitbitApi.class).apiKey(Resources.getSystem().getString(R.string.fitbit_api_key))
                            .apiSecret(Resources.getSystem().getString(R.string.fitbit_api_secret))
                            .callback(Resources.getSystem().getString(R.string.fitbit_callback)).build();
                mAccessToken = new Token(mUserInfo.getAccessToken(), mUserInfo.getSecret());
            }

            OAuthRequest request = new OAuthRequest(Verb.GET, params[0]);
            mService.signRequest(mAccessToken, request); // the access token from step
            // 4
            final Response response = request.send();
            final String result = response.getBody();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}

