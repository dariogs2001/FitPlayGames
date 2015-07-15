package dariogonzalez.activeandhealthy;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by Dario on 5/9/2015.
 */
public class FitbitApi extends DefaultApi10a {
     private static final String AUTHORIZATION_URL = "https://www.fitbit.com/oauth/authorize?oauth_token=%s";
    //private static final String AUTHORIZATION_URL = "https://www.fitbit.com/oauth/authenticate?oauth_token=%s";

    @Override
    public String getRequestTokenEndpoint() {
        return "https://api.fitbit.com/oauth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.fitbit.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZATION_URL, requestToken.getToken());
    }
}
