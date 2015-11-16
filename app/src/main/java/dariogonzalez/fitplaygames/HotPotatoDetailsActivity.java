package dariogonzalez.fitplaygames;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dariogonzalez.fitplaygames.Adapters.HotPotatoPlayersAdapter;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class HotPotatoDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, totalGameSteps, passes;
    private HotPotatoChallenge mHotPotatoChallenge;
    private ListView playingFriendsList;
    private List<ParseObject> users;

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

        users = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHotPotatoChallenge = extras.getParcelable("game-details");
            setChallengeDetails();
        }
    }

    private void setChallengeDetails() {
        getChallengePlayers();
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

    private void getChallengePlayers() {

        ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
        Log.d("Challenge ID:",mHotPotatoChallenge.getChallengeId());
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mHotPotatoChallenge.getChallengeId());

        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID, list.get(0));

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> challengePlayers, ParseException e) {
                                if (e == null) {
                                    for (ParseObject challengePlayer : challengePlayers) {
                                        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER);
                                        ParseObject user = (ParseObject) challengePlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_ID);
                                        Log.d("TEST", "Where " + ParseConstants.OBJECT_ID + " = " + user.getObjectId());
                                        userQuery.whereEqualTo(ParseConstants.OBJECT_ID, user.getObjectId());
                                        userQuery.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> list, ParseException e) {
                                                Log.d("TEST", "size: " + list.size());
                                                if (e == null) {
                                                    if (list.size() > 0) {
                                                        Log.d("TEST", "Here breh");
                                                        users.add(list.get(0));
                                                        ArrayAdapter<ParseObject> adapter = new HotPotatoPlayersAdapter(HotPotatoDetailsActivity.this, R.layout.row_hot_potato_players, users, mHotPotatoChallenge.getStepsGoal());
                                                        playingFriendsList.setAdapter(adapter);
                                                    }
                                                } else {
                                                    Log.d("TEST", e.toString());
                                                }
                                            }
                                        });
                                    }

                                }
                                else {
                                    Log.d("TEST", "Erro: " + e.toString());
                                }
                            }
                        });
                    }
                }
                else {

                }
            }
        });

    }

}
