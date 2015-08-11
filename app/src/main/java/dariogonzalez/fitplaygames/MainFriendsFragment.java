package dariogonzalez.fitplaygames;


import android.content.Context;
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
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

        //TODO: also need to add friends where "FriendId" = userId (kind of complicated to create an OR sentence with Parse.com, need to do more research).
        final String userId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
        query.whereNotEqualTo(ParseConstants.USER_FRIENDS_STATUS, 3);
        query.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (final ParseObject userFriend : list)
                {
                    try
                    {
                        ParseUser user = ParseUser.getQuery().get(userFriend.getString(ParseConstants.USER_FRIENDS_FRIEND_ID));
                        if (user != null)
                        {
                            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                            mFriendList.add(new FriendListItem(user.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri, userId, user.getObjectId()));
                        }
                    }
                    catch (ParseException ex) {}
                }
                populateListView();
            }
        });

        return view;
    }

    private void populateListView() {
        ArrayAdapter<FriendListItem> adapter = new FriendsAdapterList(view.getContext(), R.layout.friends_list_item);
        friendsResultListView = (ListView) view.findViewById(R.id.friends_list_view);
        friendsResultListView.setAdapter(adapter);
    }

    private class FriendsAdapterList extends ArrayAdapter<FriendListItem> {
        Context mContext;
        public FriendsAdapterList(Context context, int resource) {
            super(context, resource, mFriendList);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = LayoutInflater.from(mContext).inflate(R.layout.friends_list_item, parent, false);
            }
            final FriendListItem current = mFriendList.get(position);

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

            return itemView;
        }
    }
}
