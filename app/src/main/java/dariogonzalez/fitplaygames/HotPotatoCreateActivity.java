package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dariogonzalez.fitplaygames.Friends.SearchFriendsFragment;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.utils.Utils;

public class HotPotatoCreateActivity extends AppCompatActivity {

    private Spinner stepSpinner, startTimeSpinner, startDaySpinner;
    private EditText mChallengeName;
    private ListView mChallengeFriendsList;
    private String mChallengeId;
    private Button mCreateGameButton, mCancelButton;
    private SearchFriendsFragment mSearchFriendsFragment;
    private HotPotatoChallenge mHotPotatoChallenge;

    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_potato_create);

        mHotPotatoChallenge = new HotPotatoChallenge(ChallengeTypeConstants.HOT_POTATO);


        mChallengeName = (EditText) findViewById(R.id.challenge_name_edit_text);
        startDaySpinner = (Spinner) findViewById(R.id.start_day_spinner);
        startTimeSpinner = (Spinner) findViewById(R.id.start_time_spinner);
        stepSpinner = (Spinner) findViewById(R.id.steps_spinner);
        mCreateGameButton = (Button) findViewById(R.id.create_game_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);

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

        DateFormat dateFormat2 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
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

                if (mChallengeName.getText().length() < 1 || stepSpinner.getSelectedItemPosition() == 0 || selectedFriends.size() < 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotPotatoCreateActivity.this);
                    builder.setMessage(R.string.challenge_error_message)
                            .setTitle(R.string.challenge_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (selectedFriends.size() > 4)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotPotatoCreateActivity.this);
                    builder.setMessage(R.string.challenge_error_message_2)
                            .setTitle(R.string.challenge_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                for (UserListItem selectedFriend :selectedFriends)
                {
                    ParseUser user;
                    if (selectedFriend.getmFriendObject().getUsername().equals(ParseUser.getCurrentUser().getUsername()))
                    {
                        user = selectedFriend.getmUserObject();
                        mHotPotatoChallenge.getPlayerObjects().add(user);
                    } else {
                        user = selectedFriend.getmFriendObject();
                        mHotPotatoChallenge.getPlayerObjects().add(user);
                    }
                }

                if (mChallengeId == null || mChallengeId.length() == 0) {
                    //Create challenge

                    String startDateInfo = String.format("%s/%d %s", startDaySpinner.getSelectedItem().toString(), mYear, startTimeSpinner.getSelectedItem().toString());
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date startDate = new Date(startDateInfo);

                    mHotPotatoChallenge.createChallenge(ParseUser.getCurrentUser(),
                            mChallengeName.getText().toString(),
                            Integer.parseInt(stepSpinner.getSelectedItem().toString()),
                            startDate != null ? startDate : new Date(),
                            mHotPotatoChallenge.generateRandomEndDate(Integer.parseInt(stepSpinner.getSelectedItem().toString()), selectedFriends.size() + 1, startDate != null ? startDate : new Date()),
                            new ParentChallenge.GetObjectIdCallback() {
                        @Override
                        public void done(String objectId) {
                            Intent intent = new Intent(HotPotatoCreateActivity.this, HotPotatoDetailsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putParcelable("game-details", mHotPotatoChallenge);
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                    }, 1, selectedFriends.size() + 1);
                }
                else
                {
                    Intent intent = new Intent(HotPotatoCreateActivity.this, InviteFriendsActivity.class);
                    intent.putExtra(ParseConstants.CHALLENGE_CHALLENGE_ID, mChallengeId);
                    startActivity(intent);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.trackData(ParseConstants.KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_CANCEL_GAME_HOT_POTATO);

                NavUtils.navigateUpFromSameTask(HotPotatoCreateActivity.this);
            }
        });
    }

    public void showDateDialog()
    {
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
            ArrayAdapter adapter2 = new ArrayAdapter(HotPotatoCreateActivity.this, android.R.layout.simple_spinner_item, dateArray);
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

            DateFormat dateFormat2 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar cal2 = Calendar.getInstance(Locale.getDefault());

            cal2.set(Calendar.HOUR_OF_DAY, hour);
            cal2.set(Calendar.MINUTE, minute);
            Date date2 = cal2.getTime();
            ArrayList<String> dateArray2 = new ArrayList<>();
            dateArray2.add(0, dateFormat2.format(date2));
            ArrayAdapter adapter3 = new ArrayAdapter(HotPotatoCreateActivity.this, android.R.layout.simple_spinner_item, dateArray2);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startTimeSpinner.setAdapter(adapter3);
        }
    };
}
