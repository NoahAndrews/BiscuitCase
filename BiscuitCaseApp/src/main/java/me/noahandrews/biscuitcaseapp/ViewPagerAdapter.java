package me.noahandrews.biscuitcaseapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    CharSequence Titles[];
    int numOfTabs;
    FragmentManager fm;
    ArrayList<Category> categories;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabs,ArrayList<Category> categories) {
        super(fm);
        this.Titles = mTitles;
        this.numOfTabs = mNumOfTabs;
        this.fm = fm;
        this.categories = categories;
    }

    public Fragment getItem(int position) {
        MenuItemsDataSource dataSource;
        dataSource = MenuItemsDataSource.INSTANCE;
        if(!dataSource.isOpened()) {
            dataSource.open();
        }
        return CategoryFragment.newInstance(categories.get(position));
    }

    @Deprecated
    public void clearTotals() {
        FragmentTransaction transaction = fm.beginTransaction();
        ArrayList<Fragment> fragments = (ArrayList<Fragment>) fm.getFragments();
        for(int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
//            Bundle args = fragment.getArguments();
//            ArrayList items = (ArrayList)args.getSerializable("itemsArray");//Retrieve list of items in the fragment
//            for(int j = 0; j < items.size(); j++){
//                ItemMenu item = (ItemMenu)items.get(j);
//                item.setQuantityDesired(0);
//            }
//            args.putSerializable("itemsArray",(Serializable) items);
//            fragment.setArguments(args);
//            if(fragment.getView() != null)
//                fragment.getView().invalidate();

            transaction.replace(R.id.pager, fragment);//Someone online said this doesn't do the job.
        }
    }

    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    public int getCount() {
        return numOfTabs;
    }
}
