package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dariogonzalez.fitplaygames.utils.FileHelper;
import dariogonzalez.fitplaygames.classes.ParseConstants;
import dariogonzalez.fitplaygames.utils.RoundedImageView;


public class SignUpActivity extends AppCompatActivity {

    protected EditText mUserName;
    protected EditText mPassword;
    protected EditText mEmail;
    //protected TextView mError;
    protected Button mSignUpButton;
    protected Spinner mAgeRange;
    protected Spinner mGender;
    private RoundedImageView mPhoto;
    protected ToggleButton mCheckBox;
    private FloatingActionButton fab;


    protected Uri mMediaUri;
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10;//10

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mUserName = (EditText) findViewById(R.id.userNameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        //mError = (TextView) findViewById(R.id.setErrorTV);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mPhoto = (RoundedImageView) findViewById(R.id.userPhoto);
        mCheckBox = (ToggleButton) findViewById(R.id.checkBoxOutline);


        mAgeRange = (Spinner) findViewById(R.id.age_range);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.age_range_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAgeRange.setAdapter(adapter);

        mGender = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGender.setAdapter(adapter2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            });


                mCheckBox.setText(null);
        mCheckBox.setTextOn(null);
        mCheckBox.setTextOff(null);
        mSignUpButton.setEnabled(false);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //
                    mCheckBox.setText(null);
                    mCheckBox.setTextOn(null);
                    mCheckBox.setTextOff(null);
                    mCheckBox.setBackgroundResource(R.drawable.ic_check_box_orange_24px);
                    mSignUpButton.setEnabled(true);

                }else{
                    //
                    mCheckBox.setText(null);
                    mCheckBox.setTextOn(null);
                    mCheckBox.setTextOff(null);
                    mCheckBox.setBackgroundResource(R.drawable.ic_check_box_outline_blank_grey_24px);
                    mSignUpButton.setEnabled(false);
                }
            }
        });


        TextView myTextView = (TextView) findViewById(R.id.privacy_policy);
        String myString = "Read and accept the Privacy Policy.";
        int i1 = myString.indexOf("Privacy");
        int i2 = myString.indexOf(".");
        myTextView.setMovementMethod(LinkMovementMethod.getInstance());
        myTextView.setText(myString, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)myTextView.getText();
        ClickableSpan myClickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        mySpannable.setSpan(new ForegroundColorSpan(0xFF2196F3), i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String gender = mGender.getSelectedItem().toString();
                String ageRange = mAgeRange.getSelectedItem().toString();

                //mError.setError(getString(R.string.sign_up_button_error));
                if (email.equals("")) {
                    mEmail.setError(getString(R.string.email_required));
                    return;
                }
                if (userName.equals("")) {
                    mUserName.setError(getString(R.string.username_required));
                    return;
                }
                if (password.equals("")) {
                    mPassword.setError(getString(R.string.password_required));
                    return;
                }
                if (gender.contains("Gender")) {
                    TextView validateGender = (TextView)mGender.getSelectedView();
                    validateGender.setError(getString(R.string.gender_required));
                    return;
                }
                if (ageRange.equals("Age")) {
                    TextView validateAgeRange = (TextView)mAgeRange.getSelectedView();
                    validateAgeRange.setError(getString(R.string.age_range_required));
                    return;
                }


                else
                {
//                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(userName);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put(ParseConstants.USER_GENDER, gender);
                    newUser.put(ParseConstants.USER_AGE_RANGE, ageRange);

                    byte[] fileBytes = FileHelper.getByteArrayFromFile(SignUpActivity.this, mMediaUri);
                    if (fileBytes !=  null)
                    {
                        fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                        String fileName = FileHelper.getFileName(SignUpActivity.this, mMediaUri, ParseConstants.TYPE_IMAGE);
                        ParseFile file = new ParseFile(fileName, fileBytes);
                        try {
                            file.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        newUser.put(ParseConstants.USER_PROFILE_PICTURE, file);
                     }


                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                //Success
                                FitPlayGamesApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(SignUpActivity.this, FitbitAuthenticationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
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
                            Toast.makeText(SignUpActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_PHOTO_REQUEST )
            {
                if (data == null)
                {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else
                {
                    mMediaUri = data.getData();
                }

                InputStream inputStream = null;
                Log.i(TAG, "Media URI: " + mMediaUri);
            }
            else
            {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            if (mMediaUri != null)
            {
                Picasso.with(this).load(mMediaUri.toString()).resize(80, 80).into(mPhoto);
                fab.setVisibility(View.INVISIBLE);
            }
            else {
                mPhoto.setImageResource(R.drawable.ic_user);
            }
        }
        else if (resultCode != RESULT_CANCELED)
        {
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }
}
