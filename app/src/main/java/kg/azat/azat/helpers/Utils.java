package kg.azat.azat.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import kg.azat.azat.R;
import kg.azat.azat.model.Category;
import kg.azat.azat.model.Param;

public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }

    public static int getActionTypeValue(ActionType _actionType)
    {
        int x = 0;

        switch (_actionType)
        {
            case NONE: x = 0;
                break;
            case BUY: x = 1;
                break;
            case SELL: x = 2;
                break;
            case RENT_GET: x = 3;
                break;
            case RENT_GIVE: x = 4;
                break;
            case RESUME: x = 5;
                break;
            case VACANCY: x = 6;
                break;
        }

      return x;
    }

    public static ActionType getActionTypeByValue(int x)
    {
        ActionType action = null;

        switch (x)
        {
            case 0: action = ActionType.NONE;
                break;
            case 1: action = ActionType.BUY;
                break;
            case 2: action = ActionType.SELL;
                break;
            case 3: action = ActionType.RENT_GET;
                break;
            case 4: action = ActionType.RENT_GIVE;
                break;
            case 5: action = ActionType.RESUME;
                break;
            case 6: action = ActionType.VACANCY;
                break;
        }

        return action;
    }

    public static CategoryType getCategoryType(Category category)
    {
        CategoryType categoryType = null;

        if(category == null)
             return null;

        String idCategory;
        if(!category.getIdParent().equals(""))
        {
            idCategory = category.getIdParent();
        }
        else idCategory = category.getId();

        switch (idCategory)
        {
            case "0": categoryType = CategoryType.NONE;
                break;
            case "1": categoryType = CategoryType.SELL_BUY;
                break;
            case "2": categoryType = CategoryType.SERVICES;
                break;
            case "3": categoryType = CategoryType.RENT;
                break;
            case "4": categoryType = CategoryType.WORK;
                break;
            case "5": categoryType = CategoryType.REST;
                break;
            case "6": categoryType = CategoryType.EVENTS;
                break;
            case "7": categoryType = CategoryType.BUSSINES;
                break;
            case "8": categoryType = CategoryType.DATING;
                break;
        }

        return categoryType;
    }

    public static String getParams(Param param)
    {
        String params = param.getQuery().trim()+";"
                +param.getActionType()+";"
                +param.getRegion()+";"
                +param.getLocation()+";"
                +param.getPrice_from()+";"
                +param.getPrice_to()+";"
                +param.getSex()+";"
                +param.getAge_from()+";"
                +param.getAge_to();
        try
        {
            params = URLEncoder.encode(params, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }
        return params;
    }

    public static Category getCategoryByIds(String id_category, String id_subcategory)
    {
        Category category = null;
        try
        {
            for(Iterator<Category> i = GlobalVar._categories.iterator(); i.hasNext(); ) {
                Category item = i.next();

                if(!item.getIdParent().equals(""))
                {
                    if(item.getIdParent().equals(id_category) && item.getId().equals(id_subcategory))
                    {
                        category = item;
                    }
                }
                else
                {
                    if(item.getId().equals(id_category))
                    {
                        category = item;
                    }
                }
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

        return category;
    }

    public static boolean isConnected(Context context)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getTimeStamp(String dateStr)
    {
        if(GlobalVar.today == null)
        {
            Calendar calendar = Calendar.getInstance();
            GlobalVar.today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            GlobalVar.today = GlobalVar.today.length() < 2 ? "0" + GlobalVar.today : GlobalVar.today;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(GlobalVar.today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public static CharSequence getTimeAgo(String srcDate) {

        long dateInMillis = getDateInMillis(srcDate);

        CharSequence timeAgo = "";
        try
        {
            timeAgo = DateUtils.getRelativeTimeSpanString(dateInMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return timeAgo;
    }

}
