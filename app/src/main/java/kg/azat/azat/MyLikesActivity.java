package kg.azat.azat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import kg.azat.azat.adapter.PostListAdapter;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.JsonObjectRequest;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.model.Category;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

public class MyLikesActivity extends AppCompatActivity {

    private static final String TAG =  "[my_posts response]";
    private List<Post> postList = new ArrayList<Post>();
    private PostListAdapter adapter;
    private TextView emptyText;
    AppController appcon;
    public int next = 1;
    public String user_id = "";
    ProgressBar spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_likes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_exit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        emptyText = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);
        adapter = new PostListAdapter(this, postList, true, false);
        //listView.setAdapter(adapter);
        spin = (ProgressBar) findViewById(R.id.loading);

        appcon = AppController.getInstance();
        if(appcon.getUser() != null)
            user_id = appcon.getUser().getId();

        VolleyRequest(ApiHelper.getMyLikedPosts(user_id, 1));
        listView.setOnScrollListener(new EndlessScrollListener(1));
    }

    public void VolleyRequest(String url) {

        if(appcon == null)
            appcon = AppController.getInstance();

        spin.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(!response.getBoolean("error"))
                    {
                        JSONArray posts = response.getJSONArray("posts");
                        if(posts.length() > 0)
                        {
                            next = next + 1;
                            JSONObject obj;
                            User user;
                            Category category;
                            for (int i = 0; i < posts.length(); i++) {
                                try {

                                    obj = posts.getJSONObject(i);
                                    user = new User();
                                    user.setId(obj.getString("user_id"));
                                    user.setName(obj.getString("user_name"));
                                    user.setUserName(obj.getString("user_username"));
                                    user.setEmail(obj.getString("user_email"));
                                    user.setPhone(obj.getString("user_phone"));
                                    if(!obj.getString("user_image_name").equals(""))
                                        user.setAvatarUrl(ApiHelper.MEDIA_URL + "/profile/" + obj.getString("user_image_name"));

                                    category = Utils.getCategoryByIds(obj.getString("id_category"), obj.getString("id_subcategory"));
                                    Post post = ApiHelper.initPost(obj, category, user);
                                    postList.add(post);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else next = 0;
                    }
                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();
                    if(!(postList.size() > 0))
                        emptyText.setText(R.string.no_posts);

                    spin.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                spin.setVisibility(View.GONE);
            }
        });
        // Adding request to request queue
        appcon.addToRequestQueue(jsonObjReq);
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if (next > 0)
                    VolleyRequest(ApiHelper.getMyLikedPosts(user_id, next));
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

}
