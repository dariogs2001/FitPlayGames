package dariogonzalez.fitplaygames.LeaderBoard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import dariogonzalez.fitplaygames.Adapters.UserRowAdapterNew;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.UserProfileActivity;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;

/**
 * Created by ChristensenKC on 10/7/2015.
 */
public class LeaderBoardFragmentNew extends Fragment {
    private List<UserListItem> mLeadBoardList = new ArrayList<UserListItem>();
    private ListView friendsResultListView;
    private View view;

    public LeaderBoardFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_leader_board_new, container, false);
        friendsResultListView = (ListView) view.findViewById(R.id.leader_board_list_view);

        showFriendsList();
        return view;
    }

    public void showFriendsList() {
        if (mLeadBoardList!= null && mLeadBoardList.size() == 0) {
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
                                        if (mLeadBoardList.size() == 0) {
                                            location = mLeadBoardList.size();
                                        } else {
                                            location = mLeadBoardList.size() - 1;
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
                                    mLeadBoardList.add(userListItem);
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

            ArrayAdapter<UserListItem> adapter = new UserRowAdapterNew(view.getContext(), R.layout.leader_board_list_item_new, mLeadBoardList, false, true);
            friendsResultListView.setAdapter(adapter);
            friendsResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    Bundle extras = new Bundle();
                    // Parse friend object Id
                    extras.putString("userId", mLeadBoardList.get(position).getmFriendObject().getObjectId());
                    extras.putString("username", mLeadBoardList.get(position).getmFriendObject().getUsername());
                    boolean isFriend = false;
                    if (mLeadBoardList.get(position).getmFriendStatusId() == ParseConstants.FRIEND_STATUS_ACCEPTED) {
                        isFriend = true;
                    }
                    extras.putBoolean("isFriend", isFriend);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
    }

    public void clearListView() {
        friendsResultListView.setAdapter(null);
        mLeadBoardList.clear();

    }

}