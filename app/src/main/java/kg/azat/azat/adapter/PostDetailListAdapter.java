package kg.azat.azat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;
import kg.azat.azat.AppController;
import kg.azat.azat.PostDetailActivity;
import kg.azat.azat.R;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

/**
 * Created by nurzamat on 10/11/15.
 */
public class PostDetailListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    User client = AppController.getInstance().getUser();

    //views
    private int thumbnail_id;
    private int call_id;
    private int chat_id;

    public PostDetailListAdapter(Activity activity, List<Post> postItems) {
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.post_detail_list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView avatar = (NetworkImageView) convertView
                .findViewById(R.id.avatar);

        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView hitcount = (TextView) convertView.findViewById(R.id.hitcount);
        TextView displayed_name = (TextView) convertView.findViewById(R.id.displayed_name);
        TextView category_name = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        ImageButton call = (ImageButton) convertView.findViewById(R.id.show_phone);
        ImageButton chat = (ImageButton) convertView.findViewById(R.id.show_chat);

        // getting post data for the row
        Post p = postItems.get(position);
        // thumbnail image
        if(p.getUser() != null)
            avatar.setImageUrl(p.getUser().getAvatarUrl(), imageLoader);
        // title
        content.setText(p.getContent()+" \n");
        hitcount.setText(p.getHitcount());
        // username
        if(p.getUser() != null)
            displayed_name.setText(p.getUser().getUserName());

        if(p.getCategory() != null)
            category_name.setText(p.getCategory().getName());

        // price
        price.setText(String.valueOf(p.getPrice()));

        // image view click listener
        thumbnail_id = avatar.getId();
        call_id = call.getId();
        chat_id = chat.getId();

        if(p.getUser() != null && p.getUser().getUserName().equals(client.getUserName()))
        {
            call.setVisibility(View.INVISIBLE);
            chat.setVisibility(View.INVISIBLE);
        }
        else
        {
            call.setVisibility(View.VISIBLE);
            chat.setVisibility(View.VISIBLE);
        }

        avatar.setOnClickListener(new OnImageClickListener(thumbnail_id, position, p));
        call.setOnClickListener(new OnImageClickListener(call_id, position, p));
        chat.setOnClickListener(new OnImageClickListener(chat_id, position, p));

        return convertView;
    }


    class OnImageClickListener implements View.OnClickListener {

        int _position;
        int _view_id = 0;
        Post _p;

        // constructors
        public OnImageClickListener(int view_id, int _position, Post m)
        {
            this._view_id = view_id;
            this._position = _position;
            this._p = m;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            if(_view_id == thumbnail_id)
            {
                if(_p.getImages() != null && _p.getImages().size() > 0)
                {
                    GlobalVar._Post = _p;
                    //Intent i = new Intent(activity, FullScreenViewActivity.class);
                    Intent i = new Intent(activity, PostDetailActivity.class);
                    activity.startActivity(i);
                }
                else Toast.makeText(activity, R.string.no_photo, Toast.LENGTH_SHORT).show();
            }
            if(_view_id == call_id)
            {
                try
                {
                    if (client != null)
                    {
                        String phone = _p.getUser().getPhone();
                        boolean isPhone = PhoneNumberUtils.isGlobalPhoneNumber("+" + phone);
                        if(isPhone)
                        {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + "+" + phone));
                            activity.startActivity(intent);
                        }
                        else Toast.makeText(activity, "call pressed /"+phone+"/", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        /*
                        Intent in;
                        if(GlobalVar.isCodeSent)
                            in = new Intent(activity, RegisterActivity.class);
                        else in = new Intent(activity, CodeActivity.class);
                        activity.startActivity(in);
                        */
                        Toast.makeText(activity, "You are not registered", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            if(_view_id == chat_id)
            {
                //todo
            }
        }
    }
}

