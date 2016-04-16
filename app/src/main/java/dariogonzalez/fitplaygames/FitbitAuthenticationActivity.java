package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.melnykov.fab.FloatingActionButton;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.FitbitApi;
import dariogonzalez.fitplaygames.classes.NamesIds;
import dariogonzalez.fitplaygames.utils.ComplexPreferences;
import dariogonzalez.fitplaygames.utils.FitbitHelper;


public class FitbitAuthenticationActivity extends ActionBarActivity {

    private static String TAG = FitbitAuthenticationActivity.class.getSimpleName();

    private WebView wvAuthorise;
    private OAuthService service;
    private Token requestToken;
    private Token accessToken;
//    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        wvAuthorise = (WebView) findViewById(R.id.wvAuthorise);
        wvAuthorise.getSettings().setJavaScriptEnabled(true);

//        fab = (FloatingActionButton) findViewById(R.id.goHomeFab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(FitbitAuthenticationActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

        wvAuthorise.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://localhost")) {
                    Uri uri = Uri.parse(url);
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    final Verifier v = new Verifier(verifier);

                    GetTokens(v);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        // Replace these with your own api key and secret

        service = new ServiceBuilder().provider(FitbitApi.class).apiKey(getString(R.string.fitbit_api_key))
                .apiSecret(getString(R.string.fitbit_api_secret))
                .callback(getString(R.string.fitbit_callback)).build();

        // network operation shouldn't run on main thread
        new Thread(new Runnable() {
            public void run() {
                requestToken = service.getRequestToken();
                final String authURL = service.getAuthorizationUrl(requestToken);

                // Webview nagivation should run on main thread again...
                wvAuthorise.post(new Runnable() {
                    @Override
                    public void run() {
                        wvAuthorise.loadUrl(authURL);
                    }
                });
            }
        }).start();
    }

    private void GetTokens(final Verifier v)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    accessToken = service.getAccessToken(requestToken, v);

                    FitbitAccountInfo ai = new FitbitAccountInfo(accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse());
                    ComplexPreferences cp = ComplexPreferences.getComplexPreferences(getBaseContext(), NamesIds.SHARED_PREFERENCES, MODE_PRIVATE);
                    cp.putObject(NamesIds.FITBIT_ACCOUNT_INFO, ai);
                    cp.commit();

                    //Save last 30 days of activity.
                    FitbitHelper fh = new FitbitHelper(getBaseContext());
                    fh.getUserLastMonthData();

                    Intent myIntent = new Intent(FitbitAuthenticationActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
                catch (Exception ex)
                {
                    String eee = ex.getMessage();
                    Log.i(TAG, ex.getMessage());
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitbit_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_skip) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FitbitAuthenticationActivity.this);
            builder.setMessage(R.string.fitbit_attention_alert_message)
                    .setTitle(R.string.fitbit_attention_alert)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(FitbitAuthenticationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
}
