package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dariogonzalez.fitplaygames.Friends.SearchFriendsFragment;
import dariogonzalez.fitplaygames.classes.CaptureTheCrownChallenge;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.utils.Utils;

/**
 * Created by ChristensenKC on 1/27/2016.
 */
public class CaptureTheCrownCreateActivity extends AppCompatActivity {

    private Spinner stepSpinner, startTimeSpinner, startDaySpinner;
    private EditText mChallengeName;
    private ListView mChallengeFriendsList;
    private String mChallengeId;
    private Button mCreateGameButton, mCancelButton;
    private SearchFriendsFragment mSearchFriendsFragment;
    private CaptureTheCrownChallenge mCaptureTheCrownChallenge;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_the_crown_create);

        mCaptureTheCrownChallenge = new CaptureTheCrownChallenge(ChallengeTypeConstants.CROWN);
        mChallengeName = (EditText) findViewById(R.id.challenge_name_edit_text);
        startDaySpinner = (Spinner) findViewById(R.id.start_day_spinner);
        startTimeSpinner = (Spinner) findViewById(R.id.start_time_spinner);
        stepSpinner = (Spinner) findViewById(R.id.steps_spinner);
        mCreateGameButton = (Button) findViewById(R.id.create_game_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);


        //mChallengeName.setText(mCaptureTheCrownChallenge.getDefaultChallengeName());
        mChallengeName.setSelection(mChallengeName.getText().length(), mChallengeName.getText().length());


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.challenge_steps_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stepSpinner.setAdapter(adapter1);

        mSearchFriendsFragment = (SearchFriendsFragment) getFragmentManager().findFragmentById(R.id.search_friends_fragment);


        DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.add(Calendar.DATE, 1);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);
        Date date = cal.getTime();
        ArrayList<String> dateArray = new ArrayList<>();
        dateArray.add(0, dateFormat.format(date));
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dateArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startDaySpinner.setAdapter(adapter2);

        startDaySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showDateDialog();
                    return true;
                }
                return true;
            }
        });


        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        Calendar cal2 = Calendar.getInstance(Locale.getDefault());
        mHour = 6;
        mMinute = 0;
        cal2.set(Calendar.HOUR_OF_DAY, mHour);
        cal2.set(Calendar.MINUTE, mMinute);
        Date date2 = cal2.getTime();
        ArrayList<String> dateArray2 = new ArrayList<>();
        dateArray2.add(0, dateFormat2.format(date2));
        ArrayAdapter adapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dateArray2);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(adapter3);

        startTimeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showTimeDialog();
                    return true;
                }
                return false;
            }
        });


        mCreateGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.trackData(ParseConstants.KEY_ANALYTICS_CREATE_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_CREATE_GAME_HOT_POTATO);

                List<UserListItem> selectedFriends = mSearchFriendsFragment.getSelectedFriends();

                if (selectedFriends.size() > 0) {
                    for (int i = 0; i < selectedFriends.size(); i++) {
                        ParseUser user;
                        if (selectedFriends.get(i).getmFriendObject().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            user = selectedFriends.get(i).getmUserObject();
                            mCaptureTheCrownChallenge.getPlayerObjects().add(user);
                        } else {
                            user = selectedFriends.get(i).getmFriendObject();
                            mCaptureTheCrownChallenge.getPlayerObjects().add(user);
                        }
                    }
                }

                boolean isReady = true;
                if (mChallengeName.getText().length() < 1 || stepSpinner.getSelectedItemPosition() == 0)
                    isReady = false;

                if (!isReady) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureTheCrownCreateActivity.this);
                    builder.setMessage(R.string.challenge_error_message)
                            .setTitle(R.string.challenge_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (mChallengeId == null || mChallengeId.length() == 0) {
                    String challengeName = mChallengeName.getText().toString();
                    //Create challenge
                    mCaptureTheCrownChallenge.createChallenge(ParseUser.getCurrentUser(), mChallengeName.getText().toString(), Integer.parseInt(stepSpinner.getSelectedItem().toString()), new Date(), mCaptureTheCrownChallenge.generateRandomEndDate(1000, 2), new ParentChallenge.GetObjectIdCallback() {
                        @Override
                        public void done(String objectId) {
                            Intent intent = new Intent(CaptureTheCrownCreateActivity.this, CaptureTheCrownDetailsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putParcelable("game-details", mCaptureTheCrownChallenge);
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                    }, selectedFriends.size() + 1);
                } else {
                    Intent intent = new Intent(CaptureTheCrownCreateActivity.this, InviteFriendsActivity.class);
                    intent.putExtra(ParseConstants.CHALLENGE_CHALLENGE_ID, mChallengeId);
                    startActivity(intent);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.trackData(ParseConstants.KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO);

                NavUtils.navigateUpFromSameTask(CaptureTheCrownCreateActivity.this);
            }
        });


    }


    public void showDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            mYear = year;
            mMonth = month;
            mDay = day;

            DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DATE, day);
            Date date = cal.getTime();
            ArrayList<String> dateArray = new ArrayList<>();
            dateArray.add(0, dateFormat.format(date));
            ArrayAdapter adapter2 = new ArrayAdapter(CaptureTheCrownCreateActivity.this, android.R.layout.simple_spinner_item, dateArray);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startDaySpinner.setAdapter(adapter2);
        }
    };

    public void showTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerListener, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mHour = hour;
            mMinute = minute;

            DateFormat dateFormat2 = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            Calendar cal2 = Calendar.getInstance(Locale.getDefault());
//            int amOrPm = Calendar.AM;
//            if (hour > 12) {
//                hour = hour - 12;
//                amOrPm = Calendar.PM;
//            }
            cal2.set(Calendar.HOUR_OF_DAY, hour);
            cal2.set(Calendar.MINUTE, minute);
//            cal2.set(Calendar.AM_PM, amOrPm);
            Date date2 = cal2.getTime();
            ArrayList<String> dateArray2 = new ArrayList<>();
            dateArray2.add(0, dateFormat2.format(date2));
            ArrayAdapter adapter3 = new ArrayAdapter(CaptureTheCrownCreateActivity.this, android.R.layout.simple_spinner_item, dateArray2);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startTimeSpinner.setAdapter(adapter3);
        }
    };

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
