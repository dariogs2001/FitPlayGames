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
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);
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
                            mSearchFriendList.add(new FriendListItem(friend.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri,
                                    userObject.getObjectId(),
                                    friend.getObjectId(),
                                    userObject,
                                    friend));

                        } catch (ParseException ex) {
                        }

                    }
                    populateListView();
                }
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<FriendListItem> adapter = new FriendSearchAdapterList(this, R.layout.friends_search_item);
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

    private class FriendSearchAdapterList extends ArrayAdapter<FriendListItem> {
        Context mContext;
        public FriendSearchAdapterList(Context context, int resource) {
            super(context, resource, mSearchFriendList);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = LayoutInflater.from(mContext).inflate(R.layout.friends_search_item, parent, false);
            }
            final FriendListItem current = mSearchFriendList.get(position);

            TextView userNameTextView = (TextView) itemView.findViewById(R.id.user_name);
            userNameTextView.setText(current.getUserName());
            ImageView imageView = (ImageView) itemView.findViewById(R.id.user_thumbnail);
            Uri profilePicture = current.getImageUri();
            if (profilePicture != null)
            {
                Picasso.with(mContext).load(profilePicture.toString()).into(imageView);
            }
            else
            {
                imageView.setImageResource(current.getIconId());
            }

            final Button inviteButton =  (Button) itemView.findViewById(R.id.btn_invite);
            final Button sentButton =  (Button) itemView.findViewById(R.id.btn_sent);

            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Adding invitation into the DB
                    ParseObject newObject = new ParseObject(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                    newObject.put(ParseConstants.CHALLENGE_OBJECT, mChallengeObject);
                    newObject.put(ParseConstants.USER_OBJECT, current.getFriendObject());
                    newObject.put(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING);
                    newObject.put(ParseConstants.CHALLENGE_PLAYER_DATE_JOINED, new Date()); //TODO: need a real date here...will be updated when the user accept the invitation

                    newObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                inviteButton.setVisibility(View.GONE);
                                sentButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
            return itemView;
        }
    }
}
