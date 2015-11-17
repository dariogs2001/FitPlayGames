package dariogonzalez.fitplaygames;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.GamesRowAdapter;
import dariogonzalez.fitplaygames.FrameLayout.ChallengeFrameLayout;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.FitbitAccountInfo;
import dariogonzalez.fitplaygames.classes.GamesListItem;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.NamesIds;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.classes.UserListItem;
import dariogonzalez.fitplaygames.utils.ComplexPreferences;
import dariogonzalez.fitplaygames.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainChallengeFragment extends android.support.v4.app.Fragment {
    private Button btnNewChallenge;
    private List<ParentChallenge> mGamesListPlaying = new ArrayList<ParentChallenge>();
    private List<ParentChallenge> mGamesListPending = new ArrayList<ParentChallenge>();
    private List<ParentChallenge> mGamesListFinished = new ArrayList<ParentChallenge>();
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

        getGameData();
        return view;
    }

    private void getGameData() {
            final ParseUser userObject = ParseUser.getCurrentUser();
            if(userObject != null) {
                ParseQuery<ParseObject> query1 = new ParseQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                query1.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_ID, userObject);
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> challengeplayers, ParseException e) {
                        if(e == null) {
                            for(ParseObject challengeplayer:challengeplayers) {
                                ParseQuery<ParseObject> query2 = new ParseQuery(ParseConstants.CLASS_CHALLENGES);
                                ParseObject challenge = (ParseObject) challengeplayer.get(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID);
                                query2.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challenge.getObjectId());
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> challenges, ParseException e) {
                                        if(e == null) {
                                            for (ParseObject challenge:challenges) {
                                                Object i = challenge.get(ParseConstants.CHALLENGE_CHALLENGE_STATUS);
                                                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                                                    addHotPotatoChallenge(challenge, challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS));
                                                }
                                            }
                                        }
                                        populateListView();
                                    }
                                });
                            }
                        }
                    }
                });
            }
    }

    private void addHotPotatoChallenge(ParseObject challenge, int challengeStatus) {
        HotPotatoChallenge hotPotatoChallenge = new HotPotatoChallenge(challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE));
        //TODO: change icon to game icon once we have it
        //hotPotatoChallenge.setIcon(challenge.get(ParseConstants.));
        hotPotatoChallenge.setChallengeId(challenge.getObjectId());
        hotPotatoChallenge.setUserChallengeName(challenge.getString(ParseConstants.CHALLENGE_CHALLENGE_NAME));
        hotPotatoChallenge.setStepsGoal(challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STEPS_GOAL));
        hotPotatoChallenge.setChallengeStatusType(challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS));
        hotPotatoChallenge.setStartDate(challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_START));
        hotPotatoChallenge.setEndDate(challenge.getDate(ParseConstants.CHALLENGE_CHALLENGE_END));

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING) {
            mGamesListPlaying.add(hotPotatoChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING) {
            mGamesListPending.add(hotPotatoChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_FINISHED) {
            mGamesListFinished.add(hotPotatoChallenge);
        }
    }

    private void populateListView() {
        Boolean isLayoutEmpty = true;

        if (mGamesListPlaying.size() > 0) {
            gamesResultListViewPlaying.setVisibility(View.VISIBLE);
            isLayoutEmpty = false;
            ArrayAdapter<ParentChallenge> adapter = new GamesRowAdapter(view.getContext(), R.layout.row_games, mGamesListPlaying);
            gamesResultListViewPlaying.setAdapter(adapter);

            gamesResultListViewPlaying.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), HotPotatoDetailsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("game-details", (HotPotatoChallenge) mGamesListPlaying.get(position));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }

        if (mGamesListPending.size() > 0) {
            gamesResultListViewPending.setVisibility(View.VISIBLE);
            isLayoutEmpty = false;
            ArrayAdapter<ParentChallenge> adapter = new GamesRowAdapter(view.getContext(), R.layout.row_games, mGamesListPending);
            gamesResultListViewPending.setAdapter(adapter);

            gamesResultListViewPending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), HotPotatoDetailsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("game-details", (HotPotatoChallenge) mGamesListPending.get(position));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }

        if (mGamesListFinished.size() > 0) {
            gamesResultListViewFinished.setVisibility(View.VISIBLE);
            isLayoutEmpty = false;
            ArrayAdapter<ParentChallenge> adapter = new GamesRowAdapter(view.getContext(), R.layout.row_games, mGamesListFinished);
            gamesResultListViewFinished.setAdapter(adapter);

            gamesResultListViewFinished.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), HotPotatoDetailsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("game-details", (HotPotatoChallenge) mGamesListFinished.get(position));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }


        if(isLayoutEmpty) {
            gamesResultListViewPlaying.setVisibility(View.GONE);
            gamesResultListViewPending.setVisibility(View.GONE);
            gamesResultListViewFinished.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }
}
