package dariogonzalez.fitplaygames;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity {

    protected TextView mPasswordResetTV;
    protected EditText mPasswordResetET;
    protected Button mPasswordResetBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mPasswordResetTV = (TextView) findViewById(R.id.resetPasswordTV);
        mPasswordResetET = (EditText) findViewById(R.id.resetPasswrodET);
        mPasswordResetBtn = (Button) findViewById(R.id.resetPasswordBtn);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view) {

        String actualEmail = mPasswordResetET.getText().toString().trim();

        if (actualEmail.contains("@") && actualEmail != "") {
            ParseUser.requestPasswordResetInBackground(actualEmail, new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.success_email_sent), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
        else {
            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_entry_failed), Toast.LENGTH_LONG).show();
        }
        }
}
