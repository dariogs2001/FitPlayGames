package dariogonzalez.fitplaygames;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.GamesRowAdapterNew;
import dariogonzalez.fitplaygames.classes.CaptureTheCrownChallenge;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.GamesHeader;
import dariogonzalez.fitplaygames.classes.HotPotatoChallenge;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.classes.ParseConstants;


public class MainChallengeFragment extends android.support.v4.app.Fragment {
    private Button btnNewChallenge;
    private List<ParentChallenge> mGamesListPlaying = new ArrayList<ParentChallenge>();
    private List<ParentChallenge> mGamesListPending = new ArrayList<ParentChallenge>();
    private List<ParentChallenge> mGamesListFinished = new ArrayList<ParentChallenge>();
    private View view;

    private ListView showAll;
    private GamesRowAdapterNew mAdapterNew;
    private LinearLayout progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mUserPermission;
    private CardView emptyMessageCardView;

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

        btnNewChallenge = (Button) view.findViewById(R.id.new_challenge_btn);

        btnNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseChallengeActivity.class);
                startActivity(intent);
            }
        });

        progressBar = (LinearLayout) view.findViewById(R.id.progress_bar);

        mGamesListPlaying.clear();
        mGamesListPending.clear();
        mGamesListFinished.clear();
        if (ParseUser.getCurrentUser() != null)
        {
            mUserPermission = ParseUser.getCurrentUser().getInt(ParseConstants.USER_PERMISSION);
        }


        emptyMessageCardView  = (CardView) view.findViewById(R.id.card_message_empty);

        mAdapterNew = new GamesRowAdapterNew(getActivity());
        showAll = (ListView) view.findViewById(R.id.show_all);

        MainScreenTask task = new MainScreenTask();
        task.execute("");


        //All the lines below are to enable the swipeRefreshLayout..
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGamesListPlaying.clear();
                mGamesListPending.clear();
                mGamesListFinished.clear();
                mAdapterNew = new GamesRowAdapterNew(getActivity());

                MainScreenTask task = new MainScreenTask();
                task.execute("");
            }
        });

        //Enabling to resfresh only when at the top of the list...
        showAll.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {

            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                int topRowVerticalPosition = (showAll == null || showAll.getChildCount() == 0) ? 0 : showAll.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    private void getGameData()
    {
        final ParseUser userObject = ParseUser.getCurrentUser();
        if(userObject != null) {
            ParseQuery<ParseObject> query1 = new ParseQuery<>(ParseConstants.CLASS_CHALLENGE_PLAYERS);
            query1.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, userObject);
            query1.orderByDescending(ParseConstants.KEY_CREATED_AT);

            try {
                List<ParseObject> challengeplayers = query1.find();
                for (ParseObject challengePlayer : challengeplayers) {
                    ParseQuery<ParseObject> query2 = new ParseQuery<>(ParseConstants.CLASS_CHALLENGES);
                    ParseObject challenge = (ParseObject) challengePlayer.get(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT);
                    query2.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challenge.getObjectId());

                    ParseObject challenge2 = query2.getFirst();

                    if (mUserPermission == ParseConstants.PERMISSION_ALL || mUserPermission == ParseConstants.PERMISSION_HOT_POTATO) {
                        if (challenge2.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                            addHotPotatoChallenge(challenge2, challenge2.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS), challengePlayer);
                        }
                    }

                    if (mUserPermission == ParseConstants.PERMISSION_ALL || mUserPermission == ParseConstants.PERMISSION_CAPTURE_THE_CROWN) {
                        if (challenge2.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                            addCaptureTheCrownChallenge(challenge2, challenge2.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS), challengePlayer);
                        }
                    }
                }
            }catch (Exception ex) {}
        }
    }

    private void addHotPotatoChallenge(ParseObject challenge, int challengeStatus, ParseObject challengePlayer) {

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_CANCELLED ||
            (challengeStatus == ParseConstants.CHALLENGE_STATUS_FINISHED && mGamesListFinished.size() >= 5)) return;

        HotPotatoChallenge hotPotatoChallenge = new HotPotatoChallenge(challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE));
        hotPotatoChallenge.setAttributesFromParseObject(challenge, getActivity());
        hotPotatoChallenge.setChallengePlayer(challengePlayer);

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING && hotPotatoChallenge.getChallengePlayer().getInt(ParseConstants.CHALLENGE_PLAYER_STATUS) == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
            mGamesListPlaying.add(hotPotatoChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING) {
            mGamesListPending.add(hotPotatoChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_FINISHED && hotPotatoChallenge.getChallengePlayer().getInt(ParseConstants.CHALLENGE_PLAYER_STATUS) == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
            mGamesListFinished.add(hotPotatoChallenge);
        }
    }

    private void addCaptureTheCrownChallenge(ParseObject challenge, int challengeStatus, ParseObject challengePlayer) {

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_CANCELLED ||
            (challengeStatus == ParseConstants.CHALLENGE_STATUS_FINISHED && mGamesListFinished.size() >= 15)) return;

        CaptureTheCrownChallenge captureTheCrownChallenge = new CaptureTheCrownChallenge(challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE));
        captureTheCrownChallenge.setAttributesFromParseObject(challenge, getActivity());
        captureTheCrownChallenge.setChallengePlayer(challengePlayer);

        if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PLAYING && captureTheCrownChallenge.getChallengePlayer().getInt(ParseConstants.CHALLENGE_PLAYER_STATUS) == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
            mGamesListPlaying.add(captureTheCrownChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_PENDING) {
            mGamesListPending.add(captureTheCrownChallenge);
        }
        else if (challengeStatus == ParseConstants.CHALLENGE_STATUS_FINISHED && captureTheCrownChallenge.getChallengePlayer().getInt(ParseConstants.CHALLENGE_PLAYER_STATUS) == ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED) {
            mGamesListFinished.add(captureTheCrownChallenge);
        }
    }

    private void populateListViewNew() {

        //Showing empty message when there are no games to show.
        if (mGamesListPlaying.size() == 0 && mGamesListPending.size() == 0 && mGamesListFinished.size() == 0)
        {
            emptyMessageCardView.setVisibility(View.VISIBLE);
            return;
        }

        if (mGamesListPlaying.size() > 0) {
            mAdapterNew.addSectionHeaderItem(new GamesHeader("Playing", R.color.primary_dark));
            mAdapterNew.addItem(mGamesListPlaying);
        }

        if (mGamesListPending.size() > 0) {
            mAdapterNew.addSectionHeaderItem(new GamesHeader("Pending", R.color.primary));
            mAdapterNew.addItem(mGamesListPending);
        }

        if (mGamesListFinished.size() > 0) {
            mAdapterNew.addSectionHeaderItem(new GamesHeader("Finished", R.color.divider_color));
            mAdapterNew.addItem(mGamesListFinished);
        }

        showAll.setAdapter(mAdapterNew);

        showAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                ParentChallenge challenge = mAdapterNew.getGamesListElement(position);
                //Is not a header
                if (challenge != null) {
                    if (challenge.getChallengeType() == ChallengeTypeConstants.HOT_POTATO) {
                        Intent intent = new Intent(getActivity(), HotPotatoDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable("game-details", (HotPotatoChallenge) challenge);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                    else if (challenge.getChallengeType() == ChallengeTypeConstants.CROWN) {
                        Intent intent = new Intent(getActivity(), CaptureTheCrownDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable("game-details", (CaptureTheCrownChallenge) challenge);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public class MainScreenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    //Only showing the progressbar when is not refreshing...
                    if (!swipeRefreshLayout.isRefreshing())
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            };
            mainHandler.post(myRunnable);

            getGameData();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    populateListViewNew();

                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            };
            mainHandler.post(myRunnable);
        }
    }
}
