package dariogonzalez.fitplaygames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

        CardView hotPotatoBtn = (CardView) findViewById(R.id.hot_potato);
        int permission = ParseUser.getCurrentUser().getInt(ParseConstants.USER_PERMISSION);
        if (permission == ParseConstants.PERMISSION_ALL || permission == ParseConstants.PERMISSION_HOT_POTATO) {
            hotPotatoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.trackData(ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO);
                    Intent intent = new Intent(ChooseChallengeActivity.this, HotPotatoCreateActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            hotPotatoBtn.setVisibility(View.INVISIBLE);
        }

        CardView captureTheCrownBtn = (CardView) findViewById(R.id.capture_the_crown);
        if (permission == ParseConstants.PERMISSION_ALL || permission == ParseConstants.PERMISSION_CAPTURE_THE_CROWN) {
            captureTheCrownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.trackData(ParseConstants.KEY_ANALYTICS_SELECT_GAME_CAPTURE_CROWN, ParseConstants.KEY_ANALYTICS_SELECT_GAME_CAPTURE_CROWN);
                    Intent intent = new Intent(ChooseChallengeActivity.this, CaptureTheCrownCreateActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            captureTheCrownBtn.setVisibility(View.INVISIBLE);
        }
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
