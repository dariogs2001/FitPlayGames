package dariogonzalez.fitplaygames.Friends;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dariogonzalez.fitplaygames.Adapters.ChallengeInviteAdapter;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;


public class SearchFriendsFragment extends Fragment {
    private List<UserListItem> mFriendList = new ArrayList<UserListItem>();
    private List<UserListItem> mQueriedFriendList = new ArrayList<>();
    private ListView friendsResultListView;
    ArrayAdapter<UserListItem> adapter;

    public static SearchFriendsFragment newInstance() {
        SearchFriendsFragment fragment = new SearchFriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search_friends, container, false);

        friendsResultListView = (ListView) rootView.findViewById(R.id.search_results_list_view);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) rootView.findViewById(R.id.search_friends);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    updateList(query);
                }
                else {
                    populateListView(mFriendList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    updateList(newText);
                }
                else {
                    populateListView(mFriendList);
                }
                return false;
            }
        });

        getFriendData();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateList(String queryText) {
        mQueriedFriendList.clear();
        if(mFriendList.size() > 0)
        {
            for(UserListItem user : mFriendList)
            {
                Locale locale = Locale.getDefault();
                // Prefix is whatever the user has entered into the edittext. It will check lower/upper case names and check conf number
                if(user.getmFriendObject().get(ParseConstants.USER_USERNAME).toString().toLowerCase().contains(queryText.toLowerCase()))
                {
                    mQueriedFriendList.add(user);
                }
            }
            populateListView(mQueriedFriendList);
        }
    }

    private void getFriendData() {
        if (mFriendList!= null && mFriendList.size() == 0) {
            final ParseUser userObject = ParseUser.getCurrentUser();
            if (userObject != null) {
                final String userId = userObject.getObjectId();

                List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                ParseQuery<ParseObject> query1 = new ParseQuery<>(ParseConstants.CLASS_USER_FRIENDS);
                query1.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query1.whereEqualTo(ParseConstants.USER_OBJECT, userObject);
                query1.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);

                ParseQuery<ParseObject> query2 = new ParseQuery<>(ParseConstants.CLASS_USER_FRIENDS);
                query2.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_SENT);
                query2.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);
                query2.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);

                ParseQuery<ParseObject> query3 = new ParseQuery<>(ParseConstants.CLASS_USER_FRIENDS);
                query3.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query3.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);
                query3.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);

                queries.add(query1);
                queries.add(query2);
                queries.add(query3);

                ParseQuery<ParseObject> superQuery = ParseQuery.or(queries);
                superQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (final ParseObject userFriend : list) {
                            try {
                                ParseUser friendObject;
                                ParseUser newUserObject;
                                if (userFriend.getString(ParseConstants.USER_FRIENDS_FRIEND_ID).equals(userId))
                                {
                                    friendObject = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                                    newUserObject = userObject;
                                }
                                else
                                {
                                    friendObject = userFriend.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                                    newUserObject = userObject;
                                }

                                if (friendObject != null)
                                {
                                    ParseFile file = friendObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                    Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                    int friendStatusId = userFriend.getInt(ParseConstants.USER_FRIENDS_STATUS);

                                    double steps = 0;
                                    if (friendObject.has(ParseConstants.USER_LAST_SEVEN_DAYS)) {
                                        try {
                                            ParseObject lastSevenDays = friendObject.getParseObject(ParseConstants.USER_LAST_SEVEN_DAYS).fetchIfNeeded();
                                            if (lastSevenDays != null)
                                            {
                                                steps = lastSevenDays.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS);
                                            }
                                        }
                                        catch (Exception ex)
                                        {
                                            steps = 0;
                                        }
                                    } else {
                                        steps = 0;
                                    }

                                    UserListItem userListItem = new UserListItem();
                                    userListItem.setmIconId(R.drawable.ic_user);
                                    userListItem.setmImageUri(fileUri);
                                    userListItem.setmUserObject(newUserObject);
                                    userListItem.setmFriendObject(friendObject);
                                    userListItem.setmFriendStatusId(friendStatusId);
                                    userListItem.setmSteps((int) steps);
                                    userListItem.setmUserFriendId(userFriend.getObjectId());
                                    mFriendList.add(userListItem);
                                }

                            } catch (ParseException ex) {
                            }
                        }
                        populateListView(mFriendList);
                    }
                });
            }
        }
        else {
            populateListView(mFriendList);
        }
    }

    private void populateListView(List<UserListItem> list) {
        boolean isInvite = false;
        adapter = new ChallengeInviteAdapter(getActivity(), R.layout.row_challenge_invite, list);

        friendsResultListView.setVisibility(View.VISIBLE);
        friendsResultListView.setAdapter(adapter);
    }

    public List<UserListItem> getSelectedFriends()
    {
        List<UserListItem> selectedItems = new ArrayList<>();

        for (UserListItem user : mFriendList)
        {
            if (user.getChecked())
            {
                selectedItems.add(user);
            }
        }

        return selectedItems;
    }
}
