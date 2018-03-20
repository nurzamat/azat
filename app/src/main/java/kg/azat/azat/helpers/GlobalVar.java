package kg.azat.azat.helpers;

import android.util.SparseBooleanArray;
import kg.azat.azat.model.Category;
import java.util.ArrayList;
import kg.azat.azat.model.Post;

/**
 * Created by User on 12.12.2014.
 */
public class GlobalVar {

    public static ArrayList<Category> _categories = new ArrayList<Category>();
    //public static ArrayList<Bitmap> _bitmaps = new ArrayList<Bitmap>();
    public static ArrayList<String> image_paths = new ArrayList<String>();
    public static SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    public static Post _Post = null;
    public static Category Category = null;
    public static String today = null;
}
