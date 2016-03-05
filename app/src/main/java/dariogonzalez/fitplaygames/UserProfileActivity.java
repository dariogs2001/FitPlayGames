package dariogonzalez.fitplaygames;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import dariogonzalez.fitplaygames.classes.ParseConstants;

public class UserProfileActivity extends AppCompatActivity {

    private String userId;
    private boolean cameFromSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // This is just used so that it will know how to handle the back button
            cameFromSearch = extras.getBoolean("cameFromSearch");
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            final MainProfileFragment fragment = (MainProfileFragment) fragmentManager.findFragmentById(R.id.profile_fragment);
            if (actionBar != null) {
                actionBar.setTitle(extras.getString("username"));
            }
            String userId = extras.getString("userId");
            fragment.setUserData(userId);
            boolean isFriend = extras.getBoolean("isFriend", false);
            fragment.setIsFriend(isFriend);
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo(ParseConstants.OBJECT_ID, userId);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            ParseUser user = list.get(0);
                            fragment.getAnalyticalData(user);
                        }
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
            if ( ! cameFromSearch) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            else {
                // Finish is usually bad practice but it makes sense here to correctly go back to the search friends page and persist the search data and list
                finish();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}