package dariogonzalez.fitplaygames;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.UserRowAdapter;
import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class InviteFriendsActivity extends AppCompatActivity {

    private List<FriendListItem> mSearchFriendList = new ArrayList<FriendListItem>();
    ListView searchResultListView;
    ParseObject mChallengeObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        Intent intent = getIntent();
        String challengeId = intent.getStringExtra(ParseConstants.CHALLENGE_CHALLENGE_ID);
        if (challengeId.length() > 0)
        {
            try {
                mChallengeObject = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGES).get(challengeId);
            } catch (ParseException ex)
            {
                //TODO: handle this error...
            }
        }

        //TODO: also need to check for FriendObject = ParseUser.getCurrentUser(), OR clause, need to research how to do this with Parse.com
        final ParseUser userObject = ParseUser.getCurrentUser();
        final String userId = userObject.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);
        query.whereNotEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_DECLINED);
        query.whereNotEqualTo(ParseConstants.KEY_USER_ID, userId);
        query.whereEqualTo(ParseConstants.USER_OBJECT, userObject);
        query.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    mSearchFriendList.clear();
                    for (ParseObject ff : list) {
                        try {
                            ParseUser friend = ff.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                            ParseFile file = friend.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                            mSearchFriendList.add(new FriendListItem(friend.getString(ParseConstants.USER_USERNAME), R.drawable.ic_user, fileUri,
                                    userObject,
                                    friend,
                                    0,
                                    ""));

                        } catch (ParseException ex) {
                        }

                    }
                    populateListView();
                }
            }
        });
    }

    private void populateListView() {
        boolean isInvite = true;
        ArrayAdapter<FriendListItem> adapter = new UserRowAdapter(this, R.layout.row_user, mSearchFriendList, isInvite);
        searchResultListView = (ListView) findViewById(R.id.invite_friends_list_view);
        searchResultListView.setAdapter(adapter);
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
