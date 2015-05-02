package me.noahandrews.biscuitcasemanagement.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import com.software.shell.fab.ActionButton;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;
import me.noahandrews.biscuitcaselibrary.Section;

import java.util.ArrayList;


public class CategoryActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    ArrayList<Category> categories;
    ItemsDataSource dataSource;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        dataSource = ItemsDataSource.HOST_INSTANCE;
        dataSource.open();
        categories = dataSource.getCategories();

        RecyclerView list = (RecyclerView)findViewById(R.id.categoryList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CategoryListAdapter(categories));
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ActionButton addCategoryButton = (ActionButton)findViewById(R.id.actionButton);
        addCategoryButton.setImageResource(R.drawable.fab_plus_icon);
        addCategoryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder dialog = new AlertDialog.Builder(CategoryActivity.this);
                dialog.setTitle("Add new category");
                dialog.setMessage("Enter a name.");
                final EditText nameField = new EditText(CategoryActivity.this);
                nameField.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setView(nameField);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category newCategory = new Category(nameField.getText().toString(), Section.MENU);
                        dataSource.addCategory(newCategory);
                    }
                });
                dialog.setNegativeButton("Cancel",null);
                dialog.show();
            }
        });
    }
    
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // launch a new activity
    }
    
    public void onSectionAttached(int number) {
        switch(number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }
    
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.database, menu);
            restoreActionBar();
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
