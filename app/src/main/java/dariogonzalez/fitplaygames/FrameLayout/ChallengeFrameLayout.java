package dariogonzalez.fitplaygames.FrameLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import dariogonzalez.fitplaygames.R;

/**
 * Created by Dario on 8/21/2015.
 */
public class ChallengeFrameLayout extends LinearLayout {
    private View mView;
    ChallengeHeaderFrameLayout mChallengeHeader;
    ChallengeInfoItemFrameLayout mChallengeInfo1;
    ChallengeInfoItemFrameLayout mChallengeInfo2;

    public ChallengeFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public ChallengeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChallengeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context)
    {
        mView = View.inflate(context, R.layout.challenge_layer, this);
        mChallengeHeader = (ChallengeHeaderFrameLayout) mView.findViewById(R.id.challenge_header);
        mChallengeInfo1 = (ChallengeInfoItemFrameLayout) mView.findViewById(R.id.challenge_info_1);
        mChallengeInfo2 = (ChallengeInfoItemFrameLayout) mView.findViewById(R.id.challenge_info_2);
    }

    public void bindHeader(int backgroundColor, String challengeType)
    {
        mChallengeHeader.bind(backgroundColor, challengeType);
    }

    public void bindInfo1(String name, int numOfPlayers)
    {
        mChallengeInfo1.bind(name, Integer.toString(numOfPlayers));
    }

    public void bindInfo2(String name, int numOfPlayers)
    {
        mChallengeInfo2.bind(name, Integer.toString(numOfPlayers));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
