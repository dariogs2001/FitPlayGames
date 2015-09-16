package dariogonzalez.fitplaygames;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.classes.LeaderBoardListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;


public class LeaderBoardActivity extends ActionBarActivity {
    private List<LeaderBoardListItem> mLeadBoardList = new ArrayList<LeaderBoardListItem>();
    private View view;
    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        if (mLeadBoardList.size() == 0)
        {
            populateList();
        }

        registerClickCallback();
    }

    private void populateList() {

        ParseQuery<ParseObject> stepsQuery = ParseQuery.getQuery(ParseConstants.CLASS_LAST_SEVEN_DAYS);
        stepsQuery.orderByAscending(ParseConstants.LAST_SEVEN_DAYS_STEPS);
        stepsQuery.setLimit(10);
        stepsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : list) {
                            ParseUser user = obj.getParseUser(ParseConstants.USER_OBJECT);
                            if (user != null)
                            {
                                ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                mLeadBoardList.add(new LeaderBoardListItem(user.getString(ParseConstants.USER_USERNAME), "" + obj.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS), "15", R.drawable.ic_user, fileUri));
                            }
//                        }
//                        catch (ParseException ex) {}
                    }
                    populateListView();
                }
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<LeaderBoardListItem> adapter = new LeaderBoardAdapterList(this, R.layout.leader_board_list_item);
        ListView list = (ListView) findViewById(R.id.leader_board_list_view);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView)findViewById(R.id.leader_board_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }


    private class LeaderBoardAdapterList extends ArrayAdapter<LeaderBoardListItem> {
        Context mContext;
        public LeaderBoardAdapterList(Context context, int resource) {
            super(context, resource, mLeadBoardList);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = LayoutInflater.from(mContext).inflate(R.layout.leader_board_list_item, parent, false);
            }
            LeaderBoardListItem current = mLeadBoardList.get(position);

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
            TextView stepsTextView = (TextView) itemView.findViewById(R.id.steps_text_view);
            stepsTextView.setText(current.getSteps());
            TextView gamesTextView = (TextView) itemView.findViewById(R.id.games_text_view);
            gamesTextView.setText(current.getGamesPlayed());

            return itemView;

//            return super.getView(position, convertView, parent);
        }
    }
}
