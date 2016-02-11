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
import dariogonzalez.fitplaygames.classes.LeaderboardItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by Logan on 2/10/2015.
 */

public class LeaderboardRowAdapter extends ArrayAdapter<LeaderboardItem> {
    Context mContext;
    private List<LeaderboardItem> mUserList = new ArrayList<>();

    public LeaderboardRowAdapter(Context context, int resource, List<LeaderboardItem> mUserList) {
        super(context, resource, mUserList);
        mContext = context;
        this.mUserList = mUserList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserRowHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.leader_board_list_item, parent, false);

            holder = new UserRowHolder();
            holder.username = (TextView) row.findViewById(R.id.user_name);
            holder.userGamesTV = (TextView) row.findViewById(R.id.num_of_games);
            holder.userLossesTV = (TextView) row.findViewById(R.id.num_of_losses);
            holder.userAvgTime = (TextView) row.findViewById(R.id.avg_time);
            holder.userThumbnail = (ImageView) row.findViewById(R.id.user_thumbnail);

            row.setTag(holder);
        }
        else {
            holder = (UserRowHolder) row.getTag();
        }

        final LeaderboardItem currentItem = mUserList.get(position);

        holder.username.setText(currentItem.getmUsername());
        holder.userGamesTV.setText(currentItem.getmNumOfGames());
        holder.userLossesTV.setText(currentItem.getmNumOfGamesLost());
        holder.userAvgTime.setText(currentItem.getmAvgTime());

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

    static class UserRowHolder {
        TextView username, userGamesTV, userLossesTV, userAvgTime;
        ImageView userThumbnail;
    }
}


