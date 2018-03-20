package kg.azat.azat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kg.azat.azat.R;
import kg.azat.azat.helpers.GlobalVar;

/**
 * used for Gallery
 */
public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.MyViewHolder> {

    private ArrayList<String> mImagesList;
    private Context mContext;
    //private SparseBooleanArray mSparseBooleanArray;

    public GalleryImageAdapter(Context context, ArrayList<String> imageList) {
        mContext = context;
        //mSparseBooleanArray = new SparseBooleanArray();
        mImagesList = new ArrayList<String>();
        this.mImagesList = imageList;

    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<mImagesList.size();i++) {
            if(GlobalVar.mSparseBooleanArray.get(i)) {
                mTempArry.add(mImagesList.get(i));
            }
        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            GlobalVar.mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiphoto_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String imageUrl = mImagesList.get(position);

        Glide.with(mContext)
                .load("file://"+imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_action_camera)
                .error(R.drawable.ic_action_refresh)
                .into(holder.imageView);

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(GlobalVar.mSparseBooleanArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
            imageView = (ImageView) view.findViewById(R.id.imageView1);
        }
    }

}
