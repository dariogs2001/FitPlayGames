package dariogonzalez.fitplaygames;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.RoundedImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainProfileFragment extends android.support.v4.app.Fragment {

    private RoundedImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private String userId;

    public MainProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);
//        userId = getArguments().getString("userId");
//        Log.d("TEST", "user id : " + userId);



        profileImage = (RoundedImageView) view.findViewById(R.id.profile_image);
        userName = (TextView) view.findViewById(R.id.user_name);
        userEmail = (TextView) view.findViewById(R.id.user_email);

        ParseUser user;
        // Check if other userid is set. If it's not, then assume this is the current user's profile
        if ( ! false) {
            user = ParseUser.getCurrentUser();
        }
        else {
            // Get user and info based off of userId
        }
        if (user != null) {
            ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
            Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

            if (fileUri != null) {
                Picasso.with(getActivity().getBaseContext()).load(fileUri.toString()).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_user);
            }

            userName.setText(user.getUsername());
            userEmail.setText(user.getEmail());
        }

        return view;
    }

    public void setterTest() {

    }
}
