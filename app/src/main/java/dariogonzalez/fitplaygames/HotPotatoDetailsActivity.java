package dariogonzalez.fitplaygames;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dariogonzalez.fitplaygames.Adapters.HotPotatoPlayersAdapter;
import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class HotPotatoDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, totalGameSteps, passes;
    private LinearLayout statsLayout;
    private HotPotatoChallenge mHotPotatoChallenge;
    private ListView playingFriendsList;
    private List<ChallengePlayerItem> users;

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
        statsLayout = (LinearLayout) findViewById(R.id.stats);

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
        startTime.setText(time);
        stepsGoal.setText(String.valueOf(mHotPotatoChallenge.getStepsGoal()));
        totalGameSteps.setText(String.valueOf(mHotPotatoChallenge.getTotalSteps()));
        passes.setText(String.valueOf(mHotPotatoChallenge.getTotalPasses()));

        if (mHotPotatoChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_PENDING) {
            statsLayout.setVisibility(View.GONE);
        }
    }

    private void getChallengePlayers() {

        ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mHotPotatoChallenge.getChallengeId());

        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> challenge, ParseException e) {
                if (e == null) {
                    if (challenge.size() > 0) {
                        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID, challenge.get(0));
                        query.whereNotEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_DECLINED);

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> challengePlayers, ParseException e) {
                                if (e == null) {
                                    for (final ParseObject challengePlayer : challengePlayers) {
                                        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER);
                                        ParseUser user = (ParseUser) challengePlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_ID);
                                        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject user, ParseException e) {
                                                ChallengePlayerItem player = new ChallengePlayerItem();
                                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                                Uri profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                                                player.setmImageUri(profilePicture);
                                                player.setmIsOwner(challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_OWNER));
                                                player.setmPasses(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES));
                                                player.setmStatus(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_STATUS));
                                                player.setmUserName(user.getString(ParseConstants.USER_USERNAME));
                                                player.setmChallengeObject(challenge.get(0));
                                                player.setmUserObject((ParseUser) user);
                                                users.add(player);
                                            }
                                        });

                                    }
                                    ArrayAdapter<ChallengePlayerItem> adapter = new HotPotatoPlayersAdapter(HotPotatoDetailsActivity.this, R.layout.row_hot_potato_players, users, mHotPotatoChallenge.getStepsGoal(), challenge.get(0).getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS));
                                    playingFriendsList.setAdapter(adapter);

                                } else {
                                    Log.d("TEST", "Error: " + e.toString());
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
