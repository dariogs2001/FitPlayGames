package dariogonzalez.fitplaygames.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import dariogonzalez.fitplaygames.Friends.MainFriendsFragment;
import dariogonzalez.fitplaygames.MainChallengeFragment;
import dariogonzalez.fitplaygames.MainMoreOptionsFragment;
import dariogonzalez.fitplaygames.MainProfileFragment;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.LeaderBoard.SelectLeaderBoardFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {
    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
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
                return new MainChallengeFragment();
            case 1:
                return MainProfileFragment.newInstance();
            case 2:
                return new MainFriendsFragment();
            case 3:
//                return new LeaderBoardFragment();
                return new SelectLeaderBoardFragment();
            case 4:
                return new MainMoreOptionsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mContext.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return mContext.getString(R.string.title_section3).toUpperCase(l);
            case 4:
                return mContext.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_home_white;
            case 1:
                return R.drawable.ic_person_white;
            case 2:
                return R.drawable.ic_people_white;
            case 3:
                return R.drawable.ic_trophy;
            case 4:
                return R.drawable.ic_settings_white;
        }
        return R.drawable.ic_action_go_to_today;
    }
}