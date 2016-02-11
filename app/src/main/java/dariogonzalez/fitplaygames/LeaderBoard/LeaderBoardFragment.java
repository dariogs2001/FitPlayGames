package dariogonzalez.fitplaygames.LeaderBoard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import dariogonzalez.fitplaygames.UserProfileActivity;
import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.LeaderboardItem;
import dariogonzalez.fitplaygames.classes.ParseConstants;

/**
 * Created by ChristensenKC on 10/7/2015.
 */
public class LeaderBoardFragment extends Fragment {
    private List<LeaderboardItem> mLeadBoardList = new ArrayList<LeaderboardItem>();
    private ListView friendsResultListView;
    private View view;
    public int mNumOfHotPotatoGames, mAveragePotatoTime, mNumOfCrownGames, mCrownTime, mNumOfHotPotatoLosses,mNumOfCrownLosses;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_leader_board_new, container, false);
        friendsResultListView = (ListView) view.findViewById(R.id.leader_board_list_view);

        showFriendsList();
        return view;
    }

    public void showFriendsList() {
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
                        for (final ParseObject userFriend : list) {
                            try {
                                final ParseUser friendObject;
                                if (userFriend.getString("FriendId").equals(userId)) {
                                    friendObject = userFriend.getParseUser(ParseConstants.USER_OBJECT).fetchIfNeeded();
                                } else {
                                    friendObject = userFriend.getParseUser(ParseConstants.FRIEND_OBJECT).fetchIfNeeded();
                                }
                                if (friendObject != null) {
                                    ParseQuery<ParseObject> challengePlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
                                    challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_ID, friendObject);
                                    challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
                                    challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> challengePlayers, ParseException e) {
                                            if (e == null) {
                                                Log.d("TEST", "here2");
                                                for (final ParseObject challengePlayer : challengePlayers) {
                                                    ParseQuery<ParseObject> challengeQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGES);
                                                    challengeQuery.whereEqualTo(ParseConstants.CHALLENGE_CHALLENGE_ID, challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_ID).getObjectId());
                                                    challengeQuery.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> challenges, ParseException e) {
                                                            if (e == null) {
                                                                Log.d("TEST", "here3");
                                                                for (final ParseObject challenge: challenges) {
                                                                    mNumOfHotPotatoGames = 0;
                                                                    mAveragePotatoTime = 0;
                                                                    mNumOfCrownGames = 0;
                                                                    mCrownTime = 0;
                                                                    mNumOfHotPotatoLosses = 0;
                                                                    mNumOfCrownLosses = 0;

                                                                    if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                                                                        mNumOfHotPotatoGames++;
                                                                        mAveragePotatoTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                                                    }
                                                                    else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                                                                        mNumOfCrownGames++;
                                                                        mCrownTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                                                    }
                                                                    ParseQuery<ParseObject> challengeEventQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_EVENTS);
                                                                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE, challenge);
                                                                    challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER, challengePlayer);
                                                                    challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(List<ParseObject> challengeEvents, ParseException e) {
                                                                            if (e == null) {
                                                                                Log.d("TEST", "here4");
                                                                                for (ParseObject challengeEvent : challengeEvents) {
                                                                                    // If the status is still playing then that means that they were "playing" when the game ended so they lost
                                                                                    if (challengeEvent.getInt(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS) == ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_PLAYING && (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO)) {
                                                                                        mNumOfHotPotatoLosses++;
                                                                                    }
                                                                                    else if (challengeEvent.getInt(ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS) == ParseConstants.CHALLENGE_EVENTS_FINAL_STATUS_DONE && (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN)) {
                                                                                        mNumOfCrownLosses++;
                                                                                    }
                                                                                }

                                                                                Log.d("TEST", "size: " + mLeadBoardList.size());
                                                                                ParseFile file = friendObject.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                                                                                final Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

                                                                                LeaderboardItem leaderboardItem = new LeaderboardItem(friendObject.getUsername(), mNumOfHotPotatoGames, mNumOfHotPotatoLosses, mAveragePotatoTime, fileUri);
                                                                                mLeadBoardList.add(leaderboardItem);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                populateListView();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }

                            } catch (ParseException ex) {
                            }
                        }
                        populateListView();
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
            friendsResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
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
                    startActivity(intent);
                }
            });
    }

    public void clearListView() {
        friendsResultListView.setAdapter(null);
        mLeadBoardList.clear();

    }

}