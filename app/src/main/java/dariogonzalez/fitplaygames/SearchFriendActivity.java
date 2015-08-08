package dariogonzalez.fitplaygames;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.classes.FriendSearchListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

public class SearchFriendActivity extends AppCompatActivity {
    private List<FriendSearchListItem> mSearchFriendList = new ArrayList<FriendSearchListItem>();
    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_friends);
//        ListView searchResultListView = (ListView) findViewById(R.id.search_results_list_view);

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

                //TODO: Only search for those whom are not friends or have not received a friend request, or have not declined a friend request, or have not been deleted from a friend request...
                users.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null)
                        {
                            mSearchFriendList.clear();
                            for (ParseUser user : list)
                            {
                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                mSearchFriendList.add(new FriendSearchListItem(user.getString(ParseConstants.USER_USERNAME), R.mipmap.ic_profile, fileUri));
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
        ArrayAdapter<FriendSearchListItem> adapter = new FriendSearchAdapterList(this, R.layout.friends_search_item);
        ListView list = (ListView) findViewById(R.id.search_results_list_view);
        list.setAdapter(adapter);
    }

    private class FriendSearchAdapterList extends ArrayAdapter<FriendSearchListItem> {
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
            FriendSearchListItem current = mSearchFriendList.get(position);

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
