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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dariogonzalez.fitplaygames.classes.ParseConstants;

public class HotPotatoChallengeActivity extends AppCompatActivity {

    private Spinner stepSpinner, startTimeSpinner, startDaySpinner;
    private ImageButton mAddFriend;
    private EditText mChallengeName;
    private ListView mChallengeFriendsList;
    private String mChallengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_potato_challenge);


        mChallengeName = (EditText) findViewById(R.id.challenge_name_edit_text);
        startDaySpinner = (Spinner) findViewById(R.id.start_day_spinner);
        startTimeSpinner = (Spinner) findViewById(R.id.start_time_spinner);
        stepSpinner = (Spinner) findViewById(R.id.steps_spinner);
        mAddFriend = (ImageButton) findViewById(R.id.add_friend_button);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.challenge_steps_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stepSpinner.setAdapter(adapter1);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        Date date = new Date();
        ArrayList<String> dateArray = new ArrayList<>();
        dateArray.add(0, dateFormat.format(date));
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dateArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startDaySpinner.setAdapter(adapter2);

        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        Date date2 = new Date();
        ArrayList<String> dateArray2 = new ArrayList<>();
        dateArray2.add(0, dateFormat2.format(date2));
        ArrayAdapter adapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dateArray2);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(adapter3);


        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isReady = true;
                if (mChallengeName.getText().length() == 0 || stepSpinner.getSelectedItem().toString().equals("Select steps"))  isReady = false;

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
