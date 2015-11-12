package dariogonzalez.fitplaygames.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;

/**
 * Created by Logan on 11/2/2015.
 */
public class HotPotatoPlayersAdapter extends ArrayAdapter<ParseObject> {

    private Context mContext;
    private List<ParseObject> mChallengePlayers = new ArrayList<>();
    private int mStepsGoal;

    public HotPotatoPlayersAdapter(Context context, int resource, List<ParseObject> challengePlayers, int stepsGoal) {
        super(context, resource, challengePlayers);
        this.mContext = context;
        this.mChallengePlayers = challengePlayers;
        this.mStepsGoal = stepsGoal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChallengeInviteHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.row_hot_potato_players, parent, false);

            holder = new ChallengeInviteHolder();
            holder.userNameTV = (TextView) row.findViewById(R.id.user_name);
            holder.passesTV = (TextView) row.findViewById(R.id.tv_passes);
            holder.stepsTV = (TextView) row.findViewById(R.id.steps_tv);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progressBar);

            holder.progressBar.setMax(mStepsGoal);

            row.setTag(holder);
        }
        else {
            holder = (ChallengeInviteHolder) row.getTag();
        }

        final ParseObject challengePlayerObject = mChallengePlayers.get(position);

        getUserObjects(holder, challengePlayerObject);

        return row;
    }

    private void getUserObjects(final ChallengeInviteHolder holder, ParseObject challengePlayerObject) {
        Log.d("TEST", "here: " + challengePlayerObject.getObjectId());
        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER);
        userQuery.whereEqualTo(ParseConstants.KEY_USER_ID, challengePlayerObject.getObjectId());
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("TEST", "size: " + list.size());
                    if (list.size() > 0) {
                        ParseObject userObject = list.get(0);
                        holder.userNameTV.setText(userObject.get(ParseConstants.USER_USERNAME).toString());
                        ParseFile file = userObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                        final Uri profilePicture = file != null ? Uri.parse(file.getUrl()) : null;
                        if (profilePicture != null)
                        {
                            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
                        }
                        else
                        {
                            holder.userThumbnail.setImageResource(R.drawable.ic_action_person);
                        }
                    }
                }
                else {

                }
            }
        });
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, passesTV, stepsTV;
        ImageView userThumbnail;
        ProgressBar progressBar;

    }
}
