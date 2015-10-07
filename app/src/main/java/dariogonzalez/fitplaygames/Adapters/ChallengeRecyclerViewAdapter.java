package dariogonzalez.fitplaygames.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ParentGame;

/**
 * Created by Dario on 8/15/2015.
 */
public class ChallengeRecyclerViewAdapter  extends RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ChallengeViewHolder> {

    List<ParentGame> mParentGames;

    public ChallengeRecyclerViewAdapter(List<ParentGame> parentGames) {
        this.mParentGames = parentGames;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ChallengeViewHolder personViewHolder, final int i) {
        personViewHolder.challengeName.setText(mParentGames.get(i).getChallengeName());
//        personViewHolder.challengeName.setBackgroundResource(mParentGames.get(i).getPhotoId());
        personViewHolder.challengeThumbnail.setImageResource(mParentGames.get(i).getPhotoId());
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.challenge_card_view, viewGroup, false);
        ChallengeViewHolder pvh = new ChallengeViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return mParentGames.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView challengeName;
        ImageView challengeThumbnail;

        ChallengeViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.challenge_cv);
            challengeName = (TextView) itemView.findViewById(R.id.challenge_name);
            challengeThumbnail = (ImageView) itemView.findViewById(R.id.challenge_thumbnail);
        }
    }
}
