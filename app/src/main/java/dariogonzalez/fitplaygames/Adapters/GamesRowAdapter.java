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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.GamesListItem;

/**
 * Created by ChristensenKC on 11/9/2015.
 */
public class GamesRowAdapter extends ArrayAdapter<GamesListItem> {
    Context mContext;
    private List<GamesListItem> mGamesList = new ArrayList<>();

    public GamesRowAdapter(Context context, int resource, List<GamesListItem> mGamesList) {
        super(context, resource, mGamesList);
        mContext = context;
        this.mGamesList = mGamesList;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GamesRowHolder holder = null;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(R.layout.row_games, parent, false);

            holder = new GamesRowHolder();
            holder.challengeTitleTV = (TextView) row.findViewById(R.id.challenge_title);
            holder.numberOfPlayersTV = (TextView) row.findViewById(R.id.number_of_players);
            holder.challengeThumbnail = (ImageView) row.findViewById(R.id.challenge_thumbnail);

            row.setTag(holder);
        }
        else {
            holder = (GamesRowHolder) row.getTag();
        }

        final GamesListItem currentItem = mGamesList.get(position);

        holder.challengeTitleTV.setText(currentItem.getmChallengeTitle());
        holder.numberOfPlayersTV.setText(String.valueOf(currentItem.getmNumberOfPlayers()));

        Uri challengeImage = currentItem.getmImageUri();
        if(challengeImage != null)
        {
            Picasso.with(mContext).load(challengeImage.toString()).into(holder.challengeThumbnail);
        }
        else
        {
            holder.challengeThumbnail.setImageResource(currentItem.getmIconId());
        }

        return row;
    }

    static class GamesRowHolder {
        TextView challengeTitleTV, numberOfPlayersTV;
        ImageView challengeThumbnail;
    }
}

