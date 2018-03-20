package kg.azat.azat;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

import kg.azat.azat.helpers.CategoryType;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.MyPreferenceManager;
import kg.azat.azat.helpers.Utils;
import kg.azat.azat.model.Param;

/**
 * Created by nurzamat on 8/27/16.
 */
public class DialogFilter extends DialogFragment {

    CategoryType categoryType = null;
    int actionType = 0;
    int actionPos = 0;
    int sex = 2; //0 - female, 1 - male
    String region, location, price_from, price_to;
    Spinner region_spinner, city_spinner;
    EditText etPriceFrom, etPriceTo;
    LinearLayout city_layout, price_layout;
    /**
     * Create a new instance of DialogFilter, providing "num"
     * as an argument.
     */
    static DialogFilter newInstance(Param param)
    {
        DialogFilter f = new DialogFilter();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(param != null)
        {
            args.putString("region", param.getRegion());
            args.putString("location", param.getLocation());
            args.putString("price_from", param.getPrice_from());
            args.putString("price_to", param.getPrice_to());
            args.putInt("actionPos", param.getActionPos());
        }
        else
        {
            args.putString("region", "");
            args.putString("location", "");
            args.putString("price_from", "");
            args.putString("price_to", "");
            args.putInt("actionPos", 0);
        }
        f.setArguments(args);

        return f;
    }
    public interface SearchListener {
        void onSearch(Param p);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        region = getArguments().getString("region");
        location = getArguments().getString("location");
        price_from = getArguments().getString("price_from");
        price_to = getArguments().getString("price_to");
        actionPos = getArguments().getInt("actionPos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_filter, container, false);
        region_spinner = (Spinner) v.findViewById(R.id.region_spinner);
        city_spinner = (Spinner) v.findViewById(R.id.city_spinner);
        //layouts
        city_layout = (LinearLayout)v.findViewById(R.id.city_layout);
        price_layout = (LinearLayout)v.findViewById(R.id.price_layout);
        //
        etPriceFrom = (EditText) v.findViewById(R.id.price_from);
        etPriceTo = (EditText) v.findViewById(R.id.price_to);

        if(!price_from.isEmpty() && !price_from.equals("0"))
           etPriceFrom.setText(price_from);
        if(!price_to.isEmpty() && !price_to.equals("0"))
            etPriceTo.setText(price_to);
        initLocationSpinners();

        categoryType = Utils.getCategoryType(GlobalVar.Category);
        if(categoryType != null)
        {
            //actionLayout(categoryType);
            //datingLayout(categoryType);
            priceLayout(categoryType);
        }

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.btnSearch);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                price_from = etPriceFrom.getText().toString();
                price_to = etPriceTo.getText().toString();

                Param p = new Param();
                if(actionType != 0)
                    p.setActionType(actionType);
                if(!region.isEmpty())
                    p.setRegion(region);
                if(!location.isEmpty())
                    p.setLocation(location);
                if(sex != 2)
                    p.setSex(sex);
                if(!price_from.isEmpty())
                    p.setPrice_from(price_from);
                if(!price_to.isEmpty())
                    p.setPrice_to(price_to);

                p.setActionPos(actionPos);

                if(categoryType != null && categoryType.equals(CategoryType.DATING))
                {
                    MyPreferenceManager prefManager =  AppController.getInstance().getPrefManager();
                    if(sex != 2)
                        prefManager.saveDatingSex(sex);
                }

                ((SearchListener)getActivity()).onSearch(p);
                dismiss();
            }
        });

        return v;
    }

    //init work
    private void initLocationSpinners()
    {
        final ArrayAdapter<CharSequence> region_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.regions_for_filter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        region_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        region_spinner.setAdapter(region_adapter);
        if(!region.isEmpty())
            region_spinner.setSelection(region_adapter.getPosition(region));
        region_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        if(pos != 0)
                            region = parent.getItemAtPosition(pos).toString();
                        else
                        {
                            region = "";
                            location = "";
                        }
                        initLocationCity(pos);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );
    }

    private void initLocationCity(int pos)
    {
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = city_layout.getLayoutParams();

        if(pos == 0)
        {
            city_spinner.setVisibility(View.INVISIBLE);
            params.height = 0;
        }
        else
        {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            city_spinner.setVisibility(View.VISIBLE);

            List<String> list = new ArrayList<String>();
            list.add("Все");
            String[] myResArray;

            if(pos == 1)
            {
                myResArray = getResources().getStringArray(R.array.chuy);

                for (String value : myResArray)
                {
                       list.add(value);
                }
            }
            if(pos == 2)
            {
                myResArray = getResources().getStringArray(R.array.issyk);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }
            if(pos == 3)
            {
                myResArray = getResources().getStringArray(R.array.naryn);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }
            if(pos == 4)
            {
                myResArray = getResources().getStringArray(R.array.talas);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }
            if(pos == 5)
            {
                myResArray = getResources().getStringArray(R.array.jalalabad);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }
            if(pos == 6)
            {
                myResArray = getResources().getStringArray(R.array.osh);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }
            if(pos == 7)
            {
                myResArray = getResources().getStringArray(R.array.batken);

                for (String value : myResArray)
                {
                    list.add(value);
                }
            }

            ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

            // Specify the layout to use when the list of choices appears
            city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            city_spinner.setAdapter(city_adapter);
            if(!location.isEmpty())
                city_spinner.setSelection(city_adapter.getPosition(location));
            city_spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {

                            if(pos != 0)
                                location = parent.getItemAtPosition(pos).toString();
                            else location = "";
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
        city_layout.setLayoutParams(params);
    }

    private void priceLayout(final CategoryType catType)
    {
        ViewGroup.LayoutParams params = price_layout.getLayoutParams();

        if(catType.equals(CategoryType.SELL_BUY) || catType.equals(CategoryType.RENT))
        {
            price_layout.setVisibility(View.VISIBLE);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        else
        {
            price_layout.setVisibility(View.INVISIBLE);
            params.height = 0;
        }
        price_layout.setLayoutParams(params);
    }
}
