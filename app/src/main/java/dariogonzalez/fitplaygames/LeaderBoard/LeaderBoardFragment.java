package dariogonzalez.fitplaygames.LeaderBoard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.LeaderboardRowAdapter;
import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.LeaderboardItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by ChristensenKC on 10/7/2015.
 */
public class LeaderBoardFragment extends Fragment {
    private List<LeaderboardItem> mLeadBoardList = new ArrayList<LeaderboardItem>();
    private ListView friendsResultListView;
    private LinearLayout progressBar;
    private View view;
    public int ChallengeType = ChallengeTypeConstants.HOT_POTATO;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.HotPotatoTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        view = localInflater.inflate(R.layout.fragment_leader_board_new, container, false);
        // Inflate the layout for this fragment
        //view = localInflater.inflate(R.layout.fragment_leader_board_new, container, false);
        friendsResultListView = (ListView) view.findViewById(R.id.leader_board_list_view);
        progressBar = (LinearLayout) view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        showFriendsList(ChallengeType);
        return view;
    }

    public void showFriendsList(final int challengeType) {
        if (mLeadBoardList!= null && mLeadBoardList.size() == 0) {
            final ParseUser userObject = ParseUser.getCurrentUser();
            if (userObject != null) {
                final String userId = userObject.getObjectId();

                List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                ParseQuery<ParseObject> query1 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query1.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query1.whereEqualTo(ParseConstants.USER_OBJECT, userObject);

                ParseQuery<ParseObject> query2 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query2.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_SENT);
                query2.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);

                ParseQuery<ParseObject> query3 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
                query3.whereEqualTo(ParseConstants.USER_FRIENDS_STATUS, ParseConstants.FRIEND_STATUS_ACCEPTED);
                query3.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);

                queries.add(query1);
                queries.add(query2);
                queries.add(query3);

                ParseQuery<ParseObject> superQuery = ParseQuery.or(queries);
                superQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        Log.d("TEST", "here1");
                        List<ParseUser> usersList = new ArrayList<ParseUser>();
                        usersList.add(userObject);

                        for (final ParseObject userFriend : list) {
                            try {
                                final ParseUser friendObject;
                                if (userFriend.getString("FriendId").equals(userId)) {
                                    friendObject = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                                } else {
                                    friendObject = userFriend.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                                }

                                if (friendObject != null) {
                                    usersList.add(friendObject);
                                }

                            } catch (Exception ex) {

                            }
                        }

                        for (final ParseUser friendObject :  usersList) {
                            ParseQuery<ParseObject> challengePlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                            challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, friendObject);
                            challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
                            challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> challengePlayers, ParseException e) {
                                    if (e == null) {

                                        int finalResults = 0;
                                        int avgTime = 0;
                                        int numGames = 0;// challengePlayers.size();
                                        ParseFile file = friendObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                        Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;
                                        for (ParseObject challengePlayer : challengePlayers) {
                                            try {
                                                ParseObject challengeObject = challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();

                                                if (challengeObject.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == challengeType)
                                                {
                                                    numGames++;

                                                    avgTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                                    if (challengeObject.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                                                        finalResults += challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_LOSER) == true ? 1 : 0;
                                                    }
                                                    else
                                                    {
                                                        finalResults += challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_WINNER) == true ? 1 : 0;
                                                    }
                                                }
                                            } catch (Exception ex){}

                                        }
                                        if (numGames != 0) {
                                            avgTime = avgTime / numGames;
                                        }
                                        LeaderboardItem leaderboardItem = new LeaderboardItem(friendObject.getUsername(), numGames, avgTime, fileUri, finalResults, challengeType);
                                        if (leaderboardItem.getmNumOfGames() != 0) {
                                            if (mLeadBoardList.size() == 0) {
                                                mLeadBoardList.add(leaderboardItem);
                                            } else if (mLeadBoardList.size() >= 10) {
                                                return;
                                            } else {
                                                int size = mLeadBoardList.size();
                                                for (int i = 0; i < size; i++) {
                                                    // High score is actually the lowest avg time
                                                    int highScore = mLeadBoardList.get(i).getmAvgTime();
                                                    int numOfGames = mLeadBoardList.get(i).getmNumOfGames();
                                                    if (leaderboardItem.getmAvgTime() < highScore || (leaderboardItem.getmAvgTime() == highScore && leaderboardItem.getmNumOfGames() > numOfGames)) {
                                                        Log.d("TEST", "username: " + leaderboardItem.getmUsername());
                                                        mLeadBoardList.add(i, leaderboardItem);
                                                        break;
                                                    } else if (i == (size - 1)) {
                                                        mLeadBoardList.add(leaderboardItem);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        populateListView();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        else {
            populateListView();
        }
    }

    private void populateListView() {
        ArrayAdapter<LeaderboardItem> adapter = new LeaderboardRowAdapter(view.getContext(), R.layout.leader_board_list_item, mLeadBoardList);
        friendsResultListView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        friendsResultListView.setVisibility(View.VISIBLE);

//            friendsResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//                    Bundle extras = new Bundle();
//                    // Parse friend object Id
//                    extras.putString("userId", mLeadBoardList.get(position).getmFriendObject().getObjectId());
//                    extras.putString("username", mLeadBoardList.get(position).getmFriendObject().getUsername());
//                    boolean isFriend = false;
//                    if (mLeadBoardList.get(position).getmFriendStatusId() == ParseConstants.FRIEND_STATUS_ACCEPTED) {
//                        isFriend = true;
//                    }
//                    extras.putBoolean("isFriend", isFriend);
//                    intent.putExtras(extras);
//                    startActivity(intent);
//                }
//            });
    }

    public void clearListView() {
        friendsResultListView.setAdapter(null);
        mLeadBoardList.clear();
    }
}