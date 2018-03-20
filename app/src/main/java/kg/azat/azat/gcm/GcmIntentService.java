package kg.azat.azat.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import kg.azat.azat.AppController;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.Constants;
import kg.azat.azat.model.User;

public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(TAG);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";


    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(KEY);
        switch (key) {
            case SUBSCRIBE:
                // subscribe to a topic
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;
            case UNSUBSCRIBE:
                break;
            default:
                registerGCM();
        }

    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    private void registerGCM() {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            //String senderId = getResources().getString(R.string.gcm_defaultSenderId);

            Log.e(TAG, "GCM Registration Token: " + token);

            // sending the registration id to our server
            if(!token.equals(AppController.getInstance().getPrefManager().getToken()))
            sendRegistrationToServer(token);

        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {

        // checking for valid login session
        User user = AppController.getInstance().getUser();
        if (user == null) {
            // TODO
            // user not found, redirecting him to login screen
            return;
        }

        String endPoint = ApiHelper.USER.replace("_ID_", user.getId());

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        // broadcasting token sent to server
                        Intent registrationComplete = new Intent(Constants.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);

                        AppController.getInstance().getPrefManager().saveToken(token);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("gcm_registration_id", token);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }

        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    /**
     * Subscribe to a topic
     */
    public static void subscribeToTopic(String topic) {
        try {

            FirebaseMessaging.getInstance().subscribeToTopic(topic);

        } catch (Exception e) {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(AppController.getInstance().getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeFromTopic(String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        } catch (Exception e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
