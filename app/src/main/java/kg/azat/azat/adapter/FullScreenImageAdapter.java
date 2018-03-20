package kg.azat.azat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.ArrayList;
import kg.azat.azat.AppController;
import kg.azat.azat.DeleteImageActivity;
import kg.azat.azat.R;
import kg.azat.azat.model.Image;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

/**
 * Created by nurzamat on 1/18/15.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<Image> _images;
    private LayoutInflater inflater;
    private Post post;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    User client = AppController.getInstance().getUser();

    // constructor
    public FullScreenImageAdapter(Activity activity, Post _post) {
        this._activity = activity;
        this.post = _post;
        this._images = _post.getImages();
    }

    @Override
    public int getCount() {
        return this._images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        NetworkImageView imgDisplay;
        ImageButton btnClose;
        ImageButton btnDelete;
        TextView count;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        imgDisplay = (NetworkImageView) viewLayout.findViewById(R.id.imgDisplay);
        ProgressBar spin = (ProgressBar) viewLayout.findViewById(R.id.progressBar1);
        spin.setVisibility(View.VISIBLE);

        count = (TextView) viewLayout.findViewById(R.id.text_indicator);
        btnClose = (ImageButton) viewLayout.findViewById(R.id.btnClose);
        btnDelete = (ImageButton) viewLayout.findViewById(R.id.btnDelete);

        // thumbnail image
        //if(_images.get(position).getUrl().equals(""))
        //   imgDisplay.setDefaultImageResId(R.drawable.default_img);
        imgDisplay.setImageUrl(_images.get(position).getUrl(),imageLoader);
        if(imgDisplay.getDrawable() != null)
            spin.setVisibility(View.GONE);
        //indicator text
        int start = position + 1;
        count.setText(start + " из " + _images.size());
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        if(client != null && post.getUser().getId().equals(client.getId()))
        {
            // delete button click event
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setEnabled(true);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(_activity, DeleteImageActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("image_id", _images.get(position).getId());
                    _activity.startActivity(i);
                    _activity.finish();
                }
            });
        }
        else
        {
            btnDelete.setVisibility(View.INVISIBLE);
            btnDelete.setEnabled(false);
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
