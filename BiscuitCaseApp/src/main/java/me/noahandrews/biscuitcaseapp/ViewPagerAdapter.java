package me.noahandrews.biscuitcaseapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;

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
        ItemsDataSource dataSource;
        dataSource = ItemsDataSource.GUEST_INSTANCE;
        if(!dataSource.isOpened()) {
            dataSource.open();
        }
        return CategoryFragment.newInstance(categories.get(position));
    }

    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    public int getCount() {
        return numOfTabs;
    }
}
