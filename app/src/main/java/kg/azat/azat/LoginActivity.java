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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.model.User;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _usernameText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    String username;
    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _usernameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        //1-st variant
        Log.d(TAG, "Post Params: " + username + "/" + password);
        LoginAsyncTask task = new LoginAsyncTask();
        task.execute(ApiHelper.LOGIN_URL);
        //2-nd variant
        //VolleyLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the PagedHeaderActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        username = _usernameText.getText().toString().trim();
        password = _passwordText.getText().toString().trim();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        private String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = ProgressDialog.show(LoginActivity.this, "", "Загрузка...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                JSONObject response = api.login(username, password);
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
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        }
    }

    //Login Task via Volley
    private void VolleyLogin() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiHelper.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    if(obj.getBoolean("error"))
                    {
                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        User user = new User();
                        user.setId(obj.getString("id"));
                        user.setActivated(false);
                        user.setName(obj.getString("name"));
                        user.setUserName(obj.getString("username"));
                        user.setEmail(obj.getString("email"));
                        user.setApi_key(obj.getString("api_key"));

                        AppController appcon = AppController.getInstance();
                        appcon.setUser(user);
                        appcon.getPrefManager().saveUser(user);

                        Intent in = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                Log.e(TAG, "Params: " + params.toString());

                return params;
            }
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

}