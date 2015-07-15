package dariogonzalez.fitplaygames;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;


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
                return new MainProfileFragment();
            case 2:
                return new MainFriendsFragment();
            case 3:
                return new MainMoreOptionsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
       return 4;
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
                return mContext.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_action_home;
            case 1:
                return R.drawable.ic_action_person;
            case 2:
                return R.drawable.ic_action_group;
            case 3:
                return R.drawable.ic_action_list;
        }
        return R.drawable.ic_action_go_to_today;
    }
}