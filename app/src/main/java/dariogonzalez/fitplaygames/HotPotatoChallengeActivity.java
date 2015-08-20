package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

import dariogonzalez.fitplaygames.classes.ParseConstants;

public class HotPotatoChallengeActivity extends AppCompatActivity {

    private Spinner mSteps;
    private ImageView mAddFriend;
    private EditText mChallengeName;
    private ListView mChallengeFriendsList;
    private Button mStartBtn;
    private Button mCancelBtn;

    private String mChallengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_potato_challenge);


        mChallengeName = (EditText) findViewById(R.id.challenge_name_edit_text);
        mAddFriend = (ImageView)findViewById(R.id.add_friend_image_view);
        mSteps = (Spinner) findViewById(R.id.steps_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.challenge_steps_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSteps.setAdapter(adapter);
        mChallengeFriendsList = (ListView) findViewById(R.id.challenge_friends_list_view);
        mStartBtn = (Button) findViewById(R.id.start_challenge_button);
        mCancelBtn = (Button) findViewById(R.id.cancel_challenge_button);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isReady = true;
                if (mChallengeName.getText().length() == 0 || mSteps.getSelectedItem().toString().equals("Select steps"))  isReady = false;

                if (!isReady) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotPotatoChallengeActivity.this);
                    builder.setMessage(R.string.challenge_error_message)
                            .setTitle(R.string.challenge_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (mChallengeId == null || mChallengeId.length() == 0) {
                    String challengeName =  mChallengeName.getText().toString();
                    //Create challenge
                    final ParseObject object = new ParseObject(ParseConstants.CLASS_CHALLENGES);
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_NAME, challengeName);
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_TYPE, 0); //TODO: need a value for this
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_STATUS, ParseConstants.CHALLENGE_STATUS_PROCESSING);
                    object.put(ParseConstants.CHALLENGE_DAILY_STEPS_GOAL, Integer.valueOf(mSteps.getSelectedItem().toString()));
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_PASS, 0); //TODO: need a value for this
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_START, new Date()); //TODO: need a value for this
                    object.put(ParseConstants.CHALLENGE_CHALLENGE_END, new Date()); //TODO: need a value for this
                    object.put(ParseConstants.CHALLENGE_NUMBER_OF_DAYS, 5); //TODO: need a value for this
                    object.put(ParseConstants.CHALLENGE_ACTIVE_MINUTES_GOAL, 0);

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mChallengeId = object.getObjectId();
                                Intent intent = new Intent(HotPotatoChallengeActivity.this, InviteFriendsActivity.class);
                                intent.putExtra(ParseConstants.CHALLENGE_OBJECT, mChallengeId);
                                startActivity(intent);
                            } else {
                                //TODO: show error message
                            }
                        }
                    });
                }
                else
                {
                    Intent intent = new Intent(HotPotatoChallengeActivity.this, InviteFriendsActivity.class);
                    intent.putExtra(ParseConstants.CHALLENGE_CHALLENGE_ID, mChallengeId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hot_potato_challenge, menu);
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
