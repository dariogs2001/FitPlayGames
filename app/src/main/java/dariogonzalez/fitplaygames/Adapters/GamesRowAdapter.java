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
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParentChallenge;

/**
 * Created by ChristensenKC on 11/9/2015.
 */
public class GamesRowAdapter extends ArrayAdapter<ParentChallenge> {
    Context mContext;
    private List<ParentChallenge> mGamesList = new ArrayList<>();

    public GamesRowAdapter(Context context, int resource, List<ParentChallenge> mGamesList) {
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

        final ParentChallenge currentItem = mGamesList.get(position);

        holder.challengeTitleTV.setText(currentItem.getUserChallengeName());
        holder.numberOfPlayersTV.setText(String.valueOf(1));

        return row;
    }

    static class GamesRowHolder {
        TextView challengeTitleTV, numberOfPlayersTV;
        ImageView challengeThumbnail;
    }
}

