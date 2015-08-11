package dariogonzalez.fitplaygames;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class SearchFriendActivity extends AppCompatActivity {
    private List<FriendListItem> mSearchFriendList = new ArrayList<FriendListItem>();
    ListView searchResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_friends);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() == 0)
                {
                    mSearchFriendList.clear();
                    populateListView();
                    return false;
                }

                ParseQuery<ParseUser> users = ParseUser.getQuery();
                users.whereStartsWith(ParseConstants.USER_USERNAME, newText);
                users.setLimit(50);

                final String userId = ParseUser.getCurrentUser().getObjectId();
                //TODO: Only search for those whom are not friends or have not received a friend request, or have not declined a friend request, or have not been deleted from a friend request...
                users.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            mSearchFriendList.clear();
                            for (final ParseUser user : list) {
//                                ParseQuery userFriendQuery = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
//                                userFriendQuery.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
//                                userFriendQuery.whereEqualTo(ParseConstants.USER_FRIENDS_FRIEND_ID, user.getObjectId());
//                                userFriendQuery.getFirstInBackground(new GetCallback() {
//                                    @Override
//                                    public void done(ParseObject parseObject, ParseException e) {
//                                    }
//
//                                    @Override
//                                    public void done(Object o, Throwable throwable) {
//                                        if (throwable != null)
//                                        {
//                                            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
//                                            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
//                                            mSearchFriendList.add(new FriendListItem(user.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri, userId, user.getObjectId()));
//
//                                            //TODO: move this outside of this if... Need to find a way to call it only once, at the end of the loop.
//                                            populateListView();
//                                        }
//                                    }
//                                });
                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                mSearchFriendList.add(new FriendListItem(user.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri, userId, user.getObjectId()));

                            }
                            populateListView();
                        }
                    }
                });

                return false;
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<FriendListItem> adapter = new FriendSearchAdapterList(this, R.layout.friends_search_item);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);
        searchResultListView.setAdapter(adapter);
    }

    private class FriendSearchAdapterList extends ArrayAdapter<FriendListItem> {
        Context mContext;
        public FriendSearchAdapterList(Context context, int resource) {
            super(context, resource, mSearchFriendList);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = LayoutInflater.from(mContext).inflate(R.layout.friends_search_item, parent, false);
            }
            final FriendListItem current = mSearchFriendList.get(position);

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

            final Button inviteButton =  (Button) itemView.findViewById(R.id.btn_invite);
            final Button sentButton =  (Button) itemView.findViewById(R.id.btn_sent);

            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: add invitation to the DB
                    ParseObject newObject = new ParseObject(ParseConstants.CLASS_USER_FRIENDS);
                    newObject.put(ParseConstants.KEY_USER_ID, current.getUserId());
                    newObject.put(ParseConstants.USER_FRIENDS_FRIEND_ID, current.getFriendId());
                    newObject.put(ParseConstants.USER_FRIENDS_STATUS, 0);

                    newObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                inviteButton.setVisibility(View.GONE);
                                sentButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            return itemView;
        }
    }
}
