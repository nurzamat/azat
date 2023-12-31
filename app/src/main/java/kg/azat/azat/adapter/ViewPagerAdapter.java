package kg.azat.azat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kg.azat.azat.HomeCategoriesFragment;
import kg.azat.azat.HomeReklamasFragment;
import kg.azat.azat.helpers.Constants;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{

    CharSequence[] Titles; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    String mode = "";

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence[] mTitles, String _mode)
    {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mTitles.length;
        this.mode = _mode;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position)
    {
        if(mode.equals(Constants.HOME_MODE))
        {
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                HomeReklamasFragment tab1 = new HomeReklamasFragment();
                return tab1;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                HomeCategoriesFragment tab2 = new HomeCategoriesFragment();
                return tab2;
            }
        }

        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
