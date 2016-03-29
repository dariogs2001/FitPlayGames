package dariogonzalez.fitplaygames.LeaderBoard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import dariogonzalez.fitplaygames.MainActivity;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class LeadboardActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private LeaderboardPageAdapter mSectionsPagerAdapter;
    private int challengeType = ChallengeTypeConstants.HOT_POTATO;



    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            challengeType = bundle.getInt("challengeType");
            if (challengeType == ChallengeTypeConstants.HOT_POTATO) {
                super.setTheme(R.style.HotPotatoTheme);
                super.setTitle(getString(R.string.hot_potato_leaderboard));
            }
            else if (challengeType == ChallengeTypeConstants.CROWN) {
                super.setTheme(R.style.CaptureTheCrownTheme);
                super.setTitle(getString(R.string.crown_leaderboard));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.capture_the_crown_dark));
                }
            }
        }
        setContentView(R.layout.activity_leader_board_new);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new LeaderboardPageAdapter(this, getSupportFragmentManager());
        mSectionsPagerAdapter.ChallengeType = challengeType;

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // Finish is usually bad practice but it makes sense here to correctly go back to the search friends page and persist the search data and list
            finish();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
