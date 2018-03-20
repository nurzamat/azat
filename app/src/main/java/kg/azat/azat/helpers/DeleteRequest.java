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

/**
 * Created by nurzamat on 1/19/15.
 */
public class DeleteRequest extends StringRequest {

    public DeleteRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.DELETE, url, listener, errorListener);
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
        //params.put("User-Agent", "Nintendo Gameboy");
        //params.put("Accept-Language", "fr");

        params.put("Authorization", ApiHelper.getApiKey());
        return params;
    }

}
