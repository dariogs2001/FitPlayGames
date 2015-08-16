package dariogonzalez.fitplaygames;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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


        //TODO: find where to put this code to retrieve the fitbit information. It is working as desired...
//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), NamesIds.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        FitbitAccountInfo user = complexPreferences.getObject(NamesIds.FITBIT_ACCOUNT_INFO, FitbitAccountInfo.class);

        Button btnNewChallenge = (Button) view.findViewById(R.id.new_challenge_btn);

        btnNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseChallengeActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
