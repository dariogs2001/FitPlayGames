package dariogonzalez.fitplaygames.Adapters;


import android.content.Context;
import android.media.Image;
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
 * Created by ChristensenKC on 1/27/2016.
 */
public class CaptureTheCrownPlayersAdapter extends ArrayAdapter<ChallengePlayerItem> {

    private Context mContext;
    private List<ChallengePlayerItem> mUsers = new ArrayList<>();
    private int mStepsGoal, mGameStatus;

    public CaptureTheCrownPlayersAdapter(Context context, int resource, List<ChallengePlayerItem> challengePlayers, int stepsGoal, int gameStatus) {
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
            row = LayoutInflater.from(mContext).inflate(R.layout.row_capture_the_crown_players, parent, false);

            holder = new ChallengeInviteHolder();
            holder.userNameTV = (TextView) row.findViewById(R.id.user_name);
            holder.capturesTV = (TextView) row.findViewById(R.id.tv_captures);
            holder.stepsTV = (TextView) row.findViewById(R.id.steps_tv);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progressBar);
            holder.crownImage = (ImageView) row.findViewById(R.id.crownImage);
            holder.gameResponse = (LinearLayout) row.findViewById(R.id.game_response);
            holder.acceptBtn = (ImageButton) row.findViewById(R.id.btn_accept);
            holder.declineBtn = (ImageButton) row.findViewById(R.id.btn_decline);
            holder.crownLayout = (LinearLayout) row.findViewById(R.id.crownLayout);
            holder.progressLayout = (LinearLayout) row.findViewById(R.id.progressLayout);

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
                holder.capturesTV.setText(getContext().getResources().getString(R.string.accepted));
            }
            else if (userObject.getmStatus() == ParseConstants.CHALLENGE_PLAYER_STATUS_PENDING) {
                holder.capturesTV.setText(getContext().getResources().getString(R.string.pending));
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

            holder.capturesTV.setText(String.valueOf(userObject.getmPasses() + " captures"));
            if (position == 0) {
                // Only show on the top player (the player that has the crown
                holder.crownLayout.setVisibility(View.VISIBLE);
                holder.progressLayout.setVisibility(View.INVISIBLE);
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
            }
            else {
                holder.progressLayout.setVisibility(View.VISIBLE);
                holder.stepsTV.setText(String.valueOf(userObject.getmSteps()));
                row.setBackgroundColor(getContext().getResources().getColor(R.color.light_light_grey));
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
        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_ID, user.getmUserObject());
        query.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT, user.getmChallengeObject());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> challengePlayers, ParseException e) {
                if (e == null) {
                    challengePlayers.get(0).put(ParseConstants.CHALLENGE_PLAYER_STATUS, status);
                    challengePlayers.get(0).saveInBackground();
                    holder.gameResponse.setVisibility(View.GONE);
                    //TODO: see this one... We are already setting the number of players when we create the game, with this line we are incrementing the value
                    //every time a player accept the challenge...SO FOR NOW WE ONLY SET THE TOTAL NUMBER OF PLAYER INVITED TO PLAY THE GAME...
//                    if (status == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
//                        ParseObject challenge = challengePlayers.get(0).getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT);
//                        int numOfPlayers = challenge.getInt(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS);
//                        numOfPlayers++;
//                        challenge.put(ParseConstants.CHALLENGE_NUMBER_OF_PLAYERS, numOfPlayers);
//                        challenge.saveInBackground();
//                    }
                }
            }
        });
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, capturesTV, stepsTV;
        ImageView userThumbnail, crownImage;
        ProgressBar progressBar;
        LinearLayout gameResponse, crownLayout, progressLayout;
        ImageButton acceptBtn, declineBtn;
    }
}


