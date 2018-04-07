package kg.azat.azat;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.User;


public class MainActivity extends AppCompatActivity {

    //Defining Variables
    private DrawerLayout drawerLayout;
    public boolean isexit = false;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        initViewPagerAndTabs();

        user = AppController.getInstance().getUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                if(user != null)
                    in = new Intent(MainActivity.this, AddPostActivity.class);
                else
                    in = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(in);
            }
        });

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                Intent in;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    case R.id.nav_profile:
                        if(user != null)
                            in = new Intent(MainActivity.this, MyProfileActivity.class);
                        else
                            in = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(in);
                        return true;
                    // For rest of the options we just show a toast on click
                    case R.id.nav_ads:
                        if(user != null)
                        {
                            in = new Intent(MainActivity.this, PostsActivity.class);
                            in.putExtra("nav_ads",true);
                        }
                        else
                            in = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(in);
                        return true;
                    case R.id.nav_favourites:
                        if(user != null)
                            in = new Intent(MainActivity.this, MyLikesActivity.class);
                        else
                            in = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(in);
                        return true;
                    case R.id.nav_messages:
                        //in = new Intent(MainActivity.this, ChatRoomsActivity.class);// before group chat
                        if(user != null)
                            in = new Intent(MainActivity.this, ChatsActivity.class);
                        else
                            in = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(in);
                        return true;
                    case R.id.nav_settings:
                        //in = new Intent(MainActivity.this, SettingsActivityPref.class);
                        if(user != null)
                            in = new Intent(MainActivity.this, MyCartActivity.class);
                        else
                            in = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(in);
                        return true;
                    case R.id.nav_about:
                        Toast.makeText(getApplicationContext(),"About Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", " resumed");
        GlobalVar.Category = null;
        if(GlobalVar._categories.size() == 0)
        {
            Intent in = new Intent(MainActivity.this, StartActivity.class);
            startActivity(in);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            // Retrieve the SearchView and plug it into SearchManager
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(MainActivity.this, SearchResultsActivity.class)));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        if (id == R.id.action_add_post) {
            Intent in;
            if(user != null)
                in = new Intent(MainActivity.this, AddPostActivity.class);
            else
                in = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(in);
        }
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        //pagerAdapter.addFragment(RecyclerViewFragment.createInstance(20), getString(R.string.tab_1));
        //pagerAdapter.addFragment(RecyclerViewFragment.createInstance(4), getString(R.string.tab_2));
        Fragment rek = new HomeReklamasFragment();
        Fragment cats = new HomeCategoriesFragment();
        pagerAdapter.addFragment(rek, getString(R.string.tab_1));
        pagerAdapter.addFragment(cats, getString(R.string.tab_2));
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (isexit)
            super.onBackPressed();
        else {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Хотите выйти?");
            dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    isexit = true;
                    onBackPressed();
                }
            });
            dialog.setNegativeButton(MainActivity.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }
}
