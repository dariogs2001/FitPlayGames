package dariogonzalez.fitplaygames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.Utils;

public class ChooseChallengeActivity extends AppCompatActivity {

    private List<ParentChallenge> parentChallengeList;

    private void initializeData(){
        parentChallengeList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_challenge);

        initializeData();

        Button button = (Button) findViewById(R.id.hot_potato);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.trackData(ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO);
                Intent intent = new Intent(ChooseChallengeActivity.this, HotPotatoCreateActivity.class);
                startActivity(intent);
            }
        });

        Button testPush = (Button) findViewById(R.id.test_push);
        testPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParentChallenge parentChallenge = new ParentChallenge() {
                    @Override
                    public void sendPushNotification(ParseUser user) {
                        super.sendPushNotification(user);
                    }

                };
                parentChallenge.sendPushNotification(ParseUser.getCurrentUser());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_challenge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
