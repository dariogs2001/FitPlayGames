package dariogonzalez.fitplaygames;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;

public class HotPotatoDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, totalGameSteps, passes;
    private HotPotatoChallenge mHotPotatoChallenge;
    private ListView playingFriendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_potato_details);

        challengeName = (TextView) findViewById(R.id.challenge_name);
        startDate = (TextView) findViewById(R.id.start_day);
        startTime = (TextView) findViewById(R.id.start_time);
        stepsGoal = (TextView) findViewById(R.id.steps_goal);
        totalGameSteps = (TextView) findViewById(R.id.total_game_steps_value);
        passes = (TextView) findViewById(R.id.passes_value);

        playingFriendsList = (ListView) findViewById(R.id.playing_friends_listview);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHotPotatoChallenge = extras.getParcelable("game-details");
            setGameDetails();
        }
    }

    private void setGameDetails() {
        challengeName.setText(mHotPotatoChallenge.getUserChallengeName());
        Map map = mHotPotatoChallenge.splitDateAndTime(mHotPotatoChallenge.getStartDate());
        String date = (String) map.get("date");
        String time = (String) map.get("time");
        startDate.setText(date);
        startTime.setText((time));
        stepsGoal.setText(String.valueOf(mHotPotatoChallenge.getStepsGoal()));
        totalGameSteps.setText(String.valueOf(mHotPotatoChallenge.getTotalSteps()));
        passes.setText(String.valueOf(mHotPotatoChallenge.getTotalPasses()));
    }

}
