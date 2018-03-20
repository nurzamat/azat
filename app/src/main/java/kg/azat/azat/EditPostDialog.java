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
import java.util.regex.Pattern;

import kg.azat.azat.helpers.CategoryType;
import kg.azat.azat.model.Post;

/**
 * Created by nurzamat on 9/5/16.
 */
public class EditPostDialog extends DialogFragment {

    private String id, title, content, price, price_currency, birth_year, location, phone, idCategory, idSubcategory;
    EditText etContent, etPrice, etTitle, etBirth_year, etPhone;
    Spinner price_spinner;
    LinearLayout price_layout;
    ArrayAdapter<CharSequence> adapter;

    static EditPostDialog newInstance(Post p)
    {
        EditPostDialog f = new EditPostDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        args.putString("id", p.getId());
        args.putString("title", p.getTitle());
        args.putString("content", p.getContent());
        args.putString("price", p.getPrice());
        args.putString("price_currency", p.getPriceCurrency());
        args.putString("birth_year", p.getBirth_year());
        args.putString("location", p.getLocation());
        args.putString("phone", p.getPhone());
        //args.putString("idCategory", idCategory);
        //args.putString("idSubcategory", idSubcategory);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getString("id");
        title = getArguments().getString("title");
        content = getArguments().getString("content");
        price = getArguments().getString("price");
        price_currency = getArguments().getString("price_currency");
        birth_year = getArguments().getString("birth_year");
        location = getArguments().getString("location");
        phone = getArguments().getString("phone");
        //idCategory = getArguments().getString("idCategory");
        //idSubcategory = getArguments().getString("idSubcategory");
    }

    public interface SaveListener {
        void onSave(Post p);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_post_dialog, container, false);
        etTitle = (EditText) v.findViewById(R.id.title);
        etContent = (EditText) v.findViewById(R.id.content);
        etPrice = (EditText) v.findViewById(R.id.price);
        etPhone = (EditText) v.findViewById(R.id.phone);
        price_spinner = (Spinner) v.findViewById(R.id.price_spinner);
        price_layout = (LinearLayout) v.findViewById(R.id.price_layout);

        //spinner job
        price_spinner = (Spinner) v.findViewById(R.id.price_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.price_currencies, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        price_spinner.setAdapter(adapter);

        price_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        // On selecting a spinner item
                        price_currency = parent.getItemAtPosition(pos).toString();

                        // Showing selected spinner item
                        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        Fill();

        Button button = (Button)v.findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                title = etTitle.getText().toString();
                content = etContent.getText().toString();
                price = etPrice.getText().toString();
                phone = etPhone.getText().toString();
                Post p = new Post();
                p.setTitle(title);
                p.setContent(content);
                p.setPrice(price);
                p.setPriceCurrency(price_currency);
                p.setPhone(phone);

                ((SaveListener)getActivity()).onSave(p);
                dismiss();
            }
        });

        return v;
    }

    private void Fill()
    {
        try
        {
            etTitle.setText(title);

            String raw_price = price;
            if (raw_price.contains("."))
            {
                String[] parts = raw_price.split(Pattern.quote("."));
                this.price = parts[0].replaceAll("\\D+","") + "." + parts[1].replaceAll("\\D+","");
            }
            else
            {
                this.price = "";
            }

            etContent.setText(content);
            etPhone.setText(phone);


            etPrice.setText(price);
            price_spinner.setSelection(adapter.getPosition(price_currency));
            if(!price.equals("") && !price.equals("0.00"))
                priceLayout(CategoryType.SELL_BUY);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
