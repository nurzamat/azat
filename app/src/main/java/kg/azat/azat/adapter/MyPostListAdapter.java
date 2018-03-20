package kg.azat.azat.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;
import kg.azat.azat.AppController;
import kg.azat.azat.DeletePostActivity;
import kg.azat.azat.EditPostActivity;
import kg.azat.azat.FullScreenViewActivity;
import kg.azat.azat.R;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.Post;


public class MyPostListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //views
    private int thumbnail_id;
    private int menu_id;


    public MyPostListAdapter(Activity activity, List<Post> postItems) {
        this.activity = activity;
        this.postItems = postItems;
    }

    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public Object getItem(int location) {
        return postItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.my_post_list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        NetworkImageView avatar = (NetworkImageView) convertView.findViewById(R.id.avatar);
        ProgressBar spin = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        spin.setVisibility(View.VISIBLE);

        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView hitcount = (TextView) convertView.findViewById(R.id.hitcount);
        TextView displayed_name = (TextView) convertView.findViewById(R.id.displayed_name);
        TextView category_name = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        ImageButton menu = (ImageButton) convertView.findViewById(R.id.btnMenu);

        // getting post data for the row
        Post p = postItems.get(position);
        String image_url = p.getThumbnailUrl();
        // thumbnail image
        if(image_url.equals(""))
            thumbNail.setDefaultImageResId(R.drawable.default_img);
        thumbNail.setImageUrl(image_url, imageLoader);
        if(thumbNail.getDrawable() != null)
            spin.setVisibility(View.GONE);

        if(p.getUser() != null)
            avatar.setImageUrl(p.getUser().getAvatarUrl(), imageLoader);
        // title
        content.setText(p.getContent());
        hitcount.setText(p.getHitcount());
        // username
        if(p.getUser() != null)
            displayed_name.setText(p.getUser().getPhone());
        if(p.getCategory() != null)
            category_name.setText(p.getCategory().getName());
        // price
        price.setText(String.valueOf(p.getPrice()));

        // image view click listener
        thumbnail_id = thumbNail.getId();
        menu_id = menu.getId();

        thumbNail.setOnClickListener(new OnImageClickListener(thumbnail_id, position, p));
        menu.setOnClickListener(new OnImageClickListener(menu_id, position, p));

        return convertView;
    }

    public void deleteItem(int position) {
        postItems.remove(position);
    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;
        int _view_id;
        Post _m;

        // constructor
        public OnImageClickListener(int view_id, int position, Post m)
        {
            this._view_id = view_id;
            this._position = position;
            this._m = m;
        }

        @Override
        public void onClick(View v) {

            // on selecting grid view image
            // launch full screen activity
            if(_view_id == thumbnail_id)
            {
                if(_m.getImages() != null && _m.getImages().size() > 0)
                {
                    GlobalVar._Post = _m;
                    Intent i = new Intent(activity, FullScreenViewActivity.class);
                    activity.startActivity(i);
                }
                else Toast.makeText(activity, R.string.no_photo, Toast.LENGTH_SHORT).show();
            }
            if(_view_id == menu_id)
            {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, v);
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
        }

        public void deletePost()
        {
            //Toast.makeText(activity, "delete pressed", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

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
                    Intent i = new Intent(activity, DeletePostActivity.class);
                    i.putExtra("position", _position);
                    i.putExtra("id", _m.getId());
                    activity.startActivity(i);
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
            GlobalVar.Category = _m.getCategory();
            GlobalVar._Post = _m;
            //GlobalVar._bitmaps.clear();
            GlobalVar.image_paths.clear();
            GlobalVar.mSparseBooleanArray.clear();

            Intent in = new Intent(activity, EditPostActivity.class);
            activity.startActivity(in);
        }
    }

}
