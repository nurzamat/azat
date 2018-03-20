package kg.azat.azat.helpers;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

import kg.azat.azat.AppController;
import kg.azat.azat.model.User;

/**
 * Created by nurzamat on 1/19/15.
 */
public class PutRequest extends StringRequest {

    public PutRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String header = HttpHeaderParser.parseCharset(response.headers);
            Log.d("header log:", header);
            // new String(response.data, header);
            return Response.success(new String(response.data, "UTF-8"),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    /* (non-Javadoc)
 * @see com.android.volley.Request#getHeaders()
 */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        User user = AppController.getInstance().getUser();
        //params.put("Accept", "application/json");
        params.put("Content-type", "application/json");
        params.put("Authorization", user.getApi_key());
        return params;
    }

}
