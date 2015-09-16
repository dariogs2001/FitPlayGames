package dariogonzalez.fitplaygames;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFriendsFragment extends android.support.v4.app.Fragment {
    private List<FriendListItem> mFriendList = new ArrayList<FriendListItem>();
    ListView friendsResultListView;
    View view;

    private FloatingActionButton fab;

    public MainFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_friends, container, false);

        friendsResultListView = (ListView) view.findViewById(R.id.search_results_list_view);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
            }
        });

        if (mFriendList!= null && mFriendList.size() == 0) {
            final ParseUser userObject = ParseUser.getCurrentUser();
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
                            if (userFriend.getString("FriendId").equals(userId)){
                                friendObject = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                                newUserObject = userObject;
                            }
                            else {
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
                                mFriendList.add(location, new FriendListItem(friendObject.getUsername(), R.drawable.ic_user, fileUri, newUserObject, friendObject, friendStatusId, userFriend.getObjectId()));
                            }

                        } catch (ParseException ex) {
                        }
                    }
                    populateListView();
                }
            });
        }
        else {
            populateListView();
        }
        return view;
    }

    private void populateListView() {
        boolean isInvite = false;
        ArrayAdapter<FriendListItem> adapter = new UserRowAdapter(view.getContext(), R.layout.row_user, mFriendList, isInvite);
        friendsResultListView = (ListView) view.findViewById(R.id.friends_list_view);
        friendsResultListView.setAdapter(adapter);
        fab.attachToListView(friendsResultListView);
    }

}
