package kg.azat.azat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import kg.azat.azat.helpers.ActionType;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.CategoryType;
import kg.azat.azat.helpers.EndlessRecyclerOnScrollListener;
import kg.azat.azat.helpers.EndlessStraggeredGridOnScrollListener;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.JsonObjectRequest;
import kg.azat.azat.helpers.MyPreferenceManager;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.model.Param;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

public class PostsActivity extends AppCompatActivity implements DialogFilter.SearchListener{

    private static final String TAG =  PostsActivity.class.getSimpleName();
    private List<Post> postList = new ArrayList<Post>();
    private PostListAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private TextView emptyText;
    AppController appcon = null;
    public String params;
    ProgressBar spin;
    String query = "";
    static PostsActivity _postActivity = null;
    Param param = null;
    private Menu menu;
    private boolean isListView;
    private int spanCount = 1;
    boolean nav_ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        IntentWork(getIntent());

        if(_postActivity != null)
            _postActivity.finish();
        _postActivity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nav_ads = getIntent().getBooleanExtra("nav_ads", false);
        if(nav_ads)
            toolbar.setSubtitle("Мои заказы");
        else if(GlobalVar.Category != null)
        toolbar.setSubtitle(GlobalVar.Category.getName());

        isListView = false;

        recyclerView = findViewById(R.id.list);
        spin = findViewById(R.id.loading);
        emptyText = findViewById(android.R.id.empty);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        if(isListView)
          recyclerView.setLayoutManager(mLayoutManager);
        else recyclerView.setLayoutManager(mStaggeredLayoutManager);

        // specify an adapter (see also next example)
        adapter =  new PostListAdapter(this, postList, isListView, nav_ads);
        recyclerView.setAdapter(adapter);
        //end
        params = getParams();
        VolleyRequest(1);
        addOnScroll(false);
    }

    private void addOnScroll(boolean _isStraggered) {
        if(!_isStraggered)
        {
            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager)
            {
                @Override
                public void onLoadMore(int current_page) {
                    VolleyRequest(current_page);
                }
            });
        }
        else
        {
            recyclerView.addOnScrollListener(new EndlessStraggeredGridOnScrollListener(mStaggeredLayoutManager)
            {
                @Override
                public void onLoadMore(int current_page) {
                    VolleyRequest(current_page);
                }
            });
        }
    }

    @NonNull
    private String getParams()
    {
        param = new Param();
        param.setQuery(query);
        int _actionType = 0;
        CategoryType categoryType = Utils.getCategoryType(GlobalVar.Category);
        if(categoryType != null)
        {
            if(categoryType.equals(CategoryType.SELL_BUY))
                _actionType = Utils.getActionTypeValue(ActionType.SELL);
            if(categoryType.equals(CategoryType.RENT))
                _actionType = Utils.getActionTypeValue(ActionType.RENT_GIVE);
            if(categoryType.equals(CategoryType.WORK))
                _actionType = Utils.getActionTypeValue(ActionType.VACANCY);

            if(categoryType.equals(CategoryType.DATING))
            {
                MyPreferenceManager myPreferenceManager = AppController.getInstance().getPrefManager();
                param.setSex(myPreferenceManager.getDatingSex());
                param.setAge_from(myPreferenceManager.getDatingAgeFrom());
                param.setAge_to(myPreferenceManager.getDatingAgeTo());
            }
        }
        param.setActionType(_actionType);

        return Utils.getParams(param);
    }

    private void IntentWork(Intent intent)
    {
        if(intent.getStringExtra("query") != null && !intent.getStringExtra("query").equals(""))
        query = intent.getStringExtra("query");
    }

    public void VolleyRequest(int current_page) {

        if(appcon == null)
        appcon = AppController.getInstance();

        String url;
        if(nav_ads)
        {
            url = ApiHelper.getMyPostsUrl(appcon.getUser().getId(), current_page);
        }
        else
        {
            url = ApiHelper.getCategoryPostsUrl(current_page, params);
        }
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
                            JSONObject obj;
                            User user;
                            for (int i = 0; i < posts.length(); i++) {
                                try {
                                    obj = posts.getJSONObject(i);
                                    user = new User();
                                    JSONObject userObj = obj.getJSONObject("user");
                                    user.setId(userObj.getString("id"));
                                    user.setName(userObj.getString("username"));
                                    user.setUserName(userObj.getString("username"));
                                    user.setEmail(userObj.getString("email"));
                                    //user.setPhone(userObj.getString("user_phone"));
                                    //if(!obj.getString("user_image_name").equals(""))
                                    //    user.setAvatarUrl(ApiHelper.MEDIA_URL + "/profile/" + obj.getString("user_image_name"));

                                    Post post = ApiHelper.initPost(obj, GlobalVar.Category, user);
                                    postList.add(post);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();
                    if(!(postList.size() > 0))
                    {
                        recyclerView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.GONE);
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(nav_ads)
        getMenuInflater().inflate(R.menu.menu_my_posts, menu);
        else getMenuInflater().inflate(R.menu.menu_posts, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter)
        {
            // Create the fragment and show it as a dialog.
            FragmentManager fm = getSupportFragmentManager();
            DialogFragment newFragment = DialogFilter.newInstance(param);
            newFragment.show(fm, "dialog");
            return true;
        }
        if (id == R.id.action_toggle) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.action_toggle);
        if(isListView)
        {
            if(spanCount == 2)
            {
                spanCount = 1;
                recyclerView.setLayoutManager(mLayoutManager);
                adapter =  new PostListAdapter(this, postList, true, nav_ads);
                recyclerView.setAdapter(adapter);
                addOnScroll(false);

                item.setIcon(R.drawable.ic_reorder_horizontal);
                item.setTitle("Show as big list");
            }
            else
            {
                isListView = false;
                spanCount = 1;
                recyclerView.setLayoutManager(mStaggeredLayoutManager);
                adapter =  new PostListAdapter(this, postList, false, nav_ads);
                recyclerView.setAdapter(adapter);
                addOnScroll(true);
                mStaggeredLayoutManager.setSpanCount(spanCount);
                item.setIcon(R.drawable.ic_view_grid);
                item.setTitle("Show as grid");
            }
        }
        else
        {
            if(spanCount == 1)
            {
                spanCount = 2;
                recyclerView.setLayoutManager(mStaggeredLayoutManager);
                adapter =  new PostListAdapter(this, postList, false, nav_ads);
                recyclerView.setAdapter(adapter);
                addOnScroll(true);
                mStaggeredLayoutManager.setSpanCount(spanCount);
                item.setIcon(R.drawable.ic_view_list);
                item.setTitle("Show as list");
                isListView = true;
            }
        }
    }

    public void updateList(int position)
    {
        try
        {
            this.postList.remove(position);
            this.adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //DialogFilter interface
    @Override
    public void onSearch(Param _p)
    {
        // User touched the dialog's Search button
        param = _p;
        params = Utils.getParams(_p);
        postList.clear();
        adapter.notifyDataSetChanged();
        VolleyRequest(1);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        finish();
    }
}
