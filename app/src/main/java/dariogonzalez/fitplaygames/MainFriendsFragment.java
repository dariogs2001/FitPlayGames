package dariogonzalez.fitplaygames;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFriendsFragment extends android.support.v4.app.Fragment {
    private List<FriendListItem> mFriendList = new ArrayList<FriendListItem>();
    ListView friendsResultListView;
    View view;

    public MainFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_friends, container, false);

        friendsResultListView = (ListView) view.findViewById(R.id.search_results_list_view);

        ImageView imageAdd = (ImageView) view.findViewById(R.id.btn_add);

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
            }
        });

        if (mFriendList!= null && mFriendList.size() == 0) {
            //TODO: also need to add friends in the list where "FriendId" = userId (kind of complicated to create an OR sentence with Parse.com, need to do more research).
            final ParseUser userObject = ParseUser.getCurrentUser();
            final String userId = userObject.getObjectId();
            ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
            query.whereNotEqualTo(ParseConstants.USER_FRIENDS_STATUS, 3);
//        query.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
            query.whereEqualTo(ParseConstants.USER_OBJECT, userObject);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    for (final ParseObject userFriend : list) {
                        try {
//                        ParseUser user = ParseUser.getQuery().get(userFriend.getString(ParseConstants.USER_FRIENDS_FRIEND_ID));
//                        if (user != null)
//                        {
//                            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
//                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
//                            mFriendList.add(new FriendListItem(user.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri, userId, user.getObjectId()));
//                        }

                            ParseUser friendObject = userFriend.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                            if (friendObject != null) {
                                ParseFile file = friendObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                mFriendList.add(new FriendListItem(friendObject.getUsername(), R.drawable.ic_user, fileUri, userId, friendObject.getObjectId(), userObject, friendObject));
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
    }

}
