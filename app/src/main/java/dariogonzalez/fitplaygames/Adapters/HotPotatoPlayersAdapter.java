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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;

/**
 * Created by Logan on 11/2/2015.
 */
public class HotPotatoPlayersAdapter extends ArrayAdapter<ChallengePlayerItem> {

    private Context mContext;
    private List<ChallengePlayerItem> mUsers = new ArrayList<>();
    private int mStepsGoal, mGameStatus;

    public HotPotatoPlayersAdapter(Context context, int resource, List<ChallengePlayerItem> challengePlayers, int stepsGoal, int gameStatus) {
        super(context, resource, challengePlayers);
        this.mContext = context;
        this.mUsers = challengePlayers;
        this.mStepsGoal = stepsGoal;
        this.mGameStatus = gameStatus;
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

        final ChallengePlayerItem userObject = mUsers.get(position);

        holder.userNameTV.setText(userObject.getmUserName());
        holder.passesTV.setText(String.valueOf(userObject.getmPasses()));
        final Uri profilePicture = userObject.getmImageUri();
        if (profilePicture != null)
        {
            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
        }
        else
        {
            holder.userThumbnail.setImageResource(R.drawable.ic_action_person);
        }

        if (mGameStatus == ParseConstants.CHALLENGE_STATUS_PENDING) {
            String objectId = ParseUser.getCurrentUser().getObjectId();
            if (objectId.equals(userObject.getmUserObject().getObjectId())) {
                Log.d("TEST", "objectId: " + objectId + " positionObjectId: " + userObject.getmUserObject().getObjectId());
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
            }
            else {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            }
        }
        else if (mGameStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {
            if (position == 0) {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
            }
        }



        return row;
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, passesTV, stepsTV;
        ImageView userThumbnail;
        ProgressBar progressBar;

    }
}
