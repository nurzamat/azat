package kg.azat.azat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.regex.Pattern;

import kg.azat.azat.adapter.PostViewPagerAdapter;
import kg.azat.azat.helpers.ApiHelper;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.lib.CirclePageIndicator;
import kg.azat.azat.model.Category;
import kg.azat.azat.model.Post;

public class EditPostActivity extends AppCompatActivity {

    private static final String TAG =  "[edit post response]";
    String url = "";
    String content = "";
    String price = "";
    String price_currency;
    EditText etContent;
    EditText etPrice;
    Spinner price_spinner;
    ArrayAdapter<CharSequence> adapter;
    Button categoryBtn;
    Button saveBtn;
    Category category = GlobalVar.Category;
    Post post = GlobalVar._Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //toolbar.setSubtitle(GlobalVar.SubCategory.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_exit));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etContent = (EditText) findViewById(R.id.content);
        etPrice = (EditText) findViewById(R.id.price);
        //spinner job
        price_spinner = (Spinner) findViewById(R.id.price_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(EditPostActivity.this,
                R.array.price_currencies, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        price_spinner.setAdapter(adapter);
        saveBtn = (Button) findViewById(R.id.btnPost);
        categoryBtn = (Button) findViewById(R.id.btnCategory);

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton();
            }
        });

        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EditPostActivity.this, CategoriesActivity.class);
                startActivity(in);
            }
        });

        Fill();
        //ViewPagerWork();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GlobalVar.Category != null && GlobalVar.Category.getSubcats() == null)
        {
            categoryBtn.setText(GlobalVar.Category.getName());
            this.category = GlobalVar.Category;
        }
        else this.category = null;
        ViewPagerWork();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
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

    private void Fill()
    {
        try
        {
            this.url = ApiHelper.POST_URL + "/" + post.getId();
            this.content = post.getContent();
            this.price_currency = post.getPriceCurrency();

            String raw_price = post.getPrice();
            if (raw_price.contains("."))
            {
                String[] parts = raw_price.split(Pattern.quote("."));
                this.price = parts[0].replaceAll("\\D+","") + "." + parts[1].replaceAll("\\D+","");
            }
            else
            {
                this.price = "";
            }

            etContent.setText(content);
            etPrice.setText(price);
            price_spinner.setSelection(adapter.getPosition(price_currency));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void ViewPagerWork() {
        int color = getResources().getColor(R.color.blue_dark);
        PagerAdapter mAdapter = new PostViewPagerAdapter(EditPostActivity.this);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setFillColor(color);
        mIndicator.setStrokeColor(color);
        mIndicator.setRadius(5);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);
    }

    private void saveButton() {
        if(!validate())
        {
            Toast.makeText(EditPostActivity.this, "Заполните все поля", Toast.LENGTH_LONG).show();
        }
        else
        {
            //use volley or async
            //VolleyPut();
            //async task
            putHttpAsyncTask task = new putHttpAsyncTask();
            task.execute();
        }
    }

    private boolean validate(){

        content = etContent.getText().toString().trim();
        price = etPrice.getText().toString().trim();

        return (!content.equals("") && !price.equals("") && category != null && !price_currency.equals(""));
    }

    private class putHttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog pdialog;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(EditPostActivity.this, "","Загрузка...", true);
            pdialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try
            {
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                if(category.getIdParent().equals(""))
                {
                    jsonObject.put("idCategory", category.getId());
                    jsonObject.put("idSubcategory", 0);
                }
                else
                {
                    jsonObject.put("idCategory", category.getIdParent());
                    jsonObject.put("idSubcategory", category.getId());
                }
                jsonObject.put("title", "test");
                jsonObject.put("content", content);
                jsonObject.put("price", price);
                jsonObject.put("price_currency", price_currency);
                jsonObject.put("api_key", ApiHelper.getApiKey());

                JSONObject obj = api.editPost(url, jsonObject);
                if(obj.getBoolean("error"))
                {
                    result = "error";
                }
                else result = "";
            }
            catch (Exception ex)
            {
                Log.d(TAG, "Exception: " + ex.getMessage());
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
                Toast.makeText(EditPostActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(EditPostActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();

                //clear images
                //GlobalVar._bitmaps.clear();
                GlobalVar.image_paths.clear();
                GlobalVar._Post = null;

                Intent in = new Intent(EditPostActivity.this, MyCartActivity.class);
                startActivity(in);
            }
        }
    }
}
