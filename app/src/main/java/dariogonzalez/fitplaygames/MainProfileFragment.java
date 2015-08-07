package dariogonzalez.fitplaygames;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.RoundedImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainProfileFragment extends android.support.v4.app.Fragment {


    public MainProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);
        RoundedImageView image = (RoundedImageView) view.findViewById(R.id.profile_image);
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile file = user.getParseFile(ParseConstants.USER_PROFILE_PICTURE);
        Uri fileUri = file != null ? Uri.parse(file.getUrl()) : null;

        if (fileUri != null)
        {
            Picasso.with(getActivity().getBaseContext()).load(fileUri.toString()).into(image);
        }
        else
        {
            image.setImageResource(R.mipmap.ic_profile);
        }

        return view;
    }


}
