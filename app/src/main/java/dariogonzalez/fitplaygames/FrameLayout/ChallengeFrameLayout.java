package dariogonzalez.fitplaygames.FrameLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
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
    }

    public ChallengeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChallengeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context)
    {
        mView = View.inflate(context, R.layout.challenge_layer, this);
        mChallengeHeader = (ChallengeHeaderFrameLayout) mView.findViewById(R.id.challenge_header);
        mChallengeInfo1 = (ChallengeInfoItemFrameLayout) mView.findViewById(R.id.challenge_info_1);
        mChallengeInfo2 = (ChallengeInfoItemFrameLayout) mView.findViewById(R.id.challenge_info_2);
    }

    public void bindHeader(int color)
    {
        mChallengeHeader.bind(color);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 0.5);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // set again the dimensions, this time calculated as we want
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() / 2);
        // this is required because the children keep the super class calculated dimensions (which will not work with the new MyFrameLayout sizes)
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            // this works because you set the dimensions of the ImageView to FILL_PARENT
            v.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

    }
}
