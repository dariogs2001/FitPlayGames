package dariogonzalez.fitplaygames;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLeaderBoardFragment extends Fragment {


    public SelectLeaderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_leader_board, container, false);


        CardView hotPotatoBtn = (CardView) view.findViewById(R.id.hot_potato_leader_board);
        hotPotatoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Utils.trackData(ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO, ParseConstants.KEY_ANALYTICS_SELECT_GAME_HOT_POTATO);
                Intent intent = new Intent(getActivity(), LeadboardActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
