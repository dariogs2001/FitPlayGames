package dariogonzalez.fitplaygames;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;

/**
 * Created by ChristensenKC on 1/27/2016.
 */
public class CaptureTheCrownDetailsActivity extends AppCompatActivity {

    private TextView challengeName, startDate, startTime, stepsGoal, averagePotatoTime, passes;
    private LinearLayout statsLayout;
    private HotPotatoChallenge mHotPotatoChallenge;
    private ListView playingFriendsList;
    private List<ChallengePlayerItem> users;
    private com.melnykov.fab.FloatingActionButton mCancelAction;

    private int mTotalPasses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_the_crown_details);

        challengeName = (TextView) findViewById(R.id.challenge_name);
        startDate = (TextView) findViewById(R.id.start_day);
        startTime = (TextView) findViewById(R.id.start_time);
        stepsGoal = (TextView) findViewById(R.id.steps_goal);
        averagePotatoTime = (TextView) findViewById(R.id.average_potato_time_tv);
        passes = (TextView) findViewById(R.id.passes_value);
        statsLayout = (LinearLayout) findViewById(R.id.stats);
        mCancelAction = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.cancel_action);

    }
}
