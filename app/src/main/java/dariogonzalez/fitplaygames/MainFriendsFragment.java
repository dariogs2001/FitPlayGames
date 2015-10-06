package dariogonzalez.fitplaygames;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFriendsFragment extends android.support.v4.app.Fragment {
    private List<UserListItem> mFriendList = new ArrayList<UserListItem>();
    private ListView friendsResultListView;
    private LinearLayout emptyStateLayout;
    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton fab;

    public MainFriendsFragment() {
        // Required empty public constructor
    }

    public static MainFriendsFragment newInstance(int sectionNumber) {
        MainFriendsFragment fragment = new MainFriendsFragment();
        Bundle args = new Bundle();
        args.putInt("section_num", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_friends, container, false);

        friendsResultListView = (ListView) view.findViewById(R.id.friends_list_view);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        emptyStateLayout = (LinearLayout) view.findViewById(R.id.empty_state_friends);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriendData();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
            }
        });


        getFriendData();
        return view;
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
        swipeRefreshLayout.setRefreshing(false);
        if (mFriendList.size() > 0) {
            friendsResultListView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            boolean isInvite = false;
            ArrayAdapter<UserListItem> adapter = new UserRowAdapter(view.getContext(), R.layout.row_user, mFriendList, isInvite);
            friendsResultListView.setAdapter(adapter);
            fab.attachToListView(friendsResultListView);
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
        else {
            friendsResultListView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

}
