package dariogonzalez.fitplaygames;

import com.firebase.client.Firebase;

/**
 * Created by ChristensenKC on 10/28/2015.
 */
public class ChatApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

