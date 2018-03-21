package kg.azat.azat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import kg.azat.azat.adapter.CategoriesRecyclerAdapter;
import kg.azat.azat.helpers.GlobalVar;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCategoriesFragment extends Fragment
{

    private View rootView;

    public HomeCategoriesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        }

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        CategoriesRecyclerAdapter adapter =  new CategoriesRecyclerAdapter(getActivity(), GlobalVar._categories);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
