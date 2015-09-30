package dariogonzalez.fitplaygames;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class LeaderBoardActivity extends AppCompatActivity {
    private List<UserListItem> mLeadBoardList = new ArrayList<UserListItem>();
    private View view;
    private ListView friendsResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_leader_board);
        friendsResultListView = (ListView) findViewById(R.id.leader_board_list_view);

            getLeaderBoardData();

    }

    private void getLeaderBoardData() {

        ParseQuery<ParseObject> stepsQuery = ParseQuery.getQuery(ParseConstants.CLASS_LAST_SEVEN_DAYS);
        stepsQuery.orderByAscending(ParseConstants.LAST_SEVEN_DAYS_STEPS);
        stepsQuery.setLimit(10);
        stepsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                    for (ParseObject userFriend : list) {
                        try {
                            ParseUser user = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                            if (user != null) {
                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;


                                UserListItem userListItem = new UserListItem();
                                userListItem.setmIconId(R.drawable.ic_user);
                                userListItem.setmImageUri(fileUri);
                                userListItem.setmSteps((long) (userFriend.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS)));
                                userListItem.setmFriendObject(user);
                                userListItem.setmGamesPlayed(15);
                                mLeadBoardList.add(userListItem);
                            }
                        } catch (ParseException ex) {
                        }
                    }
                    populateListView();
            }
        });
    }


    private void populateListView() {
        ArrayAdapter<UserListItem> adapter = new UserRowAdapter(this, R.layout.leader_board_list_item, mLeadBoardList, false);
        friendsResultListView.setAdapter(adapter);
    }

}