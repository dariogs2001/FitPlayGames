package dariogonzalez.fitplaygames.LeaderBoard;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import dariogonzalez.fitplaygames.R;

/**
 * Created by dgonzalez on 11/14/15.
 */
public class LeaderboardPageAdapter extends FragmentPagerAdapter {
    protected Context mContext;

    public LeaderboardPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return MainActivity.PlaceholderFragment.newInstance(position + 1);

        switch (position)
        {
            case 0:
                return new LeaderBoardFragment();
            case 1:
                return new LeaderBoardFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.leaderboard_title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.leaderboard_title_section2).toUpperCase(l);
        }
        return null;
    }
//
//    public int getIcon(int position) {
//        switch (position) {
//            case 0:
//                return R.drawable.ic_home_white;
//            case 1:
//                return R.drawable.ic_person_white;
//            case 2:
//                return R.drawable.ic_people_white;
//            case 3:
//                return R.drawable.ic_trophy;
//            case 4:
//                return R.drawable.ic_settings_white;
//        }
//        return R.drawable.ic_action_go_to_today;
//    }

}