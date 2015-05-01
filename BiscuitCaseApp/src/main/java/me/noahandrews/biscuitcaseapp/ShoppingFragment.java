package me.noahandrews.biscuitcaseapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;
import me.noahandrews.biscuitcaselibrary.Section;

import java.util.ArrayList;

public class ShoppingFragment extends Fragment {
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private ItemsDataSource dataSource;
    private ArrayList<Category> categories;
    Section section;

    //The reason that this gets its own fragment is so that on a tablet in landscape mode, we can show the order next to the menu.

    //TODO According to the Android team, both fragments and activities serve the role of controllers in MVC.

    //TODO Nested fragments: http://developer.android.com/about/versions/android-4.2.html#NestedFragments

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    public ShoppingFragment() {
        // Required empty public constructor
        Log.d(MainActivity.DEBUG_TAG, "MenuFragment created");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuFragment.
     */
    public static ShoppingFragment newInstance(Section section) {
        ShoppingFragment fragment = new ShoppingFragment();
        Bundle args = new Bundle();
        args.putString("section", section.sectionName);
        fragment.setArguments(args);
        Log.d(MainActivity.DEBUG_TAG, "The section string is " + fragment.getArguments().getString("section"));
        fragment.section = section;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            Bundle args = getArguments();
            if(section == null) {
                section = Section.forString(getArguments().getString("section"));
                Log.d(MainActivity.DEBUG_TAG, "Fragment section set from argument");
            }
            else{
                Log.d(MainActivity.DEBUG_TAG,"section not null");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        dataSource = ItemsDataSource.INSTANCE;
        categories = dataSource.getCategories(section);

        ArrayList<String> tabNames = new ArrayList<>();
        for(Category category : categories) {
            tabNames.add(dataSource.toDisplayName(category.getName())); //Each category gets its own tab
        }

        adapter = new ViewPagerAdapter(getChildFragmentManager(), tabNames.toArray(new String[tabNames.size()]), tabNames.size(), categories);

        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) view.findViewById(R.id.category_tabs);
        tabs.setDistributeEvenly(false);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });

        tabs.setViewPager(pager);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if(mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
