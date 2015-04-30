package me.noahandrews.biscuitcaseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class StoreItemsDataSource {
    public static final String DB_NAME = "store.db";
    public static final int DB_VERSION = 1; // If this is incremented, we should really add code
    public static final String COLUMN_ID = "id"; //The name of the field containing the item's unique ID
    public static final String COLUMN_NAME = "item_name"; //The name of the field containing the item's name
    // to onUpgrade that will maintain the items entered.
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity"; //This is the quantity available to be purchased
    private SQLHelper helper;
    private SQLiteDatabase database;
    private String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_PRICE, COLUMN_QUANTITY};

    public StoreItemsDataSource(Context context) {
        helper = new SQLHelper(context);
    }

    private String toTableName(String string) {
        return string.replace(' ', '_');
    }

    private String toDisplayName(String string) {
        return string.replace('_', ' ');
    }

    public void open() throws SQLiteException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void createCategory(String category) {
        category = toTableName(category);

        String CREATE_TABLE = "CREATE TABLE "
                + category + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_PRICE + " FLOAT, "
                + COLUMN_QUANTITY + " INTEGER);"; //If this structure changes, DB_VERSION should be incremented.

        database.execSQL(CREATE_TABLE);
    }

    public void createItem(String category, String name, double price, int quantityAvailable) { //will be called from management app
        ContentValues newItem = new ContentValues();
        newItem.put(COLUMN_NAME, name);
        newItem.put(COLUMN_PRICE, price);
        newItem.put(COLUMN_QUANTITY, quantityAvailable);
        try {
            database.insertOrThrow(toTableName(category), null, newItem);
        } catch(Exception e) {
            Log.e("Error in inserting rows ", e.toString());
            e.printStackTrace();
        }

        // The tutorial on vogella.com has this function return the created item
        // using the the cursorToItem function.
    }

    public void deleteItem(Item item) {
        long id = item.getId();
        Log.i("helper", "Item deleted: " + id); // Specify which database and table the item came from
        database.delete(toTableName(item.getCategory().getName()), COLUMN_ID + " = " + id, null);
    }

    public ArrayList<ItemStore> getAllItems(String category) {
        category = toTableName(category);
        ArrayList<ItemStore> items = new ArrayList<ItemStore>();

        Cursor cursor = database.query(category, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            ItemStore item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private ItemStore cursorToItem(Cursor cursor) {
        ItemStore item = new ItemStore();
        item.setId(cursor.getLong(0));
        item.setName(cursor.getString(1));
        item.setPrice(cursor.getFloat(2));
        item.setQuantityAvailable(cursor.getInt(3));
        return item;
    }

    public class SQLHelper extends SQLiteOpenHelper {


        public SQLHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            //This line is commented out because by default, the database
            //will have no tables. All tables are added by the management app.
            //database.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(SQLHelper.class.getName(), "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ". All data is being deleted.");
            // database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            database.deleteDatabase(new File(DB_NAME));
            onCreate(database);
        }
    }
}
