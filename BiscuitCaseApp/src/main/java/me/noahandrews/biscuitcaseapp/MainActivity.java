package me.noahandrews.biscuitcaseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.facebook.stetho.Stetho;
import com.software.shell.fab.ActionButton;
import me.noahandrews.biscuitcaselibrary.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ShoppingFragment.OnFragmentInteractionListener,
        CheckoutFragment.OnFragmentInteractionListener, OrderResetHelper.OrderResetListener, ItemListAdapter.OnItemInteractionListener {


    public static final String WARNING_TAG = "BiscuitCaseWARNING";
    public static final String DEBUG_TAG = "BiscuitCaseDEBUG";

    final LayoutStateHolder layoutStateHolder = new LayoutStateHolder();
    Order currentOrder;
    MenuItemsDataSource dataSource;
    ShoppingFragment menuFragment;
    ShoppingFragment storeFragment;
    CheckoutFragment checkoutFragment;
    Section currentSection;

    private Spinner sectionChooser;
    private TextView totalView;

    //TODO rename variables to conform to Android's naming conventions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up Stetho so that we can access the database using the Chrome developer tools
        Stetho.initialize( //TODO remove Stetho
                Stetho.newInitializerBuilder(this)
                        //.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionChooser = (Spinner)findViewById(R.id.section_chooser);
        sectionChooser.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sections, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionChooser.setAdapter(spinnerAdapter);

        dataSource = MenuItemsDataSource.INSTANCE;
        dataSource.open();

        ItemListAdapter.addListener(this);

        currentOrder = new Order();

        currentSection = Section.MENU;

        OrderResetHelper.addListener(this); //This allows us to respond to order reset events


        if(findViewById(R.id.checkout_fragment_container) != null) {
            layoutStateHolder.setLayoutType(LayoutType.DUAL_PANE);
        } else {
            layoutStateHolder.setLayoutType(LayoutType.SINGLE_PANE);
        }
        savedInstanceState = null; //TODO FOR TESTING ONLY!! REMOVE LATER!!
        if(savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            menuFragment = ShoppingFragment.newInstance(Section.MENU);
            storeFragment = ShoppingFragment.newInstance(Section.STORE);
            transaction.add(R.id.shopping_fragment_container, menuFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            if(currentSection == Section.MENU){
                //menuFragment = (ShoppingFragment) getSupportFragmentManager().findFragmentById(R.id.shopping_fragment_container); //Store the displayed fragment in menuFragment
                //storeFragment = Sho
            }

            //TODO detect the layoutState and react accordingly.
        }
        totalView = (TextView) findViewById(R.id.totalView);


        ActionButton checkoutButton = (ActionButton) findViewById(R.id.actionButton);
        checkoutButton.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  String name;
                                                  switch(layoutStateHolder.getLayoutState()) {
                                                      case SINGLE_PANE_SHOPPING:
                                                          layoutStateHolder.setLayoutState(LayoutState.SINGLE_PANE_CHECKOUT);
                                                          break;
                                                      case DUAL_PANE_COLLAPSED:
                                                          layoutStateHolder.setLayoutState(LayoutState.DUAL_PANE_EXPANDED);
                                                          break;
                                                      case SINGLE_PANE_CHECKOUT:
                                                          name = CheckoutFragment.nameField.getText().toString();
                                                          if(name.length() > 0) {
                                                              currentOrder.setCustomerName(name);
                                                              currentOrder.submitOrder();
                                                              Toast.makeText(MyApplication.getAppContext(), "Order submitted.", Toast.LENGTH_LONG).show();
                                                              layoutStateHolder.setLayoutState(LayoutState.SINGLE_PANE_SHOPPING);
                                                          } else {
                                                              Toast.makeText(MyApplication.getAppContext(), "Enter your name.", Toast.LENGTH_SHORT).show();
                                                          }
                                                          break;
                                                      case DUAL_PANE_EXPANDED:
                                                          name = CheckoutFragment.nameField.getText().toString();
                                                          if(name.length() > 0) {
                                                              currentOrder.setCustomerName(name);
                                                              currentOrder.submitOrder();
                                                              layoutStateHolder.setLayoutState(LayoutState.DUAL_PANE_COLLAPSED);
                                                              Toast.makeText(MyApplication.getAppContext(), "Order submitted.", Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              Toast.makeText(MyApplication.getAppContext(), "Enter your name.", Toast.LENGTH_SHORT).show();
                                                          }
                                                          break;
                                                  }
                                                  //currentOrder.setCustomerName("John Doe");
                                                  //currentOrder.submitOrder();
                                              }
                                          }

        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //listener for the spinner
        String item = (String) parent.getItemAtPosition(position);
        currentSection = Section.forString(item);
        FragmentManager fm = getSupportFragmentManager();
        if(currentSection == Section.MENU && fm.findFragmentById(R.id.shopping_fragment_container) == storeFragment){
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.shopping_fragment_container,menuFragment);
            ft.commit();
        }
        else if(currentSection == Section.STORE && fm.findFragmentById(R.id.shopping_fragment_container) == menuFragment) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.shopping_fragment_container,storeFragment);
            ft.commit();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void updateTotal() {
        double totalPrice = 0;
        for(Item item : currentOrder.getOrderedItems()) {
            if(item != null) {
                totalPrice += item.getPrice() * item.getQuantityDesired();
            }
        }
        totalView.setText("$" + String.format("%.2f", totalPrice));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_clear:
                AlertDialog.Builder confirmation = new AlertDialog.Builder(MainActivity.this);
                confirmation.setTitle("Reset");
                confirmation.setMessage("Are you sure you want to reset the order?");
                confirmation.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderResetHelper.resetOrder();
                    }
                });
                confirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                confirmation.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onOrderReset() {
        currentOrder = new Order();
        Log.d(DEBUG_TAG, "currentOrder has been reset.");
        totalView.setText("$0.00");
    }

    @Override
    public void onFragmentInteraction() {
        return;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onItemInteraction(Item item, int position) {
        if(item.getQuantityDesired() != 0) {
            currentOrder.addItemToOrder(item);
        }
        updateTotal();
    }

    public void onBackPressed() {
        switch(layoutStateHolder.getLayoutState()) {
            case DUAL_PANE_EXPANDED:
                layoutStateHolder.setLayoutState(LayoutState.DUAL_PANE_COLLAPSED);
                break;
            case SINGLE_PANE_CHECKOUT:
                layoutStateHolder.setLayoutState(LayoutState.SINGLE_PANE_SHOPPING);
                break;
            default:
                super.onBackPressed();
        }
    }

    enum LayoutType {
        SINGLE_PANE, DUAL_PANE
    }

    enum LayoutState {
        SINGLE_PANE_SHOPPING, SINGLE_PANE_CHECKOUT, DUAL_PANE_COLLAPSED, DUAL_PANE_EXPANDED
    }

    private class LayoutStateHolder { //prevents improper changing of layoutState
        private LayoutState layoutState;
        private LayoutType layoutType;

        public LayoutType getLayoutType() {
            return layoutType;
        }

        public void setLayoutType(LayoutType layoutType) {
            this.layoutType = layoutType;
            if(layoutType == LayoutType.SINGLE_PANE) {
                layoutState = LayoutState.SINGLE_PANE_SHOPPING;
            } else {
                layoutState = LayoutState.DUAL_PANE_COLLAPSED;
            }
        }

        public LayoutState getLayoutState() {
            return layoutState;
        }

        public void setLayoutState(final LayoutState layoutState) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch(layoutState) {
                case SINGLE_PANE_SHOPPING:
                    if(currentSection == Section.MENU){
                        ft.replace(R.id.shopping_fragment_container, menuFragment);
                        Log.d(DEBUG_TAG,"currentSection is MENU");
                    }
                    else {
                        ft.replace(R.id.shopping_fragment_container, storeFragment);
                        Log.d(DEBUG_TAG, "currentSection is STORE");
                    }
                    checkoutFragment = null;
                    sectionChooser.setVisibility(View.VISIBLE);
                    this.layoutState = LayoutState.SINGLE_PANE_SHOPPING;
                    break;
                case DUAL_PANE_COLLAPSED:
                    ft.remove(checkoutFragment);
                    checkoutFragment = null;
                    collapseSecondPane();
                    this.layoutState = LayoutState.DUAL_PANE_COLLAPSED;
                    break;
                case SINGLE_PANE_CHECKOUT:
                    checkoutFragment = CheckoutFragment.newInstance(currentOrder.getOrderedItems());
                    ft.replace(R.id.shopping_fragment_container, checkoutFragment);
                    sectionChooser.setVisibility(View.INVISIBLE);
                    this.layoutState = LayoutState.SINGLE_PANE_CHECKOUT;
                    break;
                case DUAL_PANE_EXPANDED:
                    checkoutFragment = CheckoutFragment.newInstance(currentOrder.getOrderedItems());
                    expandSecondPane();
                    ft.add(R.id.checkout_fragment_container, CheckoutFragment.newInstance(currentOrder.getOrderedItems()));
                    this.layoutState = LayoutState.DUAL_PANE_EXPANDED;
                    break;
            }
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            ft.addToBackStack(null);
            ft.commit();
        }

        private void expandSecondPane() {
            if(findViewById(R.id.checkout_fragment_container) != null) {
                FrameLayout menuFragmentContainer = (FrameLayout) findViewById(R.id.shopping_fragment_container);
                LinearLayout.LayoutParams menuContainerParams = (LinearLayout.LayoutParams) menuFragmentContainer.getLayoutParams();
                menuContainerParams.weight = 0.5f;
                menuFragmentContainer.setLayoutParams(menuContainerParams);

                FrameLayout checkoutFragmentContainer = (FrameLayout) findViewById(R.id.checkout_fragment_container);
                LinearLayout.LayoutParams checkoutContainerParams = (LinearLayout.LayoutParams) checkoutFragmentContainer.getLayoutParams();
                checkoutContainerParams.weight = 0.5f;
                checkoutContainerParams.setMargins(0, 0, 0, 0);
                checkoutFragmentContainer.setLayoutParams(checkoutContainerParams);
            } else {
                Log.d(WARNING_TAG, "The second pane cannot be expanded on a phone.");
            }
        }

        private void collapseSecondPane() {
            if(findViewById(R.id.checkout_fragment_container) != null) {
                FrameLayout menuFragmentContainer = (FrameLayout) findViewById(R.id.shopping_fragment_container);
                LinearLayout.LayoutParams menuContainerParams = (LinearLayout.LayoutParams) menuFragmentContainer.getLayoutParams();
                menuContainerParams.weight = 1f;
                menuFragmentContainer.setLayoutParams(menuContainerParams);

                FrameLayout checkoutFragmentContainer = (FrameLayout) findViewById(R.id.checkout_fragment_container);
                LinearLayout.LayoutParams checkoutContainerParams = (LinearLayout.LayoutParams) checkoutFragmentContainer.getLayoutParams();
                checkoutContainerParams.weight = 0f;
                checkoutFragmentContainer.setLayoutParams(checkoutContainerParams);
            } else {
                Log.d(WARNING_TAG, "The second pane cannot be expanded on a phone.");
            }
        }
    }
}
