package dariogonzalez.activeandhealthy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class FitbitAuthenticationActivity extends ActionBarActivity {

    WebView wvAuthorise;
    OAuthService service;
    Token requestToken;
    Token accessToken;

    String apiKey = "a2f813cf8c7420eff5629382ae6a25a4";
    String apiSecret = "0129ef7d53df74e1bb428fdaec8df9c1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        wvAuthorise = (WebView) findViewById(R.id.wvAuthorise);
        wvAuthorise.getSettings().setJavaScriptEnabled(true);

        wvAuthorise.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final String url2 = url;

                if(url.startsWith("http://localhost")) {
                    Uri uri = Uri.parse(url);
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    final Verifier v = new Verifier(verifier);

                    GetTokens(v);
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        // Replace these with your own api key and secret


        service = new ServiceBuilder().provider(FitbitApi.class).apiKey(apiKey).apiSecret(apiSecret).callback("http://localhost").build();

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
                try {
                    accessToken = service.getAccessToken(requestToken, v);

                    String eee = "Dario";

                    Intent myIntent = new Intent(FitbitAuthenticationActivity.this, MainActivity.class);
//                    myIntent.putExtra("accesstoken ", MainActivity.accessToken.getToken()); //Optional parameters
//                    myIntent.putExtra("secret", MainActivity.accessToken.getSecret()); //Optional parameters
                    FitbitAuthenticationActivity.this.startActivity(myIntent);
                } catch (Exception ex) {
                    String eee = ex.getMessage();
                }
            }
        }).start();

    }
}
