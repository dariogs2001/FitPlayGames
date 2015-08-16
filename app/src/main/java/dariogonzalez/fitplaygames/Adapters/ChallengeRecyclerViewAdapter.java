package dariogonzalez.fitplaygames.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ChallengeGame;

/**
 * Created by Dario on 8/15/2015.
 */
public class ChallengeRecyclerViewAdapter  extends RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ChallengeViewHolder> {

    List<ChallengeGame> mChallengeGames;

    public ChallengeRecyclerViewAdapter(List<ChallengeGame> challengeGames) {
        this.mChallengeGames = challengeGames;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ChallengeViewHolder personViewHolder, final int i) {
        personViewHolder.challengeName.setText(mChallengeGames.get(i).getChallengeName());
        personViewHolder.challengeName.setBackgroundResource(mChallengeGames.get(i).getPhotoId());
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.challenge_card_view, viewGroup, false);
        ChallengeViewHolder pvh = new ChallengeViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return mChallengeGames.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView challengeName;

        ChallengeViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.challenge_cv);
            challengeName = (TextView) itemView.findViewById(R.id.game_name);
        }
    }
}
