package kg.azat.azat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import kg.azat.azat.adapter.PostViewPagerAdapter;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.lib.CirclePageIndicator;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

public class PostDetailActivity extends AppCompatActivity implements EditPostDialog.SaveListener
{
    private Post p = GlobalVar._Post;
    private Menu mOptionsMenu;
    private boolean like = false;
    User client = null;
    private int count = 0;
    //content params
    TextView hitcount, timestamp, location, title, price, price_currency, content, username, name;
    ImageButton chat, call, btnMenu;
    CircleImageView profile_image;
    boolean nav_ads;
    int position;
    public static PostDetailActivity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(p.getImages() != null && p.getImages().size() > 0)
        {
            setContentView(R.layout.activity_post_detail);
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");
        }
        else
        {
            setContentView(R.layout.activity_post_detail_no_image);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        _activity = this;
        nav_ads = getIntent().getBooleanExtra("nav_ads", false);
        position = getIntent().getIntExtra("position", 0);

        hitcount = (TextView) findViewById(R.id.hitcount);
        timestamp = (TextView) findViewById(R.id.date);
        location = (TextView) findViewById(R.id.location);
        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        price_currency = (TextView) findViewById(R.id.price_currency);
        content = (TextView) findViewById(R.id.content);
        username = (TextView) findViewById(R.id.username);
        name = (TextView) findViewById(R.id.name);
        chat = (ImageButton) findViewById(R.id.action_chat);
        call = (ImageButton) findViewById(R.id.action_call);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        //sets
        hitcount.setText(p.getHitcount());
        timestamp.setText(Utils.getTimeAgo(p.getDate_created()));
        if(!p.getLocation().equals("null"))
        location.setText(p.getLocation());
        title.setText(p.getTitle());
        content.setText(p.getContent());
        //set price
        try
        {
            String _price = p.getPrice().trim();
            if(!_price.equals("0.00"))
            {
                double number = Double.parseDouble(_price);
                int res = (int)number; //целая часть
                double res2 = number - res; //дробная часть

                if(res2 > 0)
                    price.setText(_price);
                else price.setText(""+res);

                price_currency.setText(p.getPriceCurrency());
            }

            if(!p.getUser().getAvatarUrl().equals(""))
            {
                Glide.with(this).load(p.getUser().getAvatarUrl())
                        .thumbnail(0.5f)
                        .centerCrop()
                        .placeholder(R.drawable.aka)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profile_image);
            }

            username.setText(p.getUser().getUserName());
            name.setText(p.getUser().getName());

            //buttons
            client = AppController.getInstance().getUser();

            if(client.getId().equals(p.getUser().getId()))
            {
                chat.setVisibility(View.INVISIBLE);
                call.setVisibility(View.INVISIBLE);
                btnMenu.setVisibility(View.VISIBLE);

                btnMenu.setOnClickListener(new OnImageClickListener(btnMenu.getId()));
            }
            else
            {
                chat.setVisibility(View.VISIBLE);
                call.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.INVISIBLE);

                chat.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        if(client != null)
                        {
                            if(p != null && p.getUser() != null)
                            {
                                Intent intent = new Intent(PostDetailActivity.this, MessagesActivity.class);
                                intent.putExtra("chat_id", "0");
                                intent.putExtra("interlocutor_id", p.getUser().getId());
                                intent.putExtra("post_id", p.getId());
                                intent.putExtra("name", p.getUser().getUserName());
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            Intent in = new Intent(PostDetailActivity.this, SignupActivity.class);
                            startActivity(in);
                        }
                    }
                });

                call.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        if(client != null)
                        {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + "+" + p.getPhone()));
                            startActivity(intent);
                        }
                        else
                        {
                            Intent in = new Intent(PostDetailActivity.this, SignupActivity.class);
                            startActivity(in);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        /*  // for image full screen logic
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostDetailActivity.this, "layout clicked ", Toast.LENGTH_SHORT).show();
            }
        });
        */

        ViewPagerWork();

        if(client != null && !p.getUser().getId().equals(client.getId()))
        {
            String url = ApiHelper.POST_URL + "/" + p.getId() + "/hitcount/" + client.getId();
            HitcountTask task = new HitcountTask();
            task.execute(url);
        }
    }

    private void ViewPagerWork()
    {
        try
        {
            if(p.getImages() != null && p.getImages().size() > 0)
            {
                PostViewPagerAdapter mAdapter = new PostViewPagerAdapter(PostDetailActivity.this, p.getImages());
                ViewPager mPager = (ViewPager) findViewById(R.id.pager);
                mPager.setAdapter(mAdapter);

                int color = ContextCompat.getColor(this, R.color.color_primary_green_dark);
                CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
                mIndicator.setFillColor(color);
                mIndicator.setStrokeColor(color);
                mIndicator.setRadius(15);
                mIndicator.setViewPager(mPager);
                mIndicator.setSnap(true);
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mOptionsMenu = menu;
        if(nav_ads)
        {
            getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        }
        else
        {
            if(like)
                getMenuInflater().inflate(R.menu.menu_yes_like, menu);
            else getMenuInflater().inflate(R.menu.menu_no_like, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_like)
        {
            count++;
            String url;

            MenuItem menuItem = mOptionsMenu.findItem(R.id.action_like);

            if(count < 10)
            {
                if(like)
                {
                    like = false;
                    menuItem.setIcon(R.drawable.ic_heart_outline);
                    url = ApiHelper.POST_URL + "/" + p.getId() + "/" + client.getId() + "/like/" + 0;
                }
                else
                {
                    like = true;
                    menuItem.setIcon(R.drawable.ic_heart);
                    url = ApiHelper.POST_URL + "/" + p.getId() + "/" + client.getId() + "/like/" + 1;
                }

                LikeTask task = new LikeTask();
                task.execute(url);
            }

            /* will be used
            if(client != null && !p.getUser().getId().equals(client.getId()))
            {

            }
            */
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HitcountTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                JSONObject obj = api.sendHitcount(urls[0]);
                if(obj.getBoolean("error"))
                {
                    return "error";
                }
                else
                {
                    if(obj.has("like"))
                    {
                        like = obj.getBoolean("like");
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return "error";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }

    private class LikeTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                JSONObject obj = api.sendLike(urls[0]);
                if(obj.getBoolean("error"))
                {
                    return "error";
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return "error";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }

    class OnImageClickListener implements View.OnClickListener {

        int _view_id;

        // constructor
        public OnImageClickListener(int view_id)
        {
            this._view_id = view_id;
        }

        @Override
        public void onClick(View v) {

            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, v);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.my_post_popup_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    String title = item.getTitle().toString();
                    if(title.equals("Редактировать"))
                    {
                        editPost();
                    }
                    if(title.equals("Удалить"))
                    {
                        deletePost();
                    }
                    return true;
                }
            });
            popup.show();//showing popup menu
        }

        public void deletePost()
        {
            //Toast.makeText(activity, "delete pressed", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PostDetailActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle("Удаление");

            // Setting Dialog Message
            alertDialog.setMessage("Вы действительно хотите удалить объявление?");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_menu_delete);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    // Write your code here to invoke YES event
                    //Toast.makeText(activity, "You clicked on YES", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PostDetailActivity.this, DeletePostActivity.class);
                    i.putExtra("id", p.getId());
                    i.putExtra("position", position);
                    startActivity(i);
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    //Toast.makeText(activity, "You clicked on NO", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
        public void editPost()
        {
            FragmentManager fm = getSupportFragmentManager();
            DialogFragment newFragment = EditPostDialog.newInstance(p);
            newFragment.show(fm, "dialog");
        }
    }

    private class putHttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog pdialog;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(PostDetailActivity.this, "","Загрузка...", true);
            pdialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try
            {
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                //category не трогаем пока, ноль
                jsonObject.put("idCategory", 0);
                jsonObject.put("idSubcategory", 0);
                jsonObject.put("title", p.getTitle());
                jsonObject.put("content", p.getContent());
                jsonObject.put("price", p.getPrice());
                jsonObject.put("price_currency", p.getPriceCurrency());
                jsonObject.put("api_key", ApiHelper.getApiKey());

                JSONObject obj = api.editPost(urls[0], jsonObject);
                if(obj.getBoolean("error"))
                {
                    result = "error";
                }
                else result = "";
            }
            catch (Exception ex)
            {
                Log.d("edit post", "Exception: " + ex.getMessage());
                result = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            pdialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(PostDetailActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(PostDetailActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        title.setText(p.getTitle());
                        content.setText(p.getContent());
                        price.setText(p.getPrice());
                        price_currency.setText(p.getPriceCurrency());
                    }
                });
            }
        }
    }

    @Override
    public void onSave(Post _p)
    {
        p.setTitle(_p.getTitle());
        p.setContent(_p.getContent());
        p.setPrice(_p.getPrice());
        p.setPriceCurrency(_p.getPriceCurrency());
        p.setPhone(_p.getPhone());

        putHttpAsyncTask task = new putHttpAsyncTask();
        task.execute(ApiHelper.POST_URL + "/" + p.getId());
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
