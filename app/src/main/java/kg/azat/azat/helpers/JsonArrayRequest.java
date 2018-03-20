package kg.azat.azat.helpers;

/**
 * Created by User on 23.12.2014.
 */

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class JsonArrayRequest extends JsonRequest<JSONArray> {

    public JsonArrayRequest(String url, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

@Override
protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String header = HttpHeaderParser.parseCharset(response.headers);
            Log.d("JsonArrayLog:", header);
           // String jsonString = new String(response.data, header);
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
           return Response.error(new ParseError(je));
        }
    }
}
