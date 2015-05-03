package me.noahandrews.biscuitcasemanagement.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.software.shell.fab.ActionButton;
import me.noahandrews.biscuitcaselibrary.*;

import java.util.ArrayList;


public class ItemListActivity extends AppCompatActivity implements NewItemDialogFragment.NewItemDialogListener {
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private Category mCategory;
    private static ArrayList<Item> mItems;
    private ItemsDataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Intent intent = getIntent();
        mCategory = (Category)intent.getSerializableExtra(CategoryListActivity.EXTRA_CATEGORY);

        mDataSource = ItemsDataSource.HOST_INSTANCE;
        if(!mDataSource.isOpened()){
            mDataSource.open();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerList = (ListView)findViewById(R.id.nav_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.NavSections)));

        mTitle = mDrawerTitle = getTitle();
        mTitle = mCategory.getName();
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mItems = mDataSource.getItems(mCategory);

        RecyclerView list = (RecyclerView)findViewById(R.id.itemList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new ItemListAdapter(mItems));

        Drawable plusIcon = getResources().getDrawable(R.drawable.fab_plus_icon); //must continue to use deprecated method for now. The replacement requires API 21+
        Drawable plusIconTinted = DrawableCompat.wrap(plusIcon);
        DrawableCompat.setTint(plusIconTinted, getResources().getColor(R.color.fab_material_black));
        ActionButton addCategoryButton = (ActionButton)findViewById(R.id.actionButton);
        addCategoryButton.setImageDrawable(plusIconTinted);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NewItemDialogFragment dialog = new NewItemDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                dialog.show(fm, null);
            }
        });
    }
    
    static void addItem(Item item){
        mItems.add(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(Item newItem) {
        newItem.setCategory(mCategory);
        Log.d(Constants.DEBUG_TAG, "new item added to DB");
        mDataSource.addItem(newItem);
    }
}
