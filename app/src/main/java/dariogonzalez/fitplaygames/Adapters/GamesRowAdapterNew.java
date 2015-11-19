package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.GamesHeader;
import dariogonzalez.fitplaygames.classes.ParentChallenge;

/**
 * Created by dgonzalez on 11/18/15.
 */
public class GamesRowAdapterNew extends BaseAdapter {
    Context mContext;
    private List<ParentChallenge> mGamesList = new ArrayList<>();
    private List<GamesHeader> mHeadersList = new ArrayList<>();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;


    public GamesRowAdapterNew(Context context) {
        mContext = context;
    }

    public void addItem(final ParentChallenge item) {
        mGamesList.add(item);
        mHeadersList.add(null);
        notifyDataSetChanged();
    }

    public void addItem(final List<ParentChallenge> itemList) {
        mGamesList.addAll(itemList);

        for (int idx = 0; idx < itemList.size(); idx++)
        {
            mHeadersList.add(null);
        }
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final GamesHeader item) {
        mGamesList.add(null);
        mHeadersList.add(item);
        notifyDataSetChanged();
    }


    public ParentChallenge getGamesListElement(int position)
    {
        return mGamesList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mGamesList.get(position) == null ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mGamesList.size();
    }

    @Override
    public ParentChallenge getItem(int position) {
        return mGamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GamesRowHolder holder = null;
        GamesRowHolderHeader holderHeader = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    row = LayoutInflater.from(mContext).inflate(R.layout.row_games, parent, false);

                    holder = new GamesRowHolder();
                    holder.challengeTitleTV = (TextView) row.findViewById(R.id.challenge_title);
                    holder.numberOfPlayersTV = (TextView) row.findViewById(R.id.number_of_players);
                    holder.challengeThumbnail = (ImageView) row.findViewById(R.id.challenge_thumbnail);

                    row.setTag(holder);
                    break;
                case TYPE_HEADER:
                    row = LayoutInflater.from(mContext).inflate(R.layout.row_games_header, parent, false);

                    holderHeader = new GamesRowHolderHeader();
                    holderHeader.challengeTitleTV = (TextView) row.findViewById(R.id.challenge_type);
                    row.setTag(holderHeader);
                    break;
            }

        } else {
            switch (rowType) {
                case TYPE_ITEM:
                    holder = (GamesRowHolder) row.getTag();
                    break;
                case TYPE_HEADER:
                    holderHeader = (GamesRowHolderHeader) row.getTag();
                    break;
            }
        }

        final ParentChallenge currentItem = mGamesList.get(position);
        final GamesHeader currentItemHeader = mHeadersList.get(position);
        switch (rowType) {
            case TYPE_ITEM:
                holder.challengeTitleTV.setText(currentItem.getUserChallengeName());
                holder.numberOfPlayersTV.setText(String.valueOf(1));
                holder.challengeThumbnail.setImageDrawable(currentItem.getIcon());
                break;
            case TYPE_HEADER:
                holderHeader.challengeTitleTV.setText(currentItemHeader.HeaderName);
                holderHeader.challengeTitleTV.setBackgroundResource(currentItemHeader.ColorResourceId);
                break;
        }

        return row;
    }

    static class GamesRowHolder extends  GamesRowHolderHeader {
        TextView numberOfPlayersTV;
        ImageView challengeThumbnail;
    }

    static class GamesRowHolderHeader {
        TextView challengeTitleTV;
    }
}
