package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
//import com.firebase.security.token.TokenGenerator;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getSimpleName();
    protected TextView mSignUpTextView;
    protected TextView mForgotPasswordTV;
    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);
        mForgotPasswordTV = (TextView) findViewById(R.id.forgotPassword);

        String myString = getResources().getString(R.string.sign_up_text);
        int i1 = myString.indexOf("S");
        int i2 = myString.indexOf("!");
        mSignUpTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mSignUpTextView.setText(myString, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)mSignUpTextView.getText();
        ClickableSpan myClickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        mySpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mForgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        mUserName = (EditText) findViewById(R.id.userNameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);




    }

    public void onClick(View view) {

        String userName = mUserName.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();

        if (userName.equals("")) {
            mUserName.setError(getString(R.string.username_required));
            return;
        }
        if (password.equals("")) {
            mPassword.setError(getString(R.string.password_required));
            return;
        } else {
            //   setProgressBarIndeterminateVisibility(true);
            String key = "username";
            if (userName.contains("@")) {
                key = "email";
            }

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo(key, userName);
                query.getFirstInBackground(new GetCallback<ParseUser>() {

                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser == null) {
                            Log.d(TAG, "The getFirst request failed.");
                        } else {
                            String actualUsername = parseUser.getString("username");

                            ParseUser.logInInBackground(actualUsername, password, new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    setProgressBarIndeterminateVisibility(false);

                                    if (e == null) {
                                        //Success
                                        FitPlayGamesApplication.updateParseInstallation(parseUser);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage(e.getMessage())
                                                .setTitle(R.string.login_error_title)
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
    }

}
