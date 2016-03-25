package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ChallengePlayerItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

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
            holder.gameResponse = (LinearLayout) row.findViewById(R.id.game_response);
            holder.acceptBtn = (ImageButton) row.findViewById(R.id.btn_accept);
            holder.declineBtn = (ImageButton) row.findViewById(R.id.btn_decline);
            holder.progressLayout = (LinearLayout) row.findViewById(R.id.progressLayout);
            holder.finishedLayout = (LinearLayout) row.findViewById(R.id.finishedLayout);
            holder.potatoTimeValue = (TextView) row.findViewById(R.id.potato_time_value);
            holder.avgTime = (TextView) row.findViewById(R.id.avg_time);

            holder.progressBar.setMax(mStepsGoal);

            row.setTag(holder);
        }
        else {
            holder = (ChallengeInviteHolder) row.getTag();
        }

        final ChallengePlayerItem userObject = mUsers.get(position);

        holder.userNameTV.setText(userObject.getmUserName());
        final Uri profilePicture = userObject.getmImageUri();
        if (profilePicture != null)
        {
            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
        }
        else
        {
            holder.userThumbnail.setImageResource(R.drawable.ic_user);
        }

        if (mGameStatus == ParseConstants.CHALLENGE_STATUS_PENDING) {
            if (userObject.getmStatus() == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
                holder.passesTV.setText(getContext().getResources().getString(R.string.accepted));
            }
            else if (userObject.getmStatus() == ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING) {
                holder.passesTV.setText(getContext().getResources().getString(R.string.pending));
            }

            String objectId = ParseUser.getCurrentUser().getObjectId();
            if (objectId.equals(userObject.getmUserObject().getObjectId())) {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
                if (userObject.getmStatus() == ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING) {
                    holder.gameResponse.setVisibility(View.VISIBLE);
                }
            }
            else {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                holder.gameResponse.setVisibility(View.GONE);
            }
        }
        else if (mGameStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {

            holder.passesTV.setText(String.valueOf(userObject.getmPasses() + " passes"));
            if (position == 0) {
                // Only show on the top player (the player that has the potato
                holder.progressLayout.setVisibility(View.VISIBLE);
                holder.stepsTV.setText(String.valueOf(userObject.getmSteps()));
                holder.progressBar.setProgress((int) userObject.getmSteps());
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
            }
        }
        else if (mGameStatus == ParseConstants.CHALLENGE_STATUS_FINISHED) {

            holder.passesTV.setText(String.valueOf(userObject.getmPasses() + " passes"));
            holder.finishedLayout.setVisibility(View.VISIBLE);
            holder.stepsTV.setText(String.valueOf(userObject.getmSteps()));

            int hours = userObject.getmPlayerAverageHoldingTime() / 60;
            int minutes = userObject.getmPlayerAverageHoldingTime() % 60;
            String potatoTimeStr = ((hours > 0) ? hours + " Hr " : "") + minutes + " Min";
            holder.potatoTimeValue.setText(potatoTimeStr);
            if(position == 0) {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.hot_potato_color));
                holder.passesTV.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.userNameTV.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.potatoTimeValue.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.avgTime.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.userThumbnail.setImageResource(R.drawable.firepotato);

            }
            else {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            }
        }

        final ChallengeInviteHolder rowHolder = holder;
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPlayerResponse(ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED, userObject, rowHolder);
            }
        });

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPlayerResponse(ParseConstants.CHALLENGE_PLAYER_STATUS_DECLINED, userObject, rowHolder);
            }
        });

        return row;
    }

    private void sendPlayerResponse(final int status, ChallengePlayerItem user, final ChallengeInviteHolder holder) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, user.getmUserObject());
        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, user.getmChallengeObject());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    parseObject.put(ParseConstants.CHALLENGE_PLAYER_STATUS, status);
                    parseObject.saveInBackground();
                    holder.gameResponse.setVisibility(View.GONE);

                    if (status == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
                        holder.passesTV.setText(getContext().getResources().getString(R.string.accepted));
                    } else if (status == ParseConstants.CHALLENGE_PLAYER_STATUS_DECLINED) {
                        holder.passesTV.setText(getContext().getResources().getString(R.string.declined));
                    }

                    try {
                        if (status == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
                            ParseObject challenge = parseObject.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();
                            challenge.increment(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS);
                            challenge.saveInBackground();
                        }
                    } catch (Exception ex) {}
                }
            }
        });
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, passesTV, stepsTV, potatoTimeValue, avgTime;
        ImageView userThumbnail;
        ProgressBar progressBar;
        LinearLayout gameResponse, progressLayout, finishedLayout;
        ImageButton acceptBtn, declineBtn;
    }
}
