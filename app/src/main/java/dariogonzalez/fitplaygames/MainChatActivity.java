package dariogonzalez.fitplaygames;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.Random;

import dariogonzalez.fitplaygames.Adapters.ChatListAdapter;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.utils.Utils;

/**
 * Created by ChristensenKC on 10/28/2015.
 */

public class MainChatActivity extends AppCompatActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://fitplaygames.firebaseio.com/";

    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    private LinearLayout progressBar;
    private LinearLayout emptyStateLayout;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        Utils.trackData(ParseConstants.KEY_ANALYTICS_CHAT, ParseConstants.KEY_ANALYTICS_CHAT);

        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        emptyStateLayout = (LinearLayout) findViewById(R.id.empty_state_chat);
        // Make sure we have a mUsername
        setupUsername();

        setTitle("Chat");
        Intent intent = getIntent();
        objectId = intent.getStringExtra(ParseConstants.OBJECT_ID);
        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(objectId);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView) findViewById(R.id.list);
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, mUsername);

        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void setupUsername() {
        ParseUser user = ParseUser.getCurrentUser();
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = user.getUsername();
        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername =  user.toString();
            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, new Date());
            notifyChallengeChatUsers(objectId);
            notifyFriendshipChatUser(objectId);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    public static void notifyFriendshipChatUser( String friendshipId) {

        final ParseUser userObject = ParseUser.getCurrentUser();
        final String userId = userObject.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);
        query.whereEqualTo(ParseConstants.OBJECT_ID, friendshipId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject ff : list) {
                        try {
                            ParseUser friend = ff.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                            ParentChallenge.sendPushNotification(friend.getUsername() + " " + "has sent you a private message!", friend);
                        } catch (ParseException ex) {
                        }
                    }
                }
            }
        });
    }

    public static void notifyChallengeChatUsers(String challengeId) {
        final ParseUser userObject = ParseUser.getCurrentUser();
        final String userId = userObject.getObjectId();
        ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
        challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengeId);
        challengeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    final ParseObject challenge = list.get(0);
                    // Send Push notification to all players that are part of that challengeId except yourself
                    ParseQuery<ParseObject> getListOfAllPlayersQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                    getListOfAllPlayersQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, challenge);
                    getListOfAllPlayersQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
                    getListOfAllPlayersQuery.whereNotEqualTo(ParseConstants.USER_OBJECT, userObject);
                    getListOfAllPlayersQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> list, ParseException e) {
                            Log.d("List"," is null");
                            if (e == null && !list.isEmpty()) {
                                Log.d("test","you got here");
                                for(final ParseObject chatObject : list) {
                                    try {
                                        Log.d("ListPlayer", list.toString());
                                        ParseObject challenge = chatObject.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();
                                        ParseUser chatUser = chatObject.getParseUser(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);//.get(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT);
                                        ParentChallenge.sendPushNotification("You have a chat message waiting for you in '" + challenge.get(ParseConstants.CHALLENGE_CHALLENGE_NAME) + "'!", chatUser);
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
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

