package dariogonzalez.fitplaygames;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private TextView userName, userEmail, userSteps;
    private ImageButton placeholderProfileImage;
    private FloatingActionButton fabAddFriend, fabMessageFriend;
    private static boolean isSelf = false;
    private boolean isFriend = false;

    private ParseUser user;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    protected Uri mMediaUri;

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

        if (isSelf) {
            fabAddFriend.setVisibility(View.GONE);
            setOwnData();
            isSelf = false;
        }
        else {
            // If is friend show message fab
            if (isFriend) {
                fabAddFriend.setVisibility(View.GONE);
                fabMessageFriend.setVisibility(View.VISIBLE);
            }
        }

        fabMessageFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase myFirebaseRef = new Firebase("https://fitplaygames.firebaseio.com/");
                myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");
            }
        });

        Utils.trackData(MainProfileFragment.class.getSimpleName(), ParseConstants.KEY_ANALYTICS_PROFILE);

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

                            setView(user.getUsername(), user.getEmail(), (int) steps + " " + getResources().getString(R.string.steps));
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

            setView(user.getUsername(), user.getEmail(), (int)steps + " " + getResources().getString(R.string.steps));
        }
    }

    private void setView(String username, String email, String steps) {
        userName.setText(username);
        userEmail.setText(email);
        userSteps.setText(steps);
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
        if (resultCode == getActivity().RESULT_OK)
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
                }
            }
            else {
                placeholderProfileImage.setImageResource(R.drawable.ic_user);
            }
        }
        else if (resultCode != getActivity().RESULT_CANCELED)
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

}