package dariogonzalez.fitplaygames;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dariogonzalez.fitplaygames.classes.ChallengeTypeConstants;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.dialogs.FullImageDialog;
import dariogonzalez.fitplaygames.utils.FileHelper;
import dariogonzalez.fitplaygames.utils.RoundedImageView;
import dariogonzalez.fitplaygames.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainProfileFragment extends android.support.v4.app.Fragment {

    private static String TAG = MainProfileFragment.class.getSimpleName();
    private RoundedImageView profileImage;
    private TextView userName, userEmail, userSteps, mPotatoGamesTV, mPotatoLossesTV, mPotatoAvgTimeTV, mCrownGamesTV, mCrownLossesTV, mCrownAvgTimeTV;
    private ImageButton placeholderProfileImage;
    private FloatingActionButton fabAddFriend, fabMessageFriend;
    private static boolean isSelf = false;
    private boolean isFriend = false;
    private String Friendship;
    private LinearLayout progressBar;
    private LinearLayout profileLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ParseUser user;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    protected Uri mMediaUri;

    private int mNumOfHotPotatoGames = 0, mNumOfHotPotatoLosses = 0, mAveragePotatoTime = 0, mNumOfCrownGames = 0, mNumOfCrownWins = 0, mCrownTime = 0;

    public MainProfileFragment() {
        // Required empty public constructor
    }

    public static MainProfileFragment newInstance() {
        MainProfileFragment fragment = new MainProfileFragment();
        isSelf = true;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);

        profileImage = (RoundedImageView) view.findViewById(R.id.profile_image);
        placeholderProfileImage = (ImageButton) view.findViewById(R.id.placeholder_profile_image);
        userName = (TextView) view.findViewById(R.id.user_name);
        userEmail = (TextView) view.findViewById(R.id.user_email);
        userSteps = (TextView) view.findViewById(R.id.user_steps);
        fabAddFriend = (FloatingActionButton) view.findViewById(R.id.fab_add_friend);
        fabMessageFriend = (FloatingActionButton) view.findViewById(R.id.fab_message_friend);
        progressBar = (LinearLayout) view.findViewById(R.id.progress_bar);
        mPotatoGamesTV = (TextView) view.findViewById(R.id.hp_num_of_games);
        mPotatoLossesTV = (TextView) view.findViewById(R.id.hp_num_of_losses);
        mPotatoAvgTimeTV = (TextView) view.findViewById(R.id.hp_avg_time);
        mCrownGamesTV = (TextView) view.findViewById(R.id.crown_num_of_games);
        mCrownLossesTV = (TextView) view.findViewById(R.id.crown_num_of_losses);
        mCrownAvgTimeTV = (TextView) view.findViewById(R.id.avg_crown_time);
        profileLayout = (LinearLayout)view.findViewById(R.id.profile_layout);

        MainScreenTask task = new MainScreenTask();
        task.execute("");

        //All the lines below are to enable the swipeRefreshLayout..
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                MainScreenTask task = new MainScreenTask();
                task.execute("");
            }
        });

        if (isSelf) {
            fabAddFriend.setVisibility(View.GONE);
            setOwnData();
            user = ParseUser.getCurrentUser();
            getAnalyticalData(user);
            isSelf = false;
        }
        else {
            // If is friend show message fab
            if (isFriend) {
                fabAddFriend.setVisibility(View.GONE);
                fabMessageFriend.setVisibility(View.VISIBLE);
                profileLayout.setBackgroundResource(R.color.primary);
                userName.setTextColor(getResources().getColor(R.color.white));
                userEmail.setTextColor(getResources().getColor(R.color.white));
                userSteps.setTextColor(getResources().getColor(R.color.white));
            }

            if (fabMessageFriend != null) {
                fabMessageFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createFriendChat(Friendship);
                    }
                });
            }
        }

        Utils.trackData(ParseConstants.KEY_ANALYTICS_PROFILE, ParseConstants.KEY_ANALYTICS_PROFILE);
        return view;
    }

    public void setUserData(String userId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user : users) {
                        if (user != null) {
                            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
                            final Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

                            if (fileUri != null) {
                                Picasso.with(getActivity().getBaseContext()).load(fileUri.toString()).into(profileImage);
                                profileImage.setVisibility(View.VISIBLE);
                                placeholderProfileImage.setVisibility(View.GONE);

                                profileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FullImageDialog dialog = new FullImageDialog(getActivity(), fileUri);
                                        dialog.show();
                                    }
                                });
                            }

                            double steps = 0;
                            if (user.has("lastSevenDays")) {
                                ParseObject lastSevenDays = null;
                                try {
                                    lastSevenDays = user.getParseObject("lastSevenDays").fetchIfNeeded();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                if (lastSevenDays != null) {
                                    steps =  lastSevenDays.getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS);
                                }
                            }
                            else {
                                steps = 0;
                            }

                            setView(user.getUsername(), user.getEmail(),  String.format("%,d", (int)steps) + " " + getResources().getString(R.string.steps));

                            getFriendObject(user);
                        }
                    }
                }
            }
        });
    }

    public void setIsFriend(boolean isFriend) {
        if (fabAddFriend != null && fabMessageFriend != null) {
            if (isFriend) {
                fabAddFriend.setVisibility(View.GONE);
                fabMessageFriend.setVisibility(View.VISIBLE);
                profileLayout.setBackgroundResource(R.color.primary);
                userName.setTextColor(getResources().getColor(R.color.white));
                userEmail.setTextColor(getResources().getColor(R.color.white));
                userSteps.setTextColor(getResources().getColor(R.color.white));
            }
        }
        else {
            this.isFriend = isFriend;
        }
    }

    public void setOwnData() {
        user = ParseUser.getCurrentUser();

        if (user != null) {
            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

            if (fileUri != null) {
                Picasso.with(getActivity().getBaseContext()).load(fileUri.toString()).into(profileImage);
                profileImage.setVisibility(View.VISIBLE);
                placeholderProfileImage.setVisibility(View.GONE);

                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(R.array.camera_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
            else {

                placeholderProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(R.array.camera_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }


            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_LAST_SEVEN_DAYS);
            query.whereEqualTo(ParseConstants.USER_OBJECT, user);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        double steps = 0;
                        if (list.size() > 0) {
                            steps =  list.get(0).getDouble(ParseConstants.LAST_SEVEN_DAYS_STEPS);
                        }
                        setView(user.getUsername(), user.getEmail(),  String.format("%,d", (int)steps) + " " + getResources().getString(R.string.steps));
                    }
                    else {
                        Log.d("TEST", e.toString());
                    }
                }
            });
        }
    }

    public void getAnalyticalData(ParseUser parseUser) {

        // ChallengePlayer class has a Challenge object. ChallengeObject has a Challenge type. If challenge type = 1 and the game wasn't cancelled then count it.
        ParseQuery<ParseObject> challengePlayerQuery = ParseQuery.getQuery(ParseConstants.CLASS_CHALLENGE_PLAYERS);
        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_USER_OBJECT, parseUser);
        challengePlayerQuery.whereEqualTo(ParseConstants.CHALLENGE_PLAYER_STATUS, ParseConstants.CHALLENGE_PLAYER_STATUS_ACCEPTED);
        challengePlayerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> challengePlayers, ParseException e) {
                if (e == null) {
                    for (final ParseObject challengePlayer : challengePlayers)
                    {
                        try {
                            final ParseObject challenge = challengePlayer.getParseObject(ParseConstants.CHALLENGE_PLAYER_CHALLENGE_OBJECT).fetchIfNeeded();

                            if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_STATUS) == ParseConstants.CHALLENGE_STATUS_FINISHED) {
                                if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.HOT_POTATO) {
                                    mNumOfHotPotatoGames++;
                                    mAveragePotatoTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                    mNumOfHotPotatoLosses +=challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_LOSER) == true ? 1 : 0;
                                } else if (challenge.getInt(ParseConstants.CHALLENGE_CHALLENGE_TYPE) == ChallengeTypeConstants.CROWN) {
                                    mNumOfCrownGames++;
                                    mCrownTime += challengePlayer.getInt(ParseConstants.CHALLENGE_PLAYER_AVERAGE_TIME);
                                    mNumOfCrownWins += challengePlayer.getBoolean(ParseConstants.CHALLENGE_PLAYER_IS_WINNER) == true ? 1 : 0;
                                }

//                                ParseQuery<ParseObject> challengeEventQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_CHALLENGE_EVENTS);
//                                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_OBJECT, challenge);
//                                challengeEventQuery.whereEqualTo(ParseConstants.CHALLENGE_EVENTS_CHALLENGE_PLAYER_OBJECT, challengePlayer);
//                                challengeEventQuery.findInBackground(new FindCallback<ParseObject>() {
//                                    @Override
//                                    public void done(List<ParseObject> challengeEvents, ParseException e) {
//                                        {
                                            int aHPTTime = 0;
                                            int aCtCTime = 0;
                                            mPotatoGamesTV.setText(String.valueOf(mNumOfHotPotatoGames));
                                            mPotatoLossesTV.setText(String.valueOf(mNumOfHotPotatoLosses));
                                            if (mAveragePotatoTime <= 0) {
                                                mAveragePotatoTime = 0;
                                            }

                                            if (mNumOfHotPotatoGames > 0) {
                                                aHPTTime = mAveragePotatoTime / mNumOfHotPotatoGames;
                                            }

                                            int hours = aHPTTime / 60;
                                            int minutes = aHPTTime % 60;
                                            String potatoTimeStr = ((hours > 0) ? hours + " Hr " : "") + minutes + " Min";
                                            mPotatoAvgTimeTV.setText(potatoTimeStr);

                                            mCrownGamesTV.setText(String.valueOf(mNumOfCrownGames));
                                            mCrownLossesTV.setText(String.valueOf(mNumOfCrownWins));
                                            if (mCrownTime <= 0) {
                                                mCrownTime = 0;
                                            }

                                            if (mNumOfCrownGames > 0) {
                                                aCtCTime = mCrownTime / mNumOfCrownGames;
                                            }

                                            int hoursC = aCtCTime / 60;
                                            int minutesC = aCtCTime % 60;
                                            String crownTimeStr = ((hoursC > 0) ? hoursC + " Hr " : "") + minutesC + " Min";
                                            mCrownAvgTimeTV.setText(crownTimeStr);
//                                        }
//                                    }
//                                });
                            }
                        }
                        catch (Exception ex)
                        {}
                    }
                }
            }
        });
    }

    private void setView(String username, String email, String steps) {
        userName.setText(username);
        userEmail.setText(email);
        userSteps.setText(steps);
    }

    private void getFriendObject(ParseUser userFriend) {
        final ParseUser userObject = ParseUser.getCurrentUser();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        ParseQuery<ParseObject> query1 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
        query1.whereEqualTo(ParseConstants.USER_OBJECT, userObject);
        query1.whereEqualTo(ParseConstants.FRIEND_OBJECT, userFriend);

        ParseQuery<ParseObject> query2 = new ParseQuery(ParseConstants.CLASS_USER_FRIENDS);
        query2.whereEqualTo(ParseConstants.FRIEND_OBJECT, userObject);
        query2.whereEqualTo(ParseConstants.USER_OBJECT, userFriend);

        queries.add(query1);
        queries.add(query2);


        ParseQuery<ParseObject> superQuery = ParseQuery.or(queries);
        superQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseObject userFriend = list.get(0);
                        Friendship = userFriend.getObjectId();
                    }
                }
            }
        });
    }

    private void createFriendChat(String friendship) {
        Intent intent = new Intent(getActivity(), MainChatActivity.class);
        intent.putExtra(ParseConstants.OBJECT_ID, friendship);
        startActivity(intent);
    }

    protected DialogInterface.OnClickListener mDialogListener;
    {
        mDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PHOTO_REQUEST: //Take picture
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        if (mMediaUri == null) {
                            //Display error
                            Toast.makeText(getActivity(), getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                        } else {
                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                        }
                        break;
                    case PICK_PHOTO_REQUEST://Choose picture
                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePhotoIntent.setType("image/*");
                        startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                        break;
                }
            }
        };
    }

    private boolean isExternalStorageAvailable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private Uri getOutputMediaFileUri(int mediaType)
    {
        if (isExternalStorageAvailable())
        {
            String appName = "Temp";
            //1. Get the external storage directory
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            //2. Create our subdirectory
            if (!mediaStorageDir.exists())
            {
                if (!mediaStorageDir.mkdir())
                {
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }
            //3. Create the file name
            //4. Create the file
            File mediaFile;
            Date now = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE)
            {
                mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
            }
            else if (mediaType == MEDIA_TYPE_VIDEO)
            {
                mediaFile = new File(path + "VID" + timeStamp + ".mp4");
            }
            else
                return null;

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            //5. Return the file's URI
            return Uri.fromFile(mediaFile);
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == PICK_PHOTO_REQUEST )
            {
                if (data == null)
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else
                {
                    mMediaUri = data.getData();
                }

                InputStream inputStream = null;
            }
            else
            {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                getActivity().sendBroadcast(mediaScanIntent);
            }

            if (mMediaUri != null)
            {
                placeholderProfileImage.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(mMediaUri.toString()).resize(80, 80).into(profileImage);
                profileImage.setVisibility(View.VISIBLE);
                if (user != null) {
                    UploadPictureAsync doTask = new UploadPictureAsync();
                    doTask.execute("");
                }
            }
            else {
                placeholderProfileImage.setImageResource(R.drawable.ic_user);
            }
        }
        else if (resultCode != Activity.RESULT_CANCELED)
        {
            Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }

    private void saveImage() {
        if (user != null) {
            byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity(), mMediaUri);
            if (fileBytes != null) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                String fileName = FileHelper.getFileName(getActivity(), mMediaUri, ParseConstants.TYPE_IMAGE);
                ParseFile file = new ParseFile(fileName, fileBytes);
                try {
                    file.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.put(ParseConstants.USER_PROFILE_PICTURE, file);
            }
            user.saveInBackground();
        }
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

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    public class UploadPictureAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            saveImage();
            return "";
        }
    }
}