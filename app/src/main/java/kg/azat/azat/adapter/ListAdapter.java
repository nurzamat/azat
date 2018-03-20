package kg.azat.azat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import kg.azat.azat.R;
import kg.azat.azat.model.Category;

/**
 * Created by nurzamat on 5/18/15.
 */
public class ListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Category> listItems;

    public ListAdapter(Context activity, ArrayList<Category> _listItems) {
        this.context = activity;
        this.listItems = _listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int location) {
        return listItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi=convertView;
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        ImageView img = (ImageView)vi.findViewById(R.id.left_icon);
        Category model = listItems.get(position);
        // Setting all values in listview
        title.setText(model.getName());
        img.setImageResource(R.drawable.from);

        return vi;
    }

}