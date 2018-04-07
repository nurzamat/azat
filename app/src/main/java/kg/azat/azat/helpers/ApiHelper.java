package kg.azat.azat.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import kg.azat.azat.AppController;
import kg.azat.azat.model.Category;
import kg.azat.azat.model.Image;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

public class ApiHelper {

    public static final String TAG = "[API]";
    public static final String AZAT_URL = "http://199.247.26.189";
    public static final String BASE_URL = AZAT_URL + "/azat/api";
    public static final String REGISTER_URL = BASE_URL + "/register";
    public static final String CATEGORIES_URL = BASE_URL + "/categories";
    public static final String POST_URL = BASE_URL + "/posts";
    public static final String CATEGORY_POSTS_URL = BASE_URL + "/category";
    public static final String SUBCATEGORY_POSTS_URL = BASE_URL + "/posts/subcategory";
    public static final String MEDIA_URL = AZAT_URL;
    public static final String IMAGES_URL = BASE_URL + "/images";
    public static final String USER_PROFILE = BASE_URL + "/user/profile";
    public static final String LOGIN_URL = BASE_URL + "/login/";
    public static final String USER = BASE_URL + "/user/_ID_";
    //order
    public static final String ORDER_CREATE = BASE_URL + "/orders/create/";
    public static final String ORDER_UPDATE = BASE_URL + "/orders/_ID_/status/_status_";
    public static final String ORDER_ITEM_ADD = BASE_URL + "/order/item/add/";
    public static final String USER_ORDERS = BASE_URL + "/user/_ID_/orders/";
    public static final String ORDER_ITEMS = BASE_URL + "/orders/_ID_/items/";
    //group chat
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    //private chat
    public static final String CHATS = BASE_URL + "/users/_ID_/chats";
    public static final String CHAT_MESSAGES = BASE_URL + "/chats/";
    public static final String CHAT_MESSAGE = BASE_URL + "/chats/";

    public JSONObject signup(String username, String email, String password) throws ApiException, IOException,
            JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        Log.i(TAG, "Sending request to: " + REGISTER_URL);
        String response = requestPost(REGISTER_URL, jsonObject, false);
        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject login(String username, String password) throws ApiException, IOException,
            JSONException {

        JSONObject jsonObject = new JSONObject();
        //username меняем на email так как на сервере problems
        jsonObject.put("username", username);
        jsonObject.put("email", username);
        jsonObject.put("password", password);

        Log.i(TAG, "Sending request to: " + LOGIN_URL);
        String response = requestPost(LOGIN_URL, jsonObject, false);
        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject getCategories(String url)
            throws ApiException, IOException, JSONException
    {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestGet(url);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject sendPostRequest(JSONObject jsonObject, String url)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestPost(url, jsonObject, false);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject sendHitcount(String url)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestGet(url);
        Log.i(TAG, "Response: " + response);

        return new JSONObject(response);
    }

    public JSONObject sendLike(String url)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestGet(url);
        Log.i(TAG, "Response: " + response);

        return new JSONObject(response);
    }

    public JSONObject editPost(String url, JSONObject jsonObject)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestPut(url, jsonObject, true);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject updateGcmID(String url, JSONObject jsonObject)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestPut(url, jsonObject, true);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject editProfile(String url, JSONObject jsonObject)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + url);
        String response = requestPut(url, jsonObject, true);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public JSONObject sendImage(String url, String image_path, boolean mode)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Image path : " + image_path);

        Log.i(TAG, "Sending request to: " + url);
        String response = multipart_request(url, image_path);

        Log.i(TAG, "Response: " + response);
        return new JSONObject(response);
    }

    public static String getMyPostsUrl(String user_id, int page)
    {
        String url = "";
        if(!user_id.equals(""))
        {
            url = POST_URL + "/user/" + user_id + "/" + page;
        }

        Log.i(TAG, "getMyPostsUrl: " + url);

        return url;
    }

    public static String getMyLikedPosts(String user_id, int page)
    {
        String url = "";
        if(!user_id.equals(""))
        {
            url = POST_URL + "/user/" + user_id + "/likes/" + page;
        }

        Log.i(TAG, "getMyLikedPosts: " + url);

        return url;
    }

    public static String getCategoryPostsUrl(int page, String params)
    {
        String url = POST_URL  + "/" + page + "/" + params;

        if(GlobalVar.Category != null && GlobalVar.Category.getSubcats() == null)
        {
            if(GlobalVar.Category.getIdParent().equals(""))
            {
                url = CATEGORY_POSTS_URL + "/" + GlobalVar.Category.getId() + "/posts?page=" + page + "&params=" + params;
            }
            else
            {
                url = SUBCATEGORY_POSTS_URL + "/" + GlobalVar.Category.getId() + "/posts?page=" + page + "&params=" + params;
            }
        }

        Log.i(TAG, "getCategoryPostsUrl: " + url);
        return url;
    }

    public static String getCartPostsUrl(int page, String order_id)
    {
        String endPoint = ORDER_ITEMS.replace("_ID_", order_id);
        return endPoint  + "?page="+page;
    }

    public static class ApiException extends Exception {

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }

    public String requestPost(String _url, JSONObject json, boolean token_auth)
            throws IOException, IllegalStateException,
            JSONException {

        String result = "";
        HttpURLConnection conn = null;
        try {
        String message = json.toString();

        //constants
        URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
        //conn.setReadTimeout( 10000 /*milliseconds*/ );
        //conn.setConnectTimeout( 15000 /* milliseconds */ );
        conn.setRequestMethod("POST");
            conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(message.getBytes().length);

        //make some HTTP header nicety
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");

        if(token_auth)
        {
            User user = AppController.getInstance().getUser();
            conn.setRequestProperty("Authorization", user.getApi_key());
        }

            //open
            conn.connect();//needed?
            //setup send
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();
            os.close();

            //response
        InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
            finally {
            if(conn != null)
            conn.disconnect();
            }

        return result;
    }

    public String requestGet(String _url)
            throws IOException, IllegalStateException,
            JSONException {

        String result = "";
        HttpURLConnection conn = null;
        try {
            //constants
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);

            //make some HTTP header nicety
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            //open
            conn.connect();//needed?

            //response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }

        return result;

    }


    public String requestPut(String _url, JSONObject json, boolean token_auth)
            throws IOException, IllegalStateException,
            JSONException {

        String result = "";
        HttpURLConnection conn = null;

        try {
            User user = AppController.getInstance().getUser();
            String message = json.toString();

            //constants
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setRequestProperty ("Content-type", "application/json; charset=UTF-8");
            if(token_auth)
                conn.setRequestProperty("Authorization", user.getApi_key());

            //open
            conn.connect();//needed?
            //setup send
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();
            os.close();

            //response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }

        return result;
    }

    public String multipart_request(String _url, String path) {

        String attachmentName = "image";
        //String attachmentFileName = "bitmap.bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        //mode = true -> post
        //mode = false -> put

        String result = "";
        HttpURLConnection httpUrlConnection = null;

        try
        {
            User user = AppController.getInstance().getUser();
            File file = new File(path);

            //Setup the request:
            URL url = new URL(_url);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpUrlConnection.setRequestProperty("Authorization", user.getApi_key());

            //Start content wrapper:
            DataOutputStream dataOS = new DataOutputStream(httpUrlConnection.getOutputStream());
            dataOS.writeBytes(twoHyphens + boundary + crlf);
            dataOS.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + file.getName() + "\"" + crlf);
            dataOS.writeBytes(crlf);

            // for bitmap
           //Convert Bitmap to ByteBuffer
           //I want to send only 8 bit black & white bitmaps
            /*
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }
            dataOS.write(pixels);
            */
            //for bin
            byte[] file_bytes =  org.apache.commons.io.FileUtils.readFileToByteArray(file);
            dataOS.write(file_bytes);

            //End content wrapper:
            dataOS.writeBytes(crlf);
            dataOS.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            //Flush output buffer:
            dataOS.flush();
            dataOS.close();

            //Get response:
            //response
            InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(httpUrlConnection != null)
                httpUrlConnection.disconnect();
        }

        return result;
    }

    //2-nd version
    public String multipart_request2(String targetURL, String path) {

        String BOUNDRY = "==================================";
        HttpURLConnection conn = null;
        String result = "";

        try {

            File file = new File(path);
            // These strings are sent in the request body. They provide information about the file being uploaded
            String contentDisposition = "Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"";
            String contentType = "Content-Type: application/octet-stream";

            // This is the standard format for a multipart request
            StringBuffer requestBody = new StringBuffer();
            requestBody.append("--");
            requestBody.append(BOUNDRY);
            requestBody.append('\n');
            requestBody.append(contentDisposition);
            requestBody.append('\n');
            requestBody.append(contentType);
            requestBody.append('\n');
            requestBody.append('\n');
            requestBody.append(new String(org.apache.commons.io.FileUtils.readFileToByteArray(file)));
            requestBody.append("--");
            requestBody.append(BOUNDRY);
            requestBody.append("--");

            // Make a connect to the server
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();

            // Put the authentication details in the request
            conn.setRequestProperty("Authorization", getApiKey());

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);

            // Send the body
            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
            dataOS.writeBytes(requestBody.toString());
            dataOS.flush();
            dataOS.close();

            // Ensure we got the HTTP 200 response code
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
            }

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;

    }

    public String responseText(String status)
    {
        if(status.equals("ACTIVATION_CODE_SENT"))
            return "Вам отправлен SMS с Вашим кодом.";
        if(status.equals("CODE_IS_USED"))
            return "Активация c этим кодом уже производилась.";
        if(status.equals("WRONG_ACTIVATION_CODE"))
            return "Неверный код активации.";
        if(status.equals("WRONG_API_KEY"))
            return "Неверный ключ API.";
        if(status.equals("ACTIVATION_PERIOD_EXPIRED"))
            return "Истек период активации.";
        if(status.equals("LOGIN_ERROR"))
            return "При входе возникла ошибка.";
        if(status.equals("USER_ALREADY_EXISTS"))
            return "Пользователь с таким номером телефона уже зарегистрирован.";
        if(status.equals("SEND_MESSAGE_ERROR"))
            return "Ошибка при попытке отправки сообщения.";
        if(status.equals("ACCOUNT_ACTIVATED"))
            return "Ваш аккаунт был активирован. Спасибо, за регистрацию.";

        return "";
    }

    public static String getApiKey()
    {
        String key = "";
        try
        {
            User user = AppController.getInstance().getUser();
            if(user != null)
                key = user.getApi_key();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            key = "";
        }

      return key;
    }

    public static void initClientUserFromServer(JSONObject response)
    {
        try
        {
            User user = new User();
            user.setId(response.getString("user_id"));
            user.setActivated(false);
            //user.setName(response.getString("name"));
            user.setUserName(response.getString("username"));
            //user.setEmail(response.getString("email"));
            //user.setPhone(response.getString("phone"));
            user.setApi_key(response.getString("token"));
            //if(response.getString("image_name") != null && !response.getString("image_name").equals(""))
            //    user.setAvatarUrl(ApiHelper.MEDIA_URL + "/profile/" + response.getString("image_name"));

            //setting user
            AppController appcon = AppController.getInstance();
            appcon.setUser(user);
            //setting gcm token
            //appcon.getPrefManager().saveToken(response.getString("gcm_registration_id"));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "initClientUserFromServer: " + ex.getMessage());
        }
    }

    @NonNull
    public static Post initPost(JSONObject obj, Category category, User user) throws JSONException
    {
        Post post = new Post();
        post.setId(obj.getString("id"));
        post.setTitle(obj.getString("title"));
        post.setContent(obj.getString("content"));
        post.setHitcount(obj.getString("hitcount"));
        post.setDate_created(obj.getString("created_at"));
        post.setPrice(obj.getString("price"));
        //post.setPriceCurrency(obj.getString("price_currency"));
        //post.setBirth_year(obj.getString("birth_year"));
        //post.setPhone(obj.getString("phone"));
        //post.setLocation(obj.getString("location"));
        post.setCategory(category);
        post.setUser(user);
        JSONArray jimages = obj.getJSONArray("images");
        if(jimages.length() > 0)
        {
            post.setThumbnailUrl(ApiHelper.MEDIA_URL + "/" + jimages.getJSONObject(0).getString("original_image"));
            // Images
            ArrayList<Image> images = new ArrayList<Image>();
            JSONObject img;
            Image image;
            for (int j = 0; j < jimages.length(); j++)
            {
                img = jimages.getJSONObject(j);
                image = new Image(img.getString("id"), ApiHelper.MEDIA_URL + "/" + img.getString("original_image"));
                images.add(image);
            }
            post.setImages(images);
        }
        return post;
    }
}