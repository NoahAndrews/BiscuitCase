package me.noahandrews.biscuitcasemanagement.app;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import com.software.shell.fab.ActionButton;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.ItemsDataSource;
import me.noahandrews.biscuitcaselibrary.Section;

import java.util.ArrayList;


public class CategoryListActivity extends AppCompatActivity {

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    ArrayList<Category> categories;
    ItemsDataSource dataSource;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        dataSource = ItemsDataSource.HOST_INSTANCE;
        dataSource.open();
        categories = dataSource.getCategories();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView list = (RecyclerView)findViewById(R.id.categoryList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CategoryListAdapter(categories));

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
                        dataSource.addCategory(newCategory);
                    }
                });
                dialog.setNegativeButton("Cancel",null);
                dialog.show();
            }
        });
    }
    

    
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(true/*if the drawer is not open*/) {
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
