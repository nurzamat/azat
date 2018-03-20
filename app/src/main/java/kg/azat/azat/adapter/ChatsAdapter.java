package kg.azat.azat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kg.azat.azat.AppController;
import kg.azat.azat.R;
import kg.azat.azat.model.Chat;

/**
 * Created by nurzamat on 8/14/16.
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Chat> chatArrayList;
    private static String today;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, timestamp, count;
        public NetworkImageView thumbNail;
        public ProgressBar spin;

        public ViewHolder(View view) {
            super(view);
            spin = (ProgressBar) view.findViewById(R.id.progBar);
            thumbNail = (NetworkImageView) view.findViewById(R.id.thumbnail);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            count = (TextView) view.findViewById(R.id.count);
        }
    }


    public ChatsAdapter(Context mContext, ArrayList<Chat> chatArrayList) {
        this.mContext = mContext;
        this.chatArrayList = chatArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chats_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chatArrayList.get(position);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        holder.spin.setVisibility(View.VISIBLE);
        // thumbnail image
        if(chat.getPost_url().equals(""))
            holder.thumbNail.setDefaultImageResId(R.drawable.default_img);
        holder.thumbNail.setImageUrl(chat.getPost_url(), imageLoader);
        if(holder.thumbNail.getDrawable() != null)
            holder.spin.setVisibility(View.GONE);

        holder.name.setText(chat.getName());
        holder.message.setText(chat.getLastMessage());
        if (chat.getUnreadCount() > 0) {
            holder.count.setText(String.valueOf(chat.getUnreadCount()));
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.GONE);
        }

        holder.timestamp.setText(getTimeStamp(chat.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
