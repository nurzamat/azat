package kg.azat.azat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kg.azat.azat.adapter.PostViewPagerAdapter;
import kg.azat.azat.helpers.ActionType;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.CategoryType;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.lib.CirclePageIndicator;
import kg.azat.azat.model.Category;
import kg.azat.azat.model.User;

public class AddPostActivity extends AppCompatActivity {

    private String TAG = AddPostActivity.class.getSimpleName();

    String title = "";
    String content = "";
    String price = "";
    String price_currency = "";
    String phone = "";
    String region = "";
    String location = "";
    EditText etContent, etPrice, etTitle, etBirth_year, etPhone;
    Button categoryBtn, postBtn;
    Category category = null;
    Spinner price_spinner, action_spinner, sex_spinner, region_spinner, city_spinner;
    LinearLayout city_layout, sex_layout, price_layout;
    int actionType = 0;
    int actionPos = 0;
    int sex = 2; //0 - female, 1 - male
    String birth_year = "";
    String idCategory, idSubcategory;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        context = AddPostActivity.this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, MultiPhotoSelectActivity.class);
                startActivity(in);
            }
        });

        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_exit));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etTitle = (EditText) findViewById(R.id.title);
        etContent = (EditText) findViewById(R.id.content);
        etPrice = (EditText) findViewById(R.id.price);
        postBtn = (Button) findViewById(R.id.btnPost);
        categoryBtn = (Button) findViewById(R.id.btnCategory);
        etTitle.setText(title);
        etContent.setText(content);
        etPrice.setText(price);
        //spinner job
        action_spinner = (Spinner) findViewById(R.id.action_spinner);
        price_spinner = (Spinner) findViewById(R.id.price_spinner);

        //start Dating
        etBirth_year = (EditText) findViewById(R.id.birth_year);
        sex_spinner = (Spinner) findViewById(R.id.sex_spinner);
        etBirth_year.setText(birth_year);
        //end Dating
        etPhone = (EditText) findViewById(R.id.phone);
        User user = AppController.getInstance().getUser();
        if(user != null)
            etPhone.setText(user.getPhone());
        //layouts
        city_layout = (LinearLayout)findViewById(R.id.city_layout);
        sex_layout = (LinearLayout)findViewById(R.id.sex_layout);
        price_layout = (LinearLayout)findViewById(R.id.price_layout);
        initLocationSpinners();

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postButton();
            }
        });

        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AddPostActivity.this, CategoriesActivity.class);
                startActivity(in);
            }
        });

        GlobalVar.image_paths.clear();
    }

    private void initLocationSpinners()
    {
        region_spinner = (Spinner) findViewById(R.id.region_spinner);
        city_spinner = (Spinner) findViewById(R.id.city_spinner);
        ArrayAdapter<CharSequence> region_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                R.array.regions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        region_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        region_spinner.setAdapter(region_adapter);
        region_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        if(pos != 0)
                        region = parent.getItemAtPosition(pos).toString();
                        initLocationCity(pos);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );
    }

    private void initLocationCity(int pos)
    {
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = city_layout.getLayoutParams();

        if(pos == 0)
        {
            city_spinner.setVisibility(View.INVISIBLE);
            params.height = 0;
        }
        else
        {
            // Changes the height and width to the specified *pixels*
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            city_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                    R.array.chuy, android.R.layout.simple_spinner_item);

            if(pos == 2)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.issyk, android.R.layout.simple_spinner_item);
            }
            if(pos == 3)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.naryn, android.R.layout.simple_spinner_item);
            }
            if(pos == 4)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.talas, android.R.layout.simple_spinner_item);
            }
            if(pos == 5)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.jalalabad, android.R.layout.simple_spinner_item);
            }
            if(pos == 6)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.osh, android.R.layout.simple_spinner_item);
            }
            if(pos == 7)
            {
                city_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.batken, android.R.layout.simple_spinner_item);
            }
            // Specify the layout to use when the list of choices appears
            city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            city_spinner.setAdapter(city_adapter);
            city_spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {

                                location = parent.getItemAtPosition(pos).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
        city_layout.setLayoutParams(params);
    }

    private void ViewPagerWork() {
        int color = ContextCompat.getColor(this, R.color.blue);
        PagerAdapter mAdapter = new PostViewPagerAdapter(AddPostActivity.this);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setFillColor(color);
        mIndicator.setStrokeColor(color);
        mIndicator.setRadius(15);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("AddPostActivity", "resumed");
        if(GlobalVar.Category != null && GlobalVar.Category.getSubcats() == null)
        {
            categoryBtn.setText(GlobalVar.Category.getName());
            this.category = GlobalVar.Category;
            CategoryType catType = Utils.getCategoryType(this.category);
            priceSpinner(catType);
            actionSpinner(catType);
            //Dating
            dating(catType);
        }
        else this.category = null;
        ViewPagerWork();
    }

    private void dating(CategoryType catType)
    {
        ViewGroup.LayoutParams params = sex_layout.getLayoutParams();

        if(catType.equals(CategoryType.DATING))
        {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            etBirth_year.setVisibility(View.VISIBLE);
            sex_spinner.setVisibility(View.VISIBLE);

            ArrayAdapter<CharSequence> sex_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                    R.array.sex, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            sex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            sex_spinner.setAdapter(sex_adapter);
            sex_spinner.setSelection(actionPos);
            sex_spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {

                            actionPos = pos;
                            // Showing selected spinner item
                            //Toast.makeText(parent.getContext(), "Selected: " + pos, Toast.LENGTH_LONG).show();
                            if (pos == 1)
                                sex = 1;
                            if (pos == 2)
                                sex = 0;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
        else
        {
            params.height = 0;
            etBirth_year.setVisibility(View.INVISIBLE);
            sex_spinner.setVisibility(View.INVISIBLE);
        }
        sex_layout.setLayoutParams(params);
    }

    private void actionSpinner(final CategoryType catType)
    {
        if(catType.equals(CategoryType.SELL_BUY) || catType.equals(CategoryType.RENT) || catType.equals(CategoryType.WORK))
        {
            //action
            action_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> action_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                    R.array.buy_sell, android.R.layout.simple_spinner_item);

            if(catType.equals(CategoryType.RENT))
            {
                action_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.rent_get_give, android.R.layout.simple_spinner_item);
            }
            if(catType.equals(CategoryType.WORK))
            {
                action_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                        R.array.work_resume_vacancy, android.R.layout.simple_spinner_item);
            }

            // Specify the layout to use when the list of choices appears
            action_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            action_spinner.setAdapter(action_adapter);

            action_spinner.setSelection(actionPos);
            action_spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {

                            actionPos = pos;
                            // Showing selected spinner item
                            //Toast.makeText(parent.getContext(), "Selected: " + pos, Toast.LENGTH_LONG).show();
                            if(catType.equals(CategoryType.SELL_BUY))
                            {
                                if (pos == 0)
                                    actionType = Utils.getActionTypeValue(ActionType.SELL);
                                if (pos == 1)
                                    actionType = Utils.getActionTypeValue(ActionType.BUY);
                            }
                            if(catType.equals(CategoryType.RENT))
                            {
                                if (pos == 0)
                                    actionType = Utils.getActionTypeValue(ActionType.RENT_GIVE);
                                if (pos == 1)
                                    actionType = Utils.getActionTypeValue(ActionType.RENT_GET);
                            }
                            if(catType.equals(CategoryType.WORK))
                            {
                                if (pos == 0)
                                    actionType = Utils.getActionTypeValue(ActionType.VACANCY);
                                if (pos == 1)
                                    actionType = Utils.getActionTypeValue(ActionType.RESUME);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
        else action_spinner.setVisibility(View.INVISIBLE);
    }

    private void priceSpinner(final CategoryType catType)
    {
        ViewGroup.LayoutParams params = price_layout.getLayoutParams();

        if(catType.equals(CategoryType.SELL_BUY) || catType.equals(CategoryType.RENT))
        {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            //price
            etPrice.setVisibility(View.VISIBLE);
            // Create an ArrayAdapter using the string array and a default spinner layout
            price_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> price_adapter = ArrayAdapter.createFromResource(AddPostActivity.this,
                    R.array.price_currencies, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            price_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            price_spinner.setAdapter(price_adapter);
            price_spinner.setSelection(price_adapter.getPosition(price_currency));
            price_spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {
                            // On selecting a spinner item
                            price_currency = parent.getItemAtPosition(pos).toString();

                            // Showing selected spinner item
                            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
        else
        {
            etPrice.setVisibility(View.INVISIBLE);
            price_spinner.setVisibility(View.INVISIBLE);
            params.height = 0;
        }
        price_layout.setLayoutParams(params);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("AddPostActivity", "paused");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_post, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void postButton() {
        if(!validate())
        {
            Toast.makeText(AddPostActivity.this, "Заполните все поля", Toast.LENGTH_LONG).show();
        }
        else
        {
            //AddPostTask task = new AddPostTask();
            //task.execute(ApiHelper.POST_URL);
            //volley
            sendPost();
        }
    }

    private boolean validate(){

        boolean val = true;
        title = etTitle.getText().toString().trim();
        content = etContent.getText().toString().trim();
        birth_year = etBirth_year.getText().toString().trim();
        price = etPrice.getText().toString().trim();
        phone = etPhone.getText().toString().trim();

        if(content.equals("") || category == null)
        {
            etContent.setError("Введите описание");
            val = false;
        }
        if(etPrice.getVisibility() == View.VISIBLE && price.equals(""))
        {
            etPrice.setError("Укажите цену");
            val = false;
        }

        if(price_spinner.getVisibility() == View.VISIBLE && price_currency.equals(""))
        {
            val = false;
        }
        //dating
        if(sex_spinner.getVisibility() == View.VISIBLE && (sex == 2 || sex_spinner.getSelectedItemPosition() == 0))
        {
            ((TextView)sex_spinner.getSelectedView()).setError("Укажите ваш пол");
            val = false;
        }

        if(etBirth_year.getVisibility() == View.VISIBLE && birth_year.equals(""))
        {
            etBirth_year.setError("Укажите год рождения");
            val = false;
        }

        //location
        if(region_spinner.getSelectedItemPosition() == 0)
        {
            ((TextView)region_spinner.getSelectedView()).setError("Укажите местность");
            val = false;
        }

        return val;
    }

    private void sendPost()
    {
        if(category.getIdParent().equals(""))
        {
            idCategory = category.getId();
            idSubcategory = "0";
        }
        else
        {
            idCategory = category.getIdParent();
            idSubcategory = category.getId();
        }
        if(price.equals(""))
            price = "0.00";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiHelper.POST_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false)
                    {
                        String new_post_id = "";
                        if(obj.has("post_id"))
                            new_post_id = obj.getString("post_id");
                        if(GlobalVar.image_paths.size() == 0)
                        {
                            Toast.makeText(AddPostActivity.this, "Добавлено", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if(!new_post_id.equals("") && GlobalVar.image_paths.size() > 0)
                        {
                            String url = ApiHelper.POST_URL + "/" + new_post_id + "/images";
                            SendImagesTask task = new SendImagesTask();
                            task.execute(url);
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("idCategory", idCategory);
                params.put("idSubcategory", idSubcategory);
                params.put("title", title);
                params.put("content", content);
                params.put("price", price);
                params.put("price_currency", price_currency);
                params.put("actionType", String.valueOf(actionType));
                params.put("sex", String.valueOf(sex));
                params.put("birth_year", birth_year);
                params.put("phone", phone);
                params.put("region", region);
                params.put("location", location);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Accept", "application/json");
                //params.put("Content-type", "application/json");
                params.put("Authorization", AppController.getInstance().getUser().getApi_key());
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

    private class SendImagesTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String result = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(AddPostActivity.this, "","Загрузка...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                int length = GlobalVar.image_paths.size();
                if(length > 0)
                {
                    ApiHelper api = new ApiHelper();
                    JSONObject jobj;
                    for (int i = 0; i <length; i++) {
                        jobj = api.sendImage(urls[0], GlobalVar.image_paths.get(i), true);
                        if(jobj.has("id"))
                            continue;
                    }
                }
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
            dialog.dismiss();
            if(result.equals(""))
            Toast.makeText(AddPostActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();
            //clear images
            //GlobalVar._bitmaps.clear();
            GlobalVar.image_paths.clear();
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
