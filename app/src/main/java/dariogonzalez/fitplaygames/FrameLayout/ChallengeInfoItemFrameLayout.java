package dariogonzalez.fitplaygames.FrameLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import dariogonzalez.fitplaygames.R;

/**
 * Created by Dario on 8/21/2015.
 */
public class ChallengeInfoItemFrameLayout extends FrameLayout {

//    private LayoutInflater mInflater;
    private View mView;
    private ImageView mChallengeThumbnail;
    private TextView mChallengeName;
    private TextView mNumberOfPlayers;



    public ChallengeInfoItemFrameLayout(Context context) {
        super(context);
        initView(context);
     }

    public ChallengeInfoItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChallengeInfoItemFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context)
    {

//        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView =  View.inflate(context, R.layout.challenge_info_item, this); //mInflater.inflate(R.layout.challenge_info_item, null);

        mChallengeThumbnail = (ImageView) mView.findViewById(R.id.challenge_thumbnail);
        mChallengeName = (TextView) mView.findViewById(R.id.challenge_name);
        mNumberOfPlayers = (TextView) mView.findViewById(R.id.number_of_players);

    }

    public void bind(String name, String numOfPlayer)
    {
        mChallengeName.setText(name);
        mNumberOfPlayers.setText(numOfPlayer);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
