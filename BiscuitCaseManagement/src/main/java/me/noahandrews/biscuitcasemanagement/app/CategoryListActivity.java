package me.noahandrews.biscuitcasemanagement.app;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.software.shell.fab.ActionButton;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;
import me.noahandrews.biscuitcaselibrary.Section;

import java.util.ArrayList;


public class CategoryListActivity extends AppCompatActivity {
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private ArrayList<Category> mCategories;
    private ItemsDataSource mDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mDataSource = ItemsDataSource.HOST_INSTANCE;
        if(!mDataSource.isOpened()){
            mDataSource.open();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerList = (ListView)findViewById(R.id.nav_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.NavSections)));

        mTitle = mDrawerTitle = getTitle();
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

        mCategories = mDataSource.getCategories();

        RecyclerView list = (RecyclerView)findViewById(R.id.categoryList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CategoryListAdapter(mCategories));

        Drawable plusIcon = getResources().getDrawable(R.drawable.fab_plus_icon); //must continue to use deprecated method for now. The replacement requires API 21+
        Drawable plusIconTinted = DrawableCompat.wrap(plusIcon);
        DrawableCompat.setTint(plusIconTinted, getResources().getColor(R.color.fab_material_black));
        ActionButton addCategoryButton = (ActionButton)findViewById(R.id.actionButton);
        addCategoryButton.setImageDrawable(plusIconTinted);
        addCategoryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder dialog = new AlertDialog.Builder(CategoryListActivity.this);
                dialog.setTitle("Add new category");
                dialog.setMessage("Enter a name.");
                final EditText nameField = new EditText(CategoryListActivity.this);
                nameField.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setView(nameField);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category newCategory = new Category(nameField.getText().toString(), Section.MENU);
                        mDataSource.addCategory(newCategory);
                    }
                });
                dialog.setNegativeButton("Cancel",null);
                dialog.show();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(true/*if the drawer is not open*/) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.database, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
}
