package dariogonzalez.fitplaygames;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.UserRowAdapter;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class SearchFriendActivity extends AppCompatActivity {
    private List<UserListItem> mSearchFriendList = new ArrayList<UserListItem>();
    private ListView searchResultListView;
    private LinearLayout noResultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);


        setProgressBarIndeterminateVisibility(true);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_friends);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);
        noResultsLayout = (LinearLayout) findViewById(R.id.no_results_container);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText.length() < 2)
                {
                    mSearchFriendList.clear();
                    populateListView(newText);
                    return false;
                }


                final ParseUser userObject = ParseUser.getCurrentUser();
                final String userId = userObject.getObjectId();

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereStartsWith(ParseConstants.USER_USERNAME, newText);
                query.whereNotEqualTo(ParseConstants.OBJECT_ID, userId);
                query.setLimit(25);
                query.include("lastSevenDays");
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            for (final ParseUser friendUser : list) {
                                mSearchFriendList.clear();
                                final ParseObject userFriend = friendUser.getParseObject("ActivityHistory");

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
                                        // If there is no userfriends table record or the friend request hasn't been approved
                                        boolean includeUser = false;
                                        int userFriendStatus = -1;
                                        if (friendList.size() == 0) {
                                            includeUser = true;
                                        } else {
                                            for (ParseObject friendRecord : friendList) {
                                                int friendStatusId = friendRecord.getInt(ParseConstants.USER_FRIENDS_STATUS);
                                                if (friendStatusId != ParseConstants.FRIEND_STATUS_ACCEPTED) {
                                                    includeUser = true;
                                                    userFriendStatus = friendRecord.getInt(ParseConstants.USER_FRIENDS_STATUS);
                                                }
                                            }
                                        }

                                        if (includeUser) {
                                            ParseFile file = friendUser.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

                                            double steps = 0;
                                            ParseObject lastSevenDays = friendUser.getParseObject("lastSevenDays");
                                            if (lastSevenDays != null) {
                                                steps =  lastSevenDays.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS);
                                            }


                                            UserListItem userListItem = new UserListItem();
                                            userListItem.setmIconId(R.drawable.ic_user);
                                            userListItem.setmImageUri(fileUri);
                                            userListItem.setmUserObject(userObject);
                                            userListItem.setmFriendObject(friendUser);
                                            userListItem.setmFriendStatusId(userFriendStatus);
                                            userListItem.setmSteps((int) steps);
                                            mSearchFriendList.add(userListItem);
                                        }
                                        if (mSearchFriendList.size() > 0) {
                                            populateListView(newText);
                                        }
                                    }
                                });
                            }
                                populateListView(newText);
                        }
                    }
                });


                return false;
            }
        });
    }

    private void populateListView(String newText) {
        if (mSearchFriendList.size() > 0) {
            noResultsLayout.setVisibility(View.GONE);
            searchResultListView.setVisibility(View.VISIBLE);
            boolean isInvite = true;
            ArrayAdapter<UserListItem> adapter = new UserRowAdapter(this, R.layout.row_user, mSearchFriendList, isInvite);
            searchResultListView = (ListView) findViewById(R.id.search_results_list_view);
            searchResultListView.setAdapter(adapter);
        }
        else {
            searchResultListView.setVisibility(View.GONE);
            if (newText.length()  < 2) {
                noResultsLayout.setVisibility(View.GONE);
            }
            else {
                noResultsLayout.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
