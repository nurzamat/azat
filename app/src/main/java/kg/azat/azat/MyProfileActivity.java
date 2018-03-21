package kg.azat.azat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.User;

public class MyProfileActivity extends AppCompatActivity {

    private String TAG = MyProfileActivity.class.getSimpleName();
    private CircleImageView profile_image;
    private EditText inputUsername, inputName, inputEmail, inputPhone;
    private TextInputLayout inputLayoutUsername, inputLayoutName, inputLayoutEmail, inputLayoutPhone;
    private Button btnSave;
    private String image_path = "";
    private boolean editImage = false;
    private boolean imagePop = true;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVar.image_paths.clear();
                GlobalVar.mSparseBooleanArray.clear();
                Intent in = new Intent(MyProfileActivity.this, MultiPhotoSelectActivity.class);
                startActivity(in);

                editImage = true;
            }
        });

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputUsername = (EditText) findViewById(R.id.input_username);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        btnSave = (Button) findViewById(R.id.btnSave);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        User user = AppController.getInstance().getUser();
        if(user != null)
        {
            if(!user.getAvatarUrl().equals(""))
            {
                Glide.with(this).load(user.getAvatarUrl())
                        .thumbnail(0.5f)
                        .centerCrop()
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profile_image);
            }

            inputUsername.setText(user.getUserName());
            inputName.setText(user.getName());
            inputEmail.setText(user.getEmail());
            inputPhone.setText(user.getPhone());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(editImage && GlobalVar.image_paths.size() > 0)
        {
            image_path = GlobalVar.image_paths.get(0);

            Glide.with(this).load(image_path)
                    .thumbnail(0.5f)
                    .centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profile_image);
        }

        editImage = false;
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }
       imagePop = true;
       updateUser();
        UpdateImage();
    }

    private void UpdateImage() {
        if(!image_path.equals(""))
        {
            String url = ApiHelper.BASE_URL + "/users/" + AppController.getInstance().getUser().getId() + "/profile/image";
            SendImageTask task = new SendImageTask();
            task.execute(url);
        }
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_phone:
                    validatePhone();
                    break;
            }
        }
    }

    private void updateUser()
    {
        final String name = this.inputName.getText().toString().trim();
        final String email = this.inputEmail.getText().toString().trim();
        final String phone = this.inputPhone.getText().toString().trim();

        user = AppController.getInstance().getUser();
        if(user != null)
        {
            if(user.getName().equals(name) && user.getEmail().equals(email) && user.getPhone().equals(phone))
            {
                return;
            }
        }

        Log.e(TAG, "endpoint: " + ApiHelper.USER_PROFILE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiHelper.USER_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error
                    if (obj.getBoolean("error") == false) {

                        if(user != null)
                        {
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            AppController.getInstance().setUser(user);
                        }
                        imagePop = false;
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Update profile error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("user_id", AppController.getInstance().getUser().getId());
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);

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

    private class SendImageTask extends AsyncTask<String, Void, String> {

        String result = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                //updating profile image
                ApiHelper api = new ApiHelper();
                JSONObject jobj = api.sendImage(urls[0], image_path, true);
                Log.e(TAG, "Update profile image: " + jobj.getString("message"));

                if(!jobj.getBoolean("error"))
                {
                    User user = AppController.getInstance().getUser();
                    user.setAvatarUrl(ApiHelper.MEDIA_URL + "/profile/" + jobj.getString("image_name"));
                    AppController.getInstance().setUser(user);
                }
                else result = "error";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                result = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("") && imagePop)
            {
                Toast.makeText(MyProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
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
