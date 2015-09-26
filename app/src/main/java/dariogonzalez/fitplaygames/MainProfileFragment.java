package dariogonzalez.fitplaygames;


import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.dialogs.FullImageDialog;
import dariogonzalez.fitplaygames.utils.RoundedImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainProfileFragment extends android.support.v4.app.Fragment {

    private RoundedImageView profileImage;
    private TextView userName, userEmail, userSteps;
    private ImageButton placeholderProfileImage;
    private static boolean isSelf = false;

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

        if (isSelf) {
            Log.d("TEST", "hmmmm");
            setOwnData();
            isSelf = false;
        }
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
                        userName.setText(user.getUsername());
                        userEmail.setText(user.getEmail());
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

                            userName.setText(user.getUsername());
                            userEmail.setText(user.getEmail());
                            userSteps.setText((int)steps + " " + getResources().getString(R.string.steps));
                        }
                    }
                }
            }
        });
    }

    public void setOwnData() {
        ParseUser user = ParseUser.getCurrentUser();

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

                    }
                });
            }
            else {
                // TODO: Make placeholderProfileImage clickable to add image
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

            userName.setText(user.getUsername());
            userEmail.setText(user.getEmail());
            userSteps.setText((int)steps + " " + getResources().getString(R.string.steps));
        }
    }
}