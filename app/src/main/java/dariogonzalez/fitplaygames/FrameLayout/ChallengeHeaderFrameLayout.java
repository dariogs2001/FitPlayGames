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
public class ChallengeHeaderFrameLayout extends FrameLayout {
    private LayoutInflater mInflater;
    private View mView;
    private TextView mChallengeType;
    private ImageView mChallengeViewMore;


    public ChallengeHeaderFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public ChallengeHeaderFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChallengeHeaderFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context)
    {
        mView = View.inflate(context, R.layout.challenge_header, this);
        mChallengeType = (TextView) mView.findViewById(R.id.challenge_type);
        mChallengeViewMore = (ImageView) mView.findViewById(R.id.challenge_view_more);
    }

    public void bind(int color)
    {
        mChallengeType.setBackgroundColor(color);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
