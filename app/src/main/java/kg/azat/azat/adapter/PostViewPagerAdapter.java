package kg.azat.azat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import kg.azat.azat.AppController;
import kg.azat.azat.R;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.model.Image;

/**
 * Created by nurzamat on 8/21/16.
 */
public class PostViewPagerAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<Image> images;
    //ArrayList<Bitmap> bitmaps;
    private LayoutInflater inflater;
    private boolean isAddPost = false;
    ImageLoader imageLoader = null;


    // constructor
    public PostViewPagerAdapter(Activity activity, ArrayList<Image> _images) {
        this._activity = activity;
        this.images = _images;
        this.isAddPost = false;
        this.imageLoader = AppController.getInstance().getImageLoader();
    }

    public PostViewPagerAdapter(Activity activity) {
        this._activity = activity;
        //this.bitmaps = GlobalVar._bitmaps;
        this.isAddPost = true;
    }

    @Override
    public int getCount()
    {
        if(isAddPost)
        {
            if(GlobalVar.image_paths.size() > 0)
            return GlobalVar.image_paths.size();
        }
        else
        {   if(this.images.size() > 0)
            return this.images.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewLayout;
        if(isAddPost)
        {
            viewLayout = inflater.inflate(R.layout.viewpager_item, container, false);

            ImageView imgflag = (ImageView) viewLayout.findViewById(R.id.flag);
            //imgflag.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // Capture position and set to the ImageView
            try
            {
                if(GlobalVar.image_paths.size() > 0)
                {
                    //1-st variant
                   //imgflag.setImageBitmap(bitmaps.get(position));

                    //2-nd variant
                    Glide.with(_activity)
                            .load("file://"+GlobalVar.image_paths.get(position))
                            .fitCenter()
                            .placeholder(R.drawable.ic_action_camera)
                            .error(R.drawable.ic_action_camera)
                            .into(imgflag);
                }
                else
                {
                    imgflag.setImageResource(R.drawable.default_img);
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                Log.d("exception:", ex.getMessage());
            }
        }
        else
        {
            viewLayout = inflater.inflate(R.layout.post_detail_image, container, false);

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            NetworkImageView imgDisplay = (NetworkImageView) viewLayout.findViewById(R.id.imgDisplay);
            ProgressBar spin = (ProgressBar) viewLayout.findViewById(R.id.progressBar1);
            spin.setVisibility(View.VISIBLE);

            // thumbnail image
            imgDisplay.setImageUrl(images.get(position).getUrl(),imageLoader);

            if(imgDisplay.getDrawable() != null)
                spin.setVisibility(View.GONE);
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

}
