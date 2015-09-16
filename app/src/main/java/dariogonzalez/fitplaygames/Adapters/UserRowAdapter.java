package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.FriendListItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by Logan on 9/11/2015.
 */

public class UserRowAdapter extends ArrayAdapter<FriendListItem> {
    Context mContext;
    private List<FriendListItem> mFriendList = new ArrayList<>();
    private boolean isInvite;

    public UserRowAdapter(Context context, int resource, List<FriendListItem> mFriendList, boolean isInvite) {
        super(context, resource, mFriendList);
        mContext = context;
        this.mFriendList = mFriendList;
        this.isInvite = isInvite;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserRowHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.row_user, parent, false);

            holder = new UserRowHolder();
            holder.userNameTV = (TextView) row.findViewById(R.id.user_name);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);
            holder.inviteButton = (Button) row.findViewById(R.id.btn_invite);

            row.setTag(holder);
        }
        else {
            holder = (UserRowHolder) row.getTag();
        }

        final FriendListItem currentItem = mFriendList.get(position);

        if (isInvite) {
            holder.inviteButton.setVisibility(View.VISIBLE);
            final Button inviteButton = holder.inviteButton;

            int friendStatusId = currentItem.getFriendStatusId();
            if (friendStatusId == ParseConstants.FRIEND_STATUS_SENT) {
                inviteButton.setText(R.string.invite_sent);
                inviteButton.setBackgroundColor(getContext().getResources().getColor(R.color.accent));
            }


            holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Adding invitation into the DB
                    ParseObject newObject = new ParseObject(ParseConstants.CLASS_USER_FRIENDS);
                    newObject.put(ParseConstants.KEY_USER_ID, currentItem.getUserId());
                    newObject.put(ParseConstants.USER_OBJECT, currentItem.getUserObject());
                    newObject.put(ParseConstants.USER_FRIENDS_FRIEND_ID, currentItem.getFriendId());
                    newObject.put(ParseConstants.FRIEND_OBJECT, currentItem.getFriendObject());
                    newObject.put(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_SENT);

                    newObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                inviteButton.setText(R.string.invite_sent);
                                inviteButton.setBackgroundColor(getContext().getResources().getColor(R.color.accent));
                            }
                        }
                    });
                }
            });
        }

        holder.userNameTV.setText(currentItem.getUserName());
        Uri profilePicture = currentItem.getImageUri();
        if (profilePicture != null)
        {
            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
        }
        else
        {
            holder.userThumbnail.setImageResource(currentItem.getIconId());
        }

        return row;
    }

    static class UserRowHolder {
        TextView userNameTV;
        ImageView userThumbnail;
        Button inviteButton;
    }
}

