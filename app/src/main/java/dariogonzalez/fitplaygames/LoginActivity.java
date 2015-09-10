package dariogonzalez.fitplaygames;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

    protected TextView mSignUpTextView;
    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
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

                if (userName.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
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
                                    Log.d("score", "The getFirst request failed.");
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
