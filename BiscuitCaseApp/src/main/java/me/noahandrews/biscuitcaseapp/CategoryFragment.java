package me.noahandrews.biscuitcaseapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.Item;
import me.noahandrews.biscuitcaselibrary.ItemListAdapter;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private ItemListAdapter itemListAdapter;

    private ArrayList items;

    public static CategoryFragment newInstance(Category category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        ItemsDataSource dataSource;
        dataSource = ItemsDataSource.GUEST_INSTANCE;
        if(!dataSource.isOpened()) {
            dataSource.open();
        }

        args.putSerializable("category", category);

        //TODO This is a fragment, so it doesn't need a list of the Item objects.
        //TODO This fragment perhaps should be rewritten so that it takes a List of Items as a parameter, moving the database code maybe to the MenuFragment.
        args.putSerializable("itemsArray", dataSource.getItems(category));
        dataSource.close();

        fragment.setArguments(args);

        return fragment;
    }

    public ArrayList<Item> getItems() {
        return (ArrayList<Item>) getArguments().getSerializable("itemsArray");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, parent, false);

        RecyclerView menu = (RecyclerView) v.findViewById(R.id.menu);
        menu.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        items = getItems();
        itemListAdapter = new ItemListAdapter(getActivity(), getItems(), false);

        menu.setAdapter(itemListAdapter);
        return v;
    }


}