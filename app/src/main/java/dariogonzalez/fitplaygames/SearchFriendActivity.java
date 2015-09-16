package dariogonzalez.fitplaygames;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.UserRowAdapter;
import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class SearchFriendActivity extends AppCompatActivity {
    private List<FriendListItem> mSearchFriendList = new ArrayList<FriendListItem>();
    private ListView searchResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);


        setProgressBarIndeterminateVisibility(true);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_friends);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 2)
                {
                    mSearchFriendList.clear();
                    populateListView();
                    return false;
                }


                final ParseUser userObject = ParseUser.getCurrentUser();
                final String userId = userObject.getObjectId();

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereStartsWith(ParseConstants.USER_USERNAME, newText);
                query.whereNotEqualTo(ParseConstants.OBJECT_ID, userId);
                query.setLimit(25);
                query.include("activityHistoryPointer");
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            for (final ParseUser friendUser : list) {
                                mSearchFriendList.clear();
                                ParseObject userFriend = friendUser.getParseObject("ActivityHistory");
                                if (userFriend != null) {
                                    Log.d("TEST", "UserFriend: " + userFriend.getInt(ParseConstants.USER_FRIENDS_STATUS));
                                }

                                List<ParseQuery<ParseObject>> queries = new ArrayList<>();

                                // Double check that the user doesn't already have a friend request history
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);
                                query1.whereEqualTo("UserObject", userObject);
                                query1.whereEqualTo("FriendObject", friendUser);

                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);
                                query2.whereEqualTo("UserObject", friendUser);
                                query2.whereEqualTo("FriendObject", userObject);

                                queries.add(query1);
                                queries.add(query2);

                                ParseQuery<ParseObject> friendQuery = ParseQuery.or(queries);
                                friendQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> friendList, ParseException e) {
                                        // If there is no userfriends table record or the friend request hasn't been approved or declined
                                        boolean includeUser = false;
                                        int userFriendStatus = -1;
                                        if (friendList.size() == 0) {
                                            includeUser = true;
                                        } else {
                                            for (ParseObject friendRecord : friendList) {
                                                int friendStatusId = friendRecord.getInt(ParseConstants.USER_FRIENDS_STATUS);
                                                if (friendStatusId != ParseConstants.FRIEND_STATUS_ACCEPTED &&
                                                        friendStatusId != ParseConstants.FRIEND_STATUS_DECLINED) {
                                                    includeUser = true;
                                                    userFriendStatus = friendRecord.getInt(ParseConstants.USER_FRIENDS_STATUS);
                                                }
                                            }
                                        }

                                        if (includeUser) {
                                            ParseFile file = friendUser.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                            mSearchFriendList.add(new FriendListItem(
                                                    friendUser.getString(ParseConstants.USER_USERNAME),
                                                    R.drawable.ic_user, fileUri,
                                                    userId,
                                                    friendUser.getObjectId(),
                                                    100,
                                                    userObject,
                                                    friendUser,
                                                    userFriendStatus,
                                                    ""));
                                        }
                                        populateListView();
                                    }
                                });
                            }
                        }
                    }
                });


                return false;
            }
        });
    }

    private void populateListView() {
        boolean isInvite = true;
        ArrayAdapter<FriendListItem> adapter = new UserRowAdapter(this, R.layout.row_user, mSearchFriendList, isInvite);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);
        searchResultListView.setAdapter(adapter);
    }

}
