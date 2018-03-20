package kg.azat.azat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.DeleteRequest;


public class DeleteImageActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG =  "[delete image response]";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        //final int position = i.getIntExtra("position", 0);
        String image_id = i.getStringExtra("image_id");

        pDialog = new ProgressDialog(DeleteImageActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Загрузка...");
        pDialog.show();
        String url = ApiHelper.IMAGES_URL + "/" + image_id;
        //finish();
        DeleteRequest dr = new DeleteRequest(url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response);
                        if(response.equals("")) // if response is ok
                        {
                            //MyPostsFragment.adapter.deleteItem(position);
                            //MyPostsFragment.adapter.notifyDataSetChanged();
                            Toast.makeText(DeleteImageActivity.this, "Удалено", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        // hide the progress dialog
                    }
                }
        );
        hidePDialog();
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(dr);
        finish();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
