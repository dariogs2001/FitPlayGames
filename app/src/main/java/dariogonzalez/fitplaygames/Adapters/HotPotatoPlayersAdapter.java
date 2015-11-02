package dariogonzalez.fitplaygames.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
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

        final ParseObject currentItem = mChallengePlayers.get(position);

//        holder.userNameTV.setText(currentItem.getmFriendObject().getUsername());

//        Uri profilePicture = currentItem.getmImageUri();
//        if (profilePicture != null)
//        {
//            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
//        }
//        else
//        {
//            holder.userThumbnail.setImageResource(currentItem.getmIconId());
//        }

        return row;
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, passesTV, stepsTV;
        ImageView userThumbnail;
        ProgressBar progressBar;

    }
}
