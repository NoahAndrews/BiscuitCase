package me.noahandrews.biscuitcaseapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public enum MenuItemsDataSource {
    INSTANCE;

    public static final String[] MENU_CATEGORIES_SEED = {"Appetizers", "Entrees", "Beverages", "Soups and Salads", "Sides", "Desserts"};
    public static final String DB_NAME = "menu.db";
    public static final int DB_VERSION = 17; // If this is incremented, we should really add code
    public static final String MENU_ITEMS_TABLE = "Menu_Items";
    //Columns for MenuItems table
    public static final String COLUMN_ITEM_ID = "ID"; //The name of the field containing the item's unique ID
    public static final String COLUMN_ITEM_NAME = "Item_Name"; //The name of the field containing the item's name
    // to onUpgrade that will maintain the items entered.
    public static final String COLUMN_ITEM_PRICE = "Item_Price";
    public static final String COLUMN_ITEM_CATEGORY_ID = "Category_ID";
    public static final String CATEGORIES_TABLE = "Categories";
    //Columns for Categories table
    public static final String COLUMN_CATEGORY_ID = "ID";
    public static final String COLUMN_CATEGORY_NAME = "Category_Name";
    public static final String COLUMN_CATEGORY_SECTION = "Category_Section";
    public static final String ORDER_SUMMARY_TABLE = "Order_Summaries";
    //Columns for order summaries table
    public static final String COLUMN_ORDERSUMMARY_ID = "ID";
    public static final String COLUMN_ORDERSUMMARY_CUSTOMER_NAME = "Customer_Name";
    public static final String COLUMN_ORDERSUMMARY_TIMESTAMP = "Order_Timestamp";
    public static final String ORDER_DETAILS_TABLE = "Order_Details";
    //Columns for order details table
    public static final String COLUMN_ORDERDETAILS_ORDERID = "Order_ID";
    public static final String COLUMN_ORDERDETAILS_ITEMID = "Item_ID";
    public static final String COLUMN_ORDERDETAILS_ITEMQUANTITY = "Item_Quantity";
    public static final String COLUMN_ORDERDETAILS_ITEMPRICE = "Item_Price";
    SQLHelper helper;
    SQLiteDatabase database;
    private boolean isOpened = false;
    private String[] readableColumnsItemsTable = {COLUMN_ITEM_ID, COLUMN_ITEM_NAME, COLUMN_ITEM_PRICE, COLUMN_ITEM_CATEGORY_ID};
    private String[] readableColumnsCategoriesTable = {COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_SECTION};
    private ArrayList<Category> categories;

    MenuItemsDataSource() {
        helper = new SQLHelper();
    }

    public static String toCategoryName(String string) {
        return string.replace(' ', '_');
    }

    public ArrayList<Category> getCategories() {
        if(categories == null)
            populateCategories();
        return categories;
    }

    public ArrayList<Category> getCategories(Section section) {
        if(categories == null) {
            populateCategories();
        }
        ArrayList categoriesInSection = new ArrayList();
        for(Category category: categories){
            if(category.getSection() == section){
                categoriesInSection.add(category);
            }
        }
        return categoriesInSection;
    }

    public String toDisplayName(String string) {
        return string.replace('_', ' ');
    }

    public void open() throws SQLiteException {
        database = helper.getWritableDatabase();
        //helper.onCreate(database);
        isOpened = true;
    }

    public void close() {
        helper.close();
        isOpened = false;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void addItem(Item item) {
        ContentValues newItem = new ContentValues();
        newItem.put(COLUMN_ITEM_NAME, item.getName());
        newItem.put(COLUMN_ITEM_PRICE, item.getPrice());
        newItem.put(COLUMN_ITEM_CATEGORY_ID, item.getCategory().getName());
        try {
            database.insertOrThrow(MENU_ITEMS_TABLE, null, newItem);
        } catch(Exception e) {
            Log.e("Error inserting rows ", e.toString());
            e.printStackTrace();
        }
        Cursor c = database.rawQuery("SELECT last_insert_rowid()", null);
        c.moveToFirst();
        long id = c.getLong(0);
        //id += 1;
        item.setId(id);
        c.close();
    }


    /*public void addCategory(String category, String section){
        ContentValues newCategory = new ContentValues();
        newCategory.put(COLUMN_CATEGORY_NAME,category);
        newCategory.put(COLUMN_CATEGORY_SECTION,section);
        try {
            database.insertOrThrow(CATEGORIES_TABLE, null, newCategory);
        } catch (Exception e) {
            Log.e("Error inserting rows ", e.toString());
            e.printStackTrace();
        }
        //menuCategories.add(category);
    }*/

    public void addOrder(Order order) {

        //Create order summary record
        ContentValues newOrderSummary = new ContentValues();
        newOrderSummary.put(COLUMN_ORDERSUMMARY_CUSTOMER_NAME, order.getCustomerName());
        newOrderSummary.put(COLUMN_ORDERSUMMARY_TIMESTAMP, order.getTimestamp().getTime());
        try {
            database.insertOrThrow(ORDER_SUMMARY_TABLE, null, newOrderSummary);
        } catch(Exception e) {
            Log.e("Error inserting rows ", e.toString());
            e.printStackTrace();
        }

        Cursor c = database.rawQuery("SELECT last_insert_rowid()", null);
        c.moveToFirst();
        long id = c.getLong(0);
        //id += 1;
        order.setId(id);
        c.close();

        //Create the order detail records
        for(Item item : order.getOrderedItems()) {
            ContentValues newOrderDetail = new ContentValues();
            newOrderDetail.put(COLUMN_ORDERDETAILS_ORDERID, order.getId());
            newOrderDetail.put(COLUMN_ORDERDETAILS_ITEMID, item.getId());
            newOrderDetail.put(COLUMN_ORDERDETAILS_ITEMQUANTITY, item.getQuantityDesired());
            newOrderDetail.put(COLUMN_ORDERDETAILS_ITEMPRICE, item.getPrice());
            try {
                database.insertOrThrow(ORDER_DETAILS_TABLE, null, newOrderDetail);
            } catch(Exception e) {
                Log.e("Error inserting rows ", e.toString());
                e.printStackTrace();
            }
        }
    }

    public void deleteItem(Item item) {
        long id = item.getId();
        Log.i("helper", "Item deleted: " + id); // Specify which database and table the item came from
        database.delete(MENU_ITEMS_TABLE, COLUMN_ITEM_ID + " = " + id, null);
    }


    public ArrayList getItems(Category category) {
        ArrayList items = new ArrayList();

        if(database == null) Log.w("WARNING", "database object is null.");

        Cursor cursor = database.query(MENU_ITEMS_TABLE, readableColumnsItemsTable, COLUMN_ITEM_CATEGORY_ID + " = '" + category.getId() + "'", null, null, null, null);


        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private void populateCategories() {
        ArrayList categories = new ArrayList();
        if(database == null) {
            categories = null;
        }
        Cursor cursor = database.query(CATEGORIES_TABLE, readableColumnsCategoriesTable, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category.getId(), category);
            cursor.moveToNext();
        }
        cursor.close();
        this.categories = categories;
    }

    private Item cursorToItem(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        float price = cursor.getFloat(2);
        int categoryId = cursor.getInt(3);
        Category category = categories.get(categoryId); //This works because every category's index in the categories ArrayList is equal to its category ID.
        Item item = new Item(id, name, price, category);
        return item;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category(cursor.getInt(0), cursor.getString(1), Section.forString(cursor.getString(2)));
        return category;
    }

    public class SQLHelper extends SQLiteOpenHelper {

        public SQLHelper() {
            super(MyApplication.getAppContext(), DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            Log.d("MenuItemsDataSource", "Database is being created");
            String CREATE_MENU_TABLE = "CREATE TABLE "
                    + MENU_ITEMS_TABLE + " ("
                    + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                    + COLUMN_ITEM_PRICE + " FLOAT NOT NULL,"
                    + COLUMN_ITEM_CATEGORY_ID + " INTEGER NOT NULL);"; //If this structure changes, DB_VERSION should be incremented.


            String CREATE_CATEGORIES_TABLE = "CREATE TABLE "
                    + CATEGORIES_TABLE + " ("
                    + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_CATEGORY_NAME + " TEXT, "
                    + COLUMN_CATEGORY_SECTION + " TEXT);"; //If this structure changes, DB_VERSION should be incremented.

            String CREATE_ORDER_SUMMARY_TABLE = "CREATE TABLE "
                    + ORDER_SUMMARY_TABLE + " ("
                    + COLUMN_ORDERSUMMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ORDERSUMMARY_CUSTOMER_NAME + " TEXT, "
                    + COLUMN_ORDERSUMMARY_TIMESTAMP + " INTEGER);";

            String CREATE_ORDER_DETAILS_TABLE = "CREATE TABLE "
                    + ORDER_DETAILS_TABLE + " ("
                    + COLUMN_ORDERDETAILS_ORDERID + " INTEGER, "
                    + COLUMN_ORDERDETAILS_ITEMID + " INTEGER, "
                    + COLUMN_ORDERDETAILS_ITEMQUANTITY + " INTEGER, "
                    + COLUMN_ORDERDETAILS_ITEMPRICE + " FLOAT);";

            database.execSQL(CREATE_CATEGORIES_TABLE);
            database.execSQL(CREATE_MENU_TABLE);
            database.execSQL(CREATE_ORDER_SUMMARY_TABLE);
            database.execSQL(CREATE_ORDER_DETAILS_TABLE);

            boolean isFirstTime = true;
            String ADD_CATEGORY;
            for(String currentCategory : MENU_CATEGORIES_SEED) {
                currentCategory = toCategoryName(currentCategory);
                if(isFirstTime) {
                    ADD_CATEGORY = "INSERT INTO " + CATEGORIES_TABLE + " VALUES(0, '" + currentCategory + "', 'menu')";
                    isFirstTime = false;
                } else {
                    ADD_CATEGORY = "INSERT INTO " + CATEGORIES_TABLE + " VALUES(null, '" + currentCategory + "', 'menu')";
                }


                database.execSQL(ADD_CATEGORY);
            }

        }


        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(SQLHelper.class.getName(), "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ". All data is being deleted.");

            database.execSQL("DROP TABLE IF EXISTS " + MENU_ITEMS_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + ORDER_SUMMARY_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + ORDER_DETAILS_TABLE);

            onCreate(database);
        }
    }


}
