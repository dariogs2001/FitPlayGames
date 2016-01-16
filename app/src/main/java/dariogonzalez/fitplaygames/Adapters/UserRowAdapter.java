package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.FitPlayGamesApplication;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by Logan on 9/11/2015.
 */

public class UserRowAdapter extends ArrayAdapter<UserListItem> {
    Context mContext;
    private List<UserListItem> mFriendList = new ArrayList<>();
    private boolean isInvite;
    private boolean isLeaderBoard = false;

    public UserRowAdapter(Context context, int resource, List<UserListItem> mFriendList, boolean isInvite) {
        super(context, resource, mFriendList);
        mContext = context;
        this.mFriendList = mFriendList;
        this.isInvite = isInvite;
    }

    public UserRowAdapter(Context context, int resource, List<UserListItem> mFriendList, boolean isInvite, boolean isLeaderBoard) {
        super(context, resource, mFriendList);
        mContext = context;
        this.mFriendList = mFriendList;
        this.isInvite = isInvite;
        this.isLeaderBoard = isLeaderBoard;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserRowHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.row_user, parent, false);

            holder = new UserRowHolder();
            holder.userNameTV = (TextView) row.findViewById(R.id.user_name);
            holder.stepsTV = (TextView) row.findViewById(R.id.tv_steps);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);
            holder.inviteButton = (ImageButton) row.findViewById(R.id.btn_invite);
            holder.acceptButton = (ImageButton) row.findViewById(R.id.btn_accept);
            holder.declineButton = (ImageButton) row.findViewById(R.id.btn_decline);
            holder.friendRequestResponseLayout = (LinearLayout) row.findViewById(R.id.friend_request_response_layout);
            holder.inviteLayout = (LinearLayout) row.findViewById(R.id.invite_layout);
            holder.sentLayout = (LinearLayout) row.findViewById(R.id.invite_sent_layout);

            row.setTag(holder);
        }
        else {
            holder = (UserRowHolder) row.getTag();
        }

        final UserListItem currentItem = mFriendList.get(position);

        if (isInvite) {
            holder.inviteLayout.setVisibility(View.VISIBLE);
            final ImageButton inviteButton = holder.inviteButton;

            int friendStatusId = currentItem.getmFriendStatusId();
            if (friendStatusId == ParseConstants.FRIEND_STATUS_SENT) {
                holder.inviteLayout.setVisibility(View.GONE);
                holder.sentLayout.setVisibility(View.VISIBLE);
            }

            final UserRowHolder tempHolder = holder;

            holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Adding invitation into the DB
                    ParseObject newObject = new ParseObject(ParseConstants.CLASS_USER_FRIENDS);
                    newObject.put(ParseConstants.KEY_USER_ID, currentItem.getmUserObject().getObjectId());
                    newObject.put(ParseConstants.USER_OBJECT, currentItem.getmUserObject());
                    newObject.put(ParseConstants.USER_FRIENDS_FRIEND_ID, currentItem.getmFriendObject().getObjectId());
                    newObject.put(ParseConstants.FRIEND_OBJECT, currentItem.getmFriendObject());
                    newObject.put(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_SENT);

                    newObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                tempHolder.inviteLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_out));
                                tempHolder.inviteLayout.setVisibility(View.GONE);
                                tempHolder.sentLayout.setVisibility(View.VISIBLE);
                                tempHolder.sentLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_in));
                                ParseUser friendUser = currentItem.getmFriendObject();
                                FitPlayGamesApplication.sendPushNotification(currentItem.getmUserObject().getUsername() + getContext().getString(R.string.has_invited_friend), friendUser);
                            }
                        }
                    });
                }
            });
        }
        else if (currentItem.getmFriendStatusId() == 0 && !isLeaderBoard){
            holder.friendRequestResponseLayout.setVisibility(View.VISIBLE);
            final UserRowHolder rowHolder = holder;

            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    respondToFriendRequest(true, currentItem, rowHolder, position);
                }
            });

            holder.declineButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    respondToFriendRequest(false, currentItem, rowHolder, position);
                }
            });
        }

        holder.userNameTV.setText(currentItem.getmFriendObject().getUsername());
        holder.stepsTV.setText(String.format("%,d", currentItem.getmSteps()));
        Uri profilePicture = currentItem.getmImageUri();
        if (profilePicture != null)
        {
            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
        }
        else
        {
            holder.userThumbnail.setImageResource(currentItem.getmIconId());
        }

        return row;
    }

    public void respondToFriendRequest(final boolean accept, final UserListItem currentItem, final UserRowHolder holder, final int position) {
        ParseQuery<ParseObject> updateFriendRequestQuery = ParseQuery.getQuery(ParseConstants.CLASS_USER_FRIENDS);

        updateFriendRequestQuery.getInBackground(currentItem.getmUserFriendId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    if (accept) {
                        parseObject.put(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                        ParseUser userObject = currentItem.getmUserObject();
                        FitPlayGamesApplication.sendPushNotification(currentItem.getmFriendObject().getUsername() + getContext().getString(R.string.accepted_friend_request), userObject);

                    } else {
                        parseObject.put(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_DECLINED);
                        mFriendList.remove(position);
                    }
                    parseObject.saveInBackground();
                    holder.friendRequestResponseLayout.setVisibility(View.GONE);
                    currentItem.setmFriendStatusId(ParseConstants.FRIEND_STATUS_ACCEPTED);
                }
            }
        });

    }

    static class UserRowHolder {
        TextView userNameTV, stepsTV;
        ImageView userThumbnail;
        ImageButton inviteButton;
        ImageButton acceptButton, declineButton;
        LinearLayout friendRequestResponseLayout, inviteLayout, sentLayout;
    }
}

