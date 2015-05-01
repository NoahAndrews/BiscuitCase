package me.noahandrews.biscuitcaselibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {
    private long id;
    private String customerName;
    private ArrayList<Item> orderedItems;
    private Date timestamp;
    private boolean isSubmitted = false;

    public Order() {
        this.orderedItems = new ArrayList<>();
    }

    public Order(String customerName) {
        this();
        this.customerName = customerName;
    }

    public Order(String customerName, ArrayList<Item> orderedItems, boolean submitOrder) {
        this(customerName);
        this.orderedItems = orderedItems;
        if(submitOrder)
            submitOrder();
    }

    public void addItemToOrder(Item item) {
        if(!orderedItems.contains(item) && !isSubmitted) { //If the item is already on the list of ordered items, we don't want to add it again
            orderedItems.add(item);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArrayList<Item> getOrderedItems() {
        return orderedItems;
    }


    public void submitOrder() {
        //Create a details entry for each ordered item
        for(Item item : orderedItems) {
            item = new Item(item); //Copy the item, since the original will be reset.
        }
        timestamp = new Date(System.currentTimeMillis());

        //TODO make this section a function
        ItemsDataSource dataSource;
        dataSource = ItemsDataSource.INSTANCE;
        if(!dataSource.isOpened()) {
            dataSource.open();
        }

        dataSource.addOrder(this);
        //TODO update quantity available of items in database
        isSubmitted = true;
        OrderResetHelper.resetOrder();
    }
}
