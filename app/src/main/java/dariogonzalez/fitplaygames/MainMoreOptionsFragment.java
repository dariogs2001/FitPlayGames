package dariogonzalez.fitplaygames;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.MoreOptionsListItem;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.FitbitHelper;
import dariogonzalez.fitplaygames.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMoreOptionsFragment extends android.support.v4.app.Fragment {

    private List<MoreOptionsListItem> mOptions = new ArrayList<MoreOptionsListItem>();
    View view;

    public MainMoreOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_more_options, container, false);

        if (mOptions.size() == 0) {
            populateMoreOptionsList();
        }
            populateListView();
            registerClickCallback();
        return view;
    }

    private void populateMoreOptionsList() {
        mOptions.add(new MoreOptionsListItem(getString(R.string.fitbit_sign_in_text), R.drawable.ic_fitbit));
        mOptions.add(new MoreOptionsListItem(getString(R.string.log_out_text), R.drawable.ic_logout));
        mOptions.add(new MoreOptionsListItem(getString(R.string.privacy_policy_text), R.drawable.ic_privacy));
//        mOptions.add(new MoreOptionsListItem("getUserLastMonthData", R.drawable.ic_launcher));
//        mOptions.add(new MoreOptionsListItem("lastSevenDaySumAndAverage", R.drawable.ic_launcher));
//        mOptions.add(new MoreOptionsListItem("getStepsRangeDateTime", R.drawable.ic_launcher));
//        mOptions.add(new MoreOptionsListItem("Update Games Test", R.drawable.ic_launcher));
    }

    private void populateListView() {
        ArrayAdapter<MoreOptionsListItem> adapter = new MoreOptionsAdapterList(this.getActivity(), R.layout.more_options_list_item);
        ListView list = (ListView)view.findViewById(R.id.more_options_list_view);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
//        final FitbitHelper fh = new FitbitHelper(getActivity());

        ListView list = (ListView)view.findViewById(R.id.more_options_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Utils.trackData(ParseConstants.KEY_ANALYTICS_MAIN_OTHER_FITBIT, ParseConstants.KEY_ANALYTICS_MAIN_OTHER_FITBIT);
                        showIntent(getActivity(), FitbitAuthenticationActivity.class);
                        break;
                    case 1:
                        Utils.trackData(ParseConstants.KEY_ANALYTICS_MAIN_OTHER_LOGOUT, ParseConstants.KEY_ANALYTICS_MAIN_OTHER_LOGOUT);
                        ParseUser.logOut();
                        FitPlayGamesApplication.unsubscribeFromPush();
                        showIntent(getActivity(), LoginActivity.class);
                        break;
                    case 2:
                        Utils.trackData(ParseConstants.KEY_ANALYTICS_MAIN_OTHER_PRIVACY, ParseConstants.KEY_ANALYTICS_MAIN_OTHER_PRIVACY);
                        showIntent(getActivity(), PrivacyPolicyActivity.class);
                        break;
//                    case 3:
//                        if (!fh.isFitbitUserAlive())
//                        {
//                            Context context = getActivity();
//                            CharSequence text = "Your are not logged in Fitbit!";
//                            int duration = Toast.LENGTH_LONG;
//
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
//                            return;
//                        }
////                        fh.getUserLastMonthData();
//                        fh.getUserLastWeekData();
//                        break;
//
//                    case 4:
//                        if (!fh.isFitbitUserAlive())
//                        {
//                            Context context = getActivity();
//                            CharSequence text = "Your are not logged in Fitbit!";
//                            int duration = Toast.LENGTH_LONG;
//
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
//                            return;
//                        }
//                        fh.lastSevenDaySumAndAverage(ParseUser.getCurrentUser().getObjectId());
//                        break;
//
//                    case 5:
//                        if (!fh.isFitbitUserAlive())
//                        {
//                            Context context = getActivity();
//                            CharSequence text = "Your are not logged in Fitbit!";
//                            int duration = Toast.LENGTH_LONG;
//
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
//                            return;
//                        }
//                        fh.getStepsRangeDateTime();
//                        break;
//
//                    case 6:
//                        ParentChallenge.updateChallenges();
////                        HotPotatoChallenge challenge = new HotPotatoChallenge(ChallengeTypeConstants.HOT_POTATO);
////                        Date ddd = challenge.generateRandomEndDate(1000, 2, new Date());
//                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showIntent(Context context, Class<?> intentClass)
    {
        Intent intent = new Intent(context, intentClass);

        if (intentClass == LoginActivity.class)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }

        startActivity(intent);
    }

    private class MoreOptionsAdapterList extends ArrayAdapter<MoreOptionsListItem>{
        Context mContext;
        public MoreOptionsAdapterList(Context context, int resource) {
            super(context, resource, mOptions);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = LayoutInflater.from(mContext).inflate(R.layout.more_options_list_item, parent, false);
            }
            MoreOptionsListItem current = mOptions.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            Picasso.with(mContext).load(current.getIcontId()).resize(55, 55).into(imageView);
//            imageView.setImageResource(current.getIcontId());
            TextView textView = (TextView) itemView.findViewById(R.id.tvText);
            textView.setText(current.getText());

            return itemView;

//            return super.getView(position, convertView, parent);
        }
    }
}
