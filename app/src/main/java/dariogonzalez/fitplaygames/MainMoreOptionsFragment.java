package dariogonzalez.fitplaygames;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.classes.MoreOptionsListItem;
import dariogonzalez.fitplaygames.utils.FitbitHelper;


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
        mOptions.add(new MoreOptionsListItem(getString(R.string.fitbit_sign_in_text), R.mipmap.fitbit_white));
        mOptions.add(new MoreOptionsListItem(getString(R.string.log_out_text), R.mipmap.logout));
        mOptions.add(new MoreOptionsListItem(getString(R.string.privacy_policy_text), R.mipmap.privacy));
        mOptions.add(new MoreOptionsListItem("Test", R.drawable.ic_launcher));
    }

    private void populateListView() {
        ArrayAdapter<MoreOptionsListItem> adapter = new MoreOptionsAdapterList(this.getActivity(), R.layout.more_options_list_item);
        ListView list = (ListView)view.findViewById(R.id.more_options_list_view);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView)view.findViewById(R.id.more_options_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showIntent(getActivity(), FitbitAuthenticationActivity.class);
                        break;
                    case 1:
                        ParseUser.logOut();
                        showIntent(getActivity(), LoginActivity.class);
                        break;
                    case 2:
                        showIntent(getActivity(), PrivacyPolicyActivity.class);
                        break;
                    case 3:
                        FitbitHelper fh = new FitbitHelper(getActivity());
                        fh.getUserLastMonthData();
                        fh.lastSevenDaySumAndAverage(ParseUser.getCurrentUser().getObjectId());

                        break;
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
