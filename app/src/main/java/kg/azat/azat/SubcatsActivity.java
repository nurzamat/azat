package kg.azat.azat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

import kg.azat.azat.adapter.ListAdapter;
import kg.azat.azat.helpers.Constants;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.Category;


public class SubcatsActivity extends AppCompatActivity {

    private ListAdapter adapter;
    private String mode = "";
    private ArrayList<Category> _subcats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcats);

        Intent intent = getIntent();
        if(intent != null)
            mode = intent.getStringExtra("mode");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(GlobalVar.Category.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_exit));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        _subcats = GlobalVar.Category.getSubcats();
        adapter =  new ListAdapter(this, _subcats);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(_subcats != null)
                GlobalVar.Category = _subcats.get(position);
                if(mode!=null && mode.equals(Constants.POSTS_MODE))
                {
                    Intent in = new Intent(SubcatsActivity.this, PostsActivity.class);
                    startActivity(in);
                }
                else
                {
                    CategoriesActivity.fa.finish();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subcats, menu);
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
