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

import dariogonzalez.fitplaygames.Adapters.UserRowAdapter;
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

                final ParseUser userObject = ParseUser.getCurrentUser();
                final String userId = userObject.getObjectId();

                //TODO: Only search for those whom are not friends or have not received a friend request, or have not declined a friend request, or have not been deleted from a friend request...
                users.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            mSearchFriendList.clear();
                            for (final ParseUser friend : list) {
                                ParseFile file = friend.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                mSearchFriendList.add(new FriendListItem(friend.getString(ParseConstants.USER_USERNAME), R.drawable.ic_user, fileUri, userId, friend.getObjectId(), userObject, friend));

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
        boolean isInvite = true;
        ArrayAdapter<FriendListItem> adapter = new UserRowAdapter(this, R.layout.row_user, mSearchFriendList, isInvite);
        searchResultListView = (ListView) findViewById(R.id.search_results_list_view);
        searchResultListView.setAdapter(adapter);
    }

}
