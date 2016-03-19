package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.UserListItem;

/**
 * Created by Logan on 10/23/2015.
 */
public class ChallengeInviteAdapter extends ArrayAdapter<UserListItem> {

    private Context mContext;
    private List<UserListItem> mFriendList = new ArrayList<>();

    public ChallengeInviteAdapter(Context context, int resource, List<UserListItem> friendList) {
        super(context, resource, friendList);
        this.mContext = context;
        this.mFriendList = friendList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChallengeInviteHolder holder = null;
        final UserListItem currentItem = mFriendList.get(position);

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.row_challenge_invite, parent, false);

            holder = new ChallengeInviteHolder();
            holder.userNameTV = (TextView) row.findViewById(R.id.user_name);
            holder.stepsTV = (TextView) row.findViewById(R.id.tv_steps);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);
            holder.selectedCheckbox = (CheckBox) row.findViewById(R.id.selected_checkbox);

            row.setTag(holder);
        }
        else
        {
            holder = (ChallengeInviteHolder) row.getTag();
            holder.selectedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    currentItem.setChecked(checked);
                }
            });
        }


        holder.userNameTV.setText(currentItem.getmFriendObject().getUsername());
        holder.stepsTV.setText(String.format("%,d", (int) currentItem.getmSteps()));
        Uri profilePicture = currentItem.getmImageUri();
        if (profilePicture != null)
        {
            Picasso.with(mContext).load(profilePicture.toString()).into(holder.userThumbnail);
        }
        else {
            holder.userThumbnail.setImageResource(currentItem.getmIconId());
        }

        holder.selectedCheckbox.setChecked(currentItem.getChecked());

        return row;
    }

    static class ChallengeInviteHolder {
        TextView userNameTV, stepsTV;
        ImageView userThumbnail;
        CheckBox selectedCheckbox;
    }
}
