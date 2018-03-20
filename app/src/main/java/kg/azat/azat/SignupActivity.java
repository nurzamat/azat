package kg.azat.azat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import kg.azat.azat.helpers.ApiHelper;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();

    EditText _usernameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _repeat_passwordText;
    Button _signupButton;
    TextView _loginLink;
    String username;
    String email;
    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _usernameText = findViewById(R.id.input_username);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _repeat_passwordText = findViewById(R.id.input_repeat_password);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
                Intent in = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        SignupAsyncTask task = new SignupAsyncTask();
        task.execute(ApiHelper.REGISTER_URL);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        username = _usernameText.getText().toString().trim();
        email = _emailText.getText().toString().trim();
        password = _passwordText.getText().toString().trim();
        String repeat_password = _repeat_passwordText.getText().toString().trim();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if(!password.isEmpty() && !password.equals(repeat_password))
        {
            _repeat_passwordText.setError("Repeat password correctly");
            valid = false;
        } else _repeat_passwordText.setError(null);

        return valid;
    }

    private class SignupAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        private String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = ProgressDialog.show(SignupActivity.this, "", "Регистрация...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                JSONObject response = api.signup(username, email, password);
                if(response.getBoolean("error"))
                {
                    result = response.getString("message");
                }
                else
                {
                    ApiHelper.initClientUserFromServer(response);
                }
            }
            catch (Exception ex)
            {
                result = ex.getMessage();
                Log.d("Start activity", "Exception: " + result);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(SignupActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent in = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}