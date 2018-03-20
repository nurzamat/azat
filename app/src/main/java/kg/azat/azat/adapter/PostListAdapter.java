package kg.azat.azat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;

import kg.azat.azat.AppController;
import kg.azat.azat.PostDetailActivity;
import kg.azat.azat.R;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.model.Post;
import kg.azat.azat.model.User;

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private List<Post> postItems;
    private boolean isListView;
    private boolean nav_ads;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    User user = AppController.getInstance().getUser();


    public PostListAdapter(Context _context, List<Post> _postItems, boolean _isListView, boolean _nav_ads)
    {
        context = _context;
        postItems = _postItems;
        isListView = _isListView;
        nav_ads = _nav_ads;
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public ProgressBar spin;
        public NetworkImageView thumbnail;
        public ImageView eye;
        public TextView  hitcount, timestamp, location, title, price, price_currency;
        private ItemClickListener clickListener;

        public PostViewHolder(View parent) {
            super(parent);
            spin = (ProgressBar) parent.findViewById(R.id.progressBar1);
            thumbnail = (NetworkImageView) parent.findViewById(R.id.thumbnail);
            eye = (ImageView) parent.findViewById(R.id.eye);
            hitcount = (TextView) parent.findViewById(R.id.hitcount);
            title = (TextView) parent.findViewById(R.id.title);
            timestamp = (TextView) parent.findViewById(R.id.date);
            location = (TextView) parent.findViewById(R.id.location);
            price = (TextView) parent.findViewById(R.id.price);
            price_currency = (TextView) parent.findViewById(R.id.price_currency);
            parent.setTag(parent);
            parent.setOnClickListener(this);
            parent.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        if(isListView)
           view = LayoutInflater.from(context).inflate(R.layout.post_list_row, parent, false);
        else view = LayoutInflater.from(context).inflate(R.layout.post_grid_row, parent, false);
        PostViewHolder vh = new PostViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        PostViewHolder holder = (PostViewHolder) viewHolder;
        Post post = postItems.get(position);
        holder.title.setText(post.getTitle());

        //set price & hitcount
        try
        {
            if(isListView)
            {
                if(post.getUser().getId().equals(user.getId()))
                {
                    holder.eye.setVisibility(View.VISIBLE);
                    holder.hitcount.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.eye.setVisibility(View.INVISIBLE);
                    holder.hitcount.setVisibility(View.INVISIBLE);
                }
            }

            holder.hitcount.setText(post.getHitcount());

            String price = post.getPrice().trim();
            if(!price.equals("0.00"))
            {
                double number = Double.parseDouble(price);
                int res = (int)number; //целая часть
                double res2 = number - res; //дробная часть

                if(res2 > 0)
                    holder.price.setText(price);
                else holder.price.setText(""+res);

                holder.price_currency.setText(post.getPriceCurrency());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //set location
        if(!post.getLocation().equals("null"))
        holder.location.setText(post.getLocation());
        //holder.timestamp.setText(Utils.getTimeStamp(post.getDate_created(), GlobalVar.today));
        holder.timestamp.setText(Utils.getTimeAgo(post.getDate_created()));

        holder.spin.setVisibility(View.VISIBLE);
        String image_url = post.getThumbnailUrl();
        // thumbnail image
        if(image_url.equals(""))
            holder.thumbnail.setDefaultImageResId(R.drawable.default_img);
        holder.thumbnail.setImageUrl(image_url, imageLoader);
        if(holder.thumbnail.getDrawable() != null)
            holder.spin.setVisibility(View.GONE);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    //Toast.makeText(context, "#" + position + " - " + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    GlobalVar._Post = postItems.get(position);
                    Intent i = new Intent(context, PostDetailActivity.class);
                    i.putExtra("position",position);
                    if(nav_ads)
                        i.putExtra("nav_ads",true);
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postItems == null ? 0 : postItems.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}
