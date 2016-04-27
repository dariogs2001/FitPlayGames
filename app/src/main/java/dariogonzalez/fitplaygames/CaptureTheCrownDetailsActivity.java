package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import dariogonzalez.fitplaygames.Adapters.CaptureTheCrownPlayersAdapter;
import dariogonzalez.fitplaygames.classes.CaptureTheCrownChallenge;
import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by ChristensenKC on 1/27/2016.
 */
public class CaptureTheCrownDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, averageCrownTime, captures, endDayDate;
    private LinearLayout statsLayout, creationDetails, endDateLayout;
    private CaptureTheCrownChallenge mCaptureTheCrownChallenge;
    private ListView playingFriendsList;
    private List<ChallengePlayerItem> users;
    private com.melnykov.fab.FloatingActionButton mCancelAction;
    private Uri profilePicture;

    private int mState = 0;
    private int mAverageCrownTime = 0;
    private int mTotalCaptures = 0;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_the_crown_details);
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_TRADITIONAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setMessage(getResources().getString(R.string.challenge_refreshing));

        challengeName = (TextView) findViewById(R.id.challenge_name);
        startDate = (TextView) findViewById(R.id.start_day);
        startTime = (TextView) findViewById(R.id.start_time);
        stepsGoal = (TextView) findViewById(R.id.steps_goal);
        averageCrownTime = (TextView) findViewById(R.id.average_crown_time_tv);
        captures = (TextView) findViewById(R.id.captures_value);
        statsLayout = (LinearLayout) findViewById(R.id.stats);
        mCancelAction = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.cancel_action);
        playingFriendsList = (ListView) findViewById(R.id.playing_friends_listview);
        creationDetails = (LinearLayout) findViewById(R.id.creation_details);
        endDateLayout = (LinearLayout) findViewById(R.id.end_day_layout);
        endDayDate = (TextView) findViewById(R.id.end_day_date);

        users = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCaptureTheCrownChallenge = extras.getParcelable("game-details");
            setChallengeDetails();
        }

        mCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(CaptureTheCrownDetailsActivity.this)
                        .setTitle("Cancel Challenge")
                        .setMessage("Are you sure you want to Cancel this Challenge?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mCaptureTheCrownChallenge.setChallengeStatusType(ParseConstants.CHALLENGE_STATUS_CANCELLED);
                                mCaptureTheCrownChallenge.updateChallengeStatusInDatabase(mCaptureTheCrownChallenge.getChallengeId(), mCaptureTheCrownChallenge.getChallengeStatusType());

                                Intent intent = new Intent(CaptureTheCrownDetailsActivity.this, MainActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_capture_the_crown_details, menu);
        if (mState == 1)
        {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getItemId() != R.id.action_message) {
                    menu.getItem(i).setVisible(false);
                }
            }
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
            Log.d("Updated Challenge: ", "true");
        }

        return true;
    }

    private void setChallengeDetails() {
        getChallengePlayers();
        challengeName.setText(mCaptureTheCrownChallenge.getUserChallengeName());
        Map map = mCaptureTheCrownChallenge.splitDateAndTime(mCaptureTheCrownChallenge.getStartDate());
        String date = (String) map.get("date");
        String time = (String) map.get("time");
        startDate.setText(date);
        startTime.setText(time);
        stepsGoal.setText(String.valueOf(mCaptureTheCrownChallenge.getStepsGoal()));
        averageCrownTime.setText("0");
        captures.setText(String.valueOf(mCaptureTheCrownChallenge.getTotalCaptures()));

        if (mCaptureTheCrownChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_PENDING) {
            statsLayout.setVisibility(View.GONE);

            ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
            //For some reason the challenge object is null, so I have to get it in order to get the value I need here... :(
            challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mCaptureTheCrownChallenge.getChallengeId());
            challengeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null && parseObject != null) {
                        ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, parseObject);
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

        if (mCaptureTheCrownChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_FINISHED) {
            creationDetails.setVisibility(View.GONE);
            endDateLayout.setVisibility(View.VISIBLE);
            Date endDate = mCaptureTheCrownChallenge.getEndDate();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            endDayDate.setText(dateFormat.format(endDate));
            mState = 1; // setting state
            invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
        }
    }

    private void getChallengePlayers() {

        ParseQuery<ParseObject> challengeQuery = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mCaptureTheCrownChallenge.getChallengeId());
        challengeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject challenge, ParseException e) {
                if (e == null) {
                    if (challenge != null)
                    {
                        //For some reason this value has not been set.
                        if (mCaptureTheCrownChallenge.getChallengeObject() == null) {
                            mCaptureTheCrownChallenge.setChallengeObject(challenge);
                        }

                        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
                        if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_PLAYING || challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_FINISHED)
                        {
                            query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
                        }
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> challengePlayers, ParseException e) {
                                if (e == null) {
                                    for (final ParseObject challengePlayer : challengePlayers) {
                                        ParseUser user = challengePlayer.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                                        mAverageCrownTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject user, ParseException e) {
                                                final ChallengePlayerItem player = new ChallengePlayerItem();
                                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                                if (mCaptureTheCrownChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_PENDING) {
                                                    profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                                                    player.setmImageUri(profilePicture);
                                                }
                                                if (mCaptureTheCrownChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_PLAYING) {
                                                    profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                                                    player.setmImageUri(profilePicture);
                                                }
                                                if (mCaptureTheCrownChallenge.getChallengeStatusType() == ParseConstants.CHALLENGE_STATUS_FINISHED) {
                                                    if (challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_WINNER)) {
                                                        profilePicture = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                                                "://" + getResources().getResourcePackageName(R.drawable.crown_47)
                                                                + '/' + getResources().getResourceTypeName(R.drawable.crown_47) + '/' + getResources().getResourceEntryName(R.drawable.crown_47));
                                                        player.setmImageUri(profilePicture);
                                                    } else {
                                                        profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                                                        player.setmImageUri(profilePicture);
                                                    }
                                                }
                                                player.setmIsOwner(challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_OWNER));
                                                player.setmPasses(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES));
                                                player.setmPlayerAverageHoldingTime(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME));
                                                player.setmStatus(challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_STATUS));
                                                player.setmUserName(user.getString(ParseConstants.USER_USERNAME));
                                                player.setmChallengeObject(challenge);
                                                player.setmUserObject((ParseUser) user);
                                                // This is to get the step progression
                                                ParseQuery<ParseObject> challengeEventQuery1 = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_EVENTS);
                                                challengeEventQuery1.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
                                                challengeEventQuery1.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                                                challengeEventQuery1.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING);

                                                ParseQuery<ParseObject> challengeEventQuery2 = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_EVENTS);
                                                challengeEventQuery2.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
                                                challengeEventQuery2.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
                                                challengeEventQuery2.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS, ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_CROWN);

                                                List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                                                queries.add(challengeEventQuery1);
                                                queries.add(challengeEventQuery2);

                                                ParseQuery<ParseObject> challengeEventQuery = ParseQuery.or(queries);

                                                challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> list, ParseException e) {
                                                        if (e == null) {
                                                            if (list.size() > 0) {
                                                                player.setmSteps(list.get(0).getLong(ParseConstants.CHALLENGE_EVENTS_STEP_PROGRESSION));
                                                                Log.d("good list", "Got here");
                                                            } else {
                                                                player.setmSteps(0);
                                                                Log.d("bad list", "Got here");
                                                            }
                                                        }
                                                        if (challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_TURN)) {
                                                            users.add(0, player);
                                                        } else {
                                                            users.add(users.size(), player);
                                                        }
                                                        ArrayAdapter<ChallengePlayerItem> adapter = new CaptureTheCrownPlayersAdapter(CaptureTheCrownDetailsActivity.this, R.layout.row_capture_the_crown_players, users, mCaptureTheCrownChallenge.getStepsGoal(), challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS));
                                                        playingFriendsList.setAdapter(adapter);
                                                    }
                                                });

                                                mTotalCaptures += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_PASSES);
                                                captures.setText(String.valueOf(mTotalCaptures));
                                            }
                                        });

                                    }
                                    mAverageCrownTime = mAverageCrownTime / challengePlayers.size();
                                    int hours = mAverageCrownTime / 60;
                                    int minutes = mAverageCrownTime % 60;
                                    String crownTimeStr = ((hours > 0) ? hours + " Hr " : "") + minutes + " Min";
                                    averageCrownTime.setText(String.valueOf(crownTimeStr));

                                } else {
                                    Log.d("TEST", "Error: " + e.toString());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void updateChallenge() {
        progressDialog.show();
        new refreshGameTask().execute("");
    }

    private void updateChallengeDetails() {
        users.clear();
        mAverageCrownTime = 0;
        mTotalCaptures = 0;
        ParseQuery<ParseObject> getChallengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
        getChallengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mCaptureTheCrownChallenge.getChallengeId());
        getChallengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    CaptureTheCrownChallenge updatedCaptureTheCrownChallenge = new CaptureTheCrownChallenge(ChallengeTypeConstants.CROWN);
                    updatedCaptureTheCrownChallenge.setAttributesFromParseObject(list.get(0), getBaseContext());
                    mCaptureTheCrownChallenge = updatedCaptureTheCrownChallenge;
                    setChallengeDetails();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void openChatDialogue() {
        String mChallengeId = mCaptureTheCrownChallenge.getChallengeId();
        Intent intent = new Intent(CaptureTheCrownDetailsActivity.this, MainChatActivity.class);
        intent.putExtra(ParseConstants.OBJECT_ID, mChallengeId);
        startActivity(intent);
    }

    private class refreshGameTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... params) {
            ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
            challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, mCaptureTheCrownChallenge.getChallengeId());
            challengeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject challenge, ParseException e) {
                    if (e == null && challenge != null) {
                        ParseQuery<ParseObject> challengePlayerQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
                        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, ParseUser.getCurrentUser());
                        challengePlayerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject challengePlayer, ParseException e) {
                                if (e == null && challengePlayer != null) {
                                    ParentChallenge.updateChallenge(challenge, challengePlayer);
                                }
                            }
                        });
                    }
                }
            });

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            updateChallengeDetails();
        }
    }
}
