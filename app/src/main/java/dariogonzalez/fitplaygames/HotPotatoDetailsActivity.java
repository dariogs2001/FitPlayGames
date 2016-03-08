package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dariogonzalez.fitplaygames.Adapters.HotPotatoPlayersAdapter;
import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class HotPotatoDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, averagePotatoTime, passes, endDayDate;
    private LinearLayout statsLayout, creationDetails, endDateLayout;
    private HotPotatoChallenge mHotPotatoChallenge;
    private ListView playingFriendsList;
    private List<ChallengePlayerItem> users;
    private com.melnykov.fab.FloatingActionButton mCancelAction;
    private Uri profilePicture;
    private Boolean isFinsihed = false;

    private int mState = 0;
    private int mAveragePotatoTime = 0;
    private int mTotalPasses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_potato_details);

        challengeName = (TextView) findViewById(R.id.challenge_name);
        startDate = (TextView) findViewById(R.id.start_day);
        startTime = (TextView) findViewById(R.id.start_time);
        stepsGoal = (TextView) findViewById(R.id.steps_goal);
        averagePotatoTime = (TextView) findViewById(R.id.average_potato_time_tv);
        passes = (TextView) findViewById(R.id.passes_value);
        statsLayout = (LinearLayout) findViewById(R.id.stats);
        creationDetails = (LinearLayout) findViewById(R.id.creation_details);
        endDateLayout = (LinearLayout) findViewById(R.id.end_day_layout);
        endDayDate = (TextView) findViewById(R.id.end_day_date);

        mCancelAction = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.cancel_action);

        playingFriendsList = (ListView) findViewById(R.id.playing_friends_listview);

        users = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHotPotatoChallenge = extras.getParcelable("game-details");
            setChallengeDetails();
        }

        mCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(HotPotatoDetailsActivity.this)
                        .setTitle("Cancel Challenge")
                        .setMessage("Are you sure you want to Cancel this Challenge?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mHotPotatoChallenge.setChallengeStatusType(ParseConstants.CHALLENGE_STATUS_CANCELLED);
                                mHotPotatoChallenge.updateChallengeStatusInDatabase(mHotPotatoChallenge.getChallengeId(), mHotPotatoChallenge.getChallengeStatusType());

                                Intent intent = new Intent(HotPotatoDetailsActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hot_potato_details, menu);
        if (mState == 1)
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        else if (id == R.id.action_message) {
            openChatDialogue();
        }
        else if (id == R.id.action_refresh) {
            updateChallenge();
        }
            return true;
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
        passes.setText(String.valueOf(mHotPotatoChallenge.getTotalPasses()));

        if (mHotPotatoChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_PENDING) {
            statsLayout.setVisibility(View.GONE);

            ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
            //For some reason the challenge object is null, so I have to get it in order to get the value I need here... :(
            challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mHotPotatoChallenge.getChallengeId());
            challengeQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> list, final ParseException e) {
                    if (e == null && !list.isEmpty()) {
                        ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, list.get(0));
                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());
                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_OWNER, true);
                        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(final List<ParseObject> list, final ParseException e) {
                                if (e == null && !list.isEmpty()) {
                                    mCancelAction.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        }

        if (mHotPotatoChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_FINISHED) {
            creationDetails.setVisibility(View.GONE);
            endDateLayout.setVisibility(View.VISIBLE);
            Date endDate = mHotPotatoChallenge.getEndDate();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            endDayDate.setText(dateFormat.format(endDate));
            mState = 1; // setting state
            invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
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
                        //For some reason this value has not been set.
                        if (mHotPotatoChallenge.getChallengeObject() == null)
                        {
                            mHotPotatoChallenge.setChallengeObject(challenge.get(0));
                        }
//                        mHotPotatoChallenge.refresh();

                        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge.get(0));
                        query.whereNotEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_DECLINED);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> challengePlayers, ParseException e) {
                                if (e == null) {
                                    for (final ParseObject challengePlayer : challengePlayers) {
                                        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER);
                                        ParseUser user = (ParseUser) challengePlayer.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                                        mAveragePotatoTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject user, ParseException e) {
                                                final ChallengePlayerItem player = new ChallengePlayerItem();
                                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                                if (challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_TURN)) {
                                                    profilePicture = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                                            "://" + getResources().getResourcePackageName(R.drawable.potato_47)
                                                            + '/' + getResources().getResourceTypeName(R.drawable.potato_47) + '/' + getResources().getResourceEntryName(R.drawable.potato_47));
                                                    player.setmImageUri(profilePicture);
                                                }
                                                else if (mHotPotatoChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_FINISHED) {
                                                    if (challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_LOSER)) {
                                                        profilePicture = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                                                "://" + getResources().getResourcePackageName(R.drawable.firepotato)
                                                                + '/' + getResources().getResourceTypeName(R.drawable.firepotato) + '/' + getResources().getResourceEntryName(R.drawable.firepotato));
                                                        player.setmImageUri(profilePicture);
                                                    }
                                                }
                                                else {
                                                profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                                                player.setmImageUri(profilePicture);}
                                                player.setmIsOwner(challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_OWNER));
                                                player.setmPasses(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES));
                                                player.setmPlayerAverageHoldingTime(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME));
                                                player.setmStatus(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_STATUS));
                                                player.setmUserName(user.getString(ParseConstants.USER_USERNAME));
                                                player.setmChallengeObject(challenge.get(0));
                                                player.setmUserObject((ParseUser) user);
                                                // This is to get the step progression
                                                ParseQuery<ParseObject> challengeEventQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_EVENTS);
                                                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, challengePlayer);
                                                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);
                                                challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> list, ParseException e) {
                                                        if (e == null) {
                                                            if (list.size() > 0) {
                                                                player.setmSteps(list.get(0).getLong(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION));
                                                            } else {
                                                                player.setmSteps(0);
                                                            }
                                                        }

                                                        if (challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_TURN)) {
                                                            users.add(0, player);
                                                        } else {
                                                            users.add(users.size(), player);
                                                        }

                                                        ArrayAdapter<ChallengePlayerItem> adapter = new HotPotatoPlayersAdapter(HotPotatoDetailsActivity.this,
                                                                                                                                R.layout.row_hot_potato_players,
                                                                                                                                users,
                                                                                                                                mHotPotatoChallenge.getStepsGoal(),
                                                                                                                                challenge.get(0).getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS));
                                                        playingFriendsList.setAdapter(adapter);
                                                    }
                                                });

                                                mTotalPasses += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                                passes.setText(String.valueOf(mTotalPasses));
                                            }
                                        });
                                    }
                                    mAveragePotatoTime = mAveragePotatoTime / challengePlayers.size();
                                    int hours = mAveragePotatoTime / 60;
                                    int minutes = mAveragePotatoTime % 60;
                                    String potatoTimeStr = ((hours > 0) ? hours + " Hr " : "") + minutes + " Min";
                                    averagePotatoTime.setText(String.valueOf(potatoTimeStr));
                                } else {
                                    Log.d("TEST", "Error: " + e.toString());
                                }
                            }
                        });
                    }
                } else {}
            }
        });
    }

    private void updateChallenge() {
        ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mHotPotatoChallenge.getChallengeId());
        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    final ParseObject challenge = list.get(0);
                    ParseQuery<ParseObject> challengePlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                    challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
                    challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());
                    challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e)
                        {
                            if (e == null && !list.isEmpty())
                            {
                                ParseObject challengePlayer = list.get(0);
                                ParentChallenge.updateChallenge(challenge, challengePlayer);
                            }
                        }
                    });
                }
            }
        });
    }

    private void openChatDialogue() {
        String mChallengeId = mHotPotatoChallenge.getChallengeId();
        Intent intent = new Intent(HotPotatoDetailsActivity.this, MainChatActivity.class);
        intent.putExtra(ParseConstants.OBJECT_ID, mChallengeId);
        startActivity(intent);
    }
}
