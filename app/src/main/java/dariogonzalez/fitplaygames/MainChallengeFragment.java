package dariogonzalez.fitplaygames;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.NamesIds;
import dariogonzalez.fitplaygames.utils.ComplexPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainChallengeFragment extends android.support.v4.app.Fragment {


    public MainChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_challenge, container, false);

//        FitbitAccountInfo ai = new FitbitAccountInfo("1", "1", "1");
//        ComplexPreferences cp = ComplexPreferences.getComplexPreferences(getActivity(), NamesIds.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        cp.putObject(NamesIds.FITBIT_ACCOUNT_INFO, ai);
//        cp.commit();
//
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), NamesIds.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        FitbitAccountInfo user = complexPreferences.getObject(NamesIds.FITBIT_ACCOUNT_INFO, FitbitAccountInfo.class);
        return view;
    }
}
