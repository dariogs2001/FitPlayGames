package dariogonzalez.fitplaygames;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import dariogonzalez.fitplaygames.Adapters.UserRowAdapter;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;


public class SearchFriendsFragment extends Fragment {
    private List<UserListItem> mFriendList = new ArrayList<UserListItem>();
    private ListView friendsResultListView;

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


        getFriendData();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void getFriendData() {
        if (mFriendList!= null && mFriendList.size() == 0) {
            final ParseUser userObject = ParseUser.getCurrentUser();
            if (userObject != null) {
                final String userId = userObject.getObjectId();

                List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                ParseQuery<ParseObject> query1 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query1.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query1.whereEqualTo(ParseConstants.USER_OBJECT, userObject);

                ParseQuery<ParseObject> query2 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query2.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_SENT);
                query2.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);

                ParseQuery<ParseObject> query3 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query3.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query3.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);

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
                                if (userFriend.getString("FriendId").equals(userId)) {
                                    friendObject = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                                    newUserObject = userObject;
                                } else {
                                    friendObject = userFriend.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                                    newUserObject = userObject;
                                }
                                if (friendObject != null) {
                                    ParseFile file = friendObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                    Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                    int friendStatusId = userFriend.getInt(ParseConstants.USER_FRIENDS_STATUS);
                                    int location = 0;
                                    if (friendStatusId == ParseConstants.FRIEND_STATUS_SENT) {
                                        if (mFriendList.size() == 0) {
                                            location = mFriendList.size();
                                        } else {
                                            location = mFriendList.size() - 1;
                                        }
                                    }

                                    double steps = 0;
                                    if (friendObject.has("lastSevenDays")) {
                                        ParseObject lastSevenDays = friendObject.getParseObject("lastSevenDays").fetchIfNeeded();
                                        if (lastSevenDays != null) {
                                            steps = lastSevenDays.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS);
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
                        populateListView();
                    }
                });
            }
        }
        else {
            populateListView();
        }
    }

    private void populateListView() {
        friendsResultListView.setVisibility(View.VISIBLE);
        boolean isInvite = false;
        ArrayAdapter<UserListItem> adapter = new UserRowAdapter(getActivity(), R.layout.row_user, mFriendList, isInvite);
        friendsResultListView.setAdapter(adapter);
        friendsResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                Bundle extras = new Bundle();
                // Parse friend object Id
                extras.putString("userId", mFriendList.get(position).getmFriendObject().getObjectId());
                extras.putString("username", mFriendList.get(position).getmFriendObject().getUsername());
                boolean isFriend = false;
                if (mFriendList.get(position).getmFriendStatusId() == ParseConstants.FRIEND_STATUS_ACCEPTED) {
                    isFriend = true;
                }
                extras.putBoolean("isFriend", isFriend);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

}
