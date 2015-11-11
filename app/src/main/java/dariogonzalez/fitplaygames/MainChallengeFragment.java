package dariogonzalez.fitplaygames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.GamesRowAdapter;
import dariogonzalez.fitplaygames.FrameLayout.ChallengeFrameLayout;
import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.GamesListItem;
import dariogonzalez.fitplaygames.classes.NamesIds;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.ComplexPreferences;
import dariogonzalez.fitplaygames.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainChallengeFragment extends android.support.v4.app.Fragment {
    private Button btnNewChallenge;
    private List<GamesListItem> mGamesList = new ArrayList<GamesListItem>();
    private ListView gamesResultListViewPlaying;
    private ListView gamesResultListViewPending;
    private ListView gamesResultListViewFinished;
    private LinearLayout emptyStateLayout;
    private View view;

   // private SwipeRefreshLayout swipeRefreshLayout;


    public MainChallengeFragment() {
        // Required empty public constructor
    }

    public static MainChallengeFragment newInstance(int sectionNumber) {
        MainChallengeFragment fragment = new MainChallengeFragment();
        Bundle args = new Bundle();
        args.putInt("section_num", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_challenge, container, false);

        gamesResultListViewPlaying = (ListView) view.findViewById(R.id.games_list_view_playing);
        gamesResultListViewPending = (ListView) view.findViewById(R.id.games_list_view_pending);
        gamesResultListViewFinished = (ListView) view.findViewById(R.id.games_list_view_finished);
        emptyStateLayout = (LinearLayout) view.findViewById(R.id.empty_state_friends);
/*
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        }); */
        btnNewChallenge = (Button) view.findViewById(R.id.new_challenge_btn);

        btnNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseChallengeActivity.class);
                startActivity(intent);
            }
        });

        getGamePlayingData();
        getGamePendingData();
        getGameFinishedData();
        return view;
    }

    private void getGamePlayingData() {
        if(mGamesList != null && mGamesList.size() == 0) {
            final ParseUser userObject = ParseUser.getCurrentUser();
            if(userObject != null) {
                final String userId = userObject.getObjectId();

                List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                //ParseQuery<ParseObject> query1 = new ParseQuery()
            }
        }
    }

    private void getGamePendingData() {

    }

    private void getGameFinishedData() {

    }

    private void populateListView() {
        //swipeRefreshLayout.setRefreshing(false);
        if (mGamesList.size() > 0) {
            gamesResultListViewPlaying.setVisibility(View.VISIBLE);
            gamesResultListViewPending.setVisibility(View.VISIBLE);
            gamesResultListViewFinished.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            ArrayAdapter<GamesListItem> adapter = new GamesRowAdapter(view.getContext(), R.layout.row_games, mGamesList);
            gamesResultListViewPlaying.setAdapter(adapter);
            gamesResultListViewPending.setAdapter(adapter);
            gamesResultListViewFinished.setAdapter(adapter);

            gamesResultListViewPlaying.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    Bundle extras = new Bundle();
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
            }
        else {
            gamesResultListViewPlaying.setVisibility(View.GONE);
            gamesResultListViewPending.setVisibility(View.GONE);
            gamesResultListViewFinished.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }
}
