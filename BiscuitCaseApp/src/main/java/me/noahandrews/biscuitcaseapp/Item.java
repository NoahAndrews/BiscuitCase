package me.noahandrews.biscuitcaseapp;

import java.io.Serializable;

public class Item implements Serializable {
    private long id;
    private String name = "";
    private double price = 0;
    private Category category;
    private boolean hasLimitedQuantity;

    private int quantityAvailable = 0;
    private int quantityDesired = 0;

    public Item() {
    }

    public Item(long id, String name, double price, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    //Copy constructor
    public Item(Item original) {
        this.id = original.id;
        this.name = original.name;
        this.price = original.price;
        this.category = original.category;
        this.hasLimitedQuantity = original.hasLimitedQuantity;
        this.quantityAvailable = original.quantityAvailable;
        this.quantityDesired = original.quantityDesired;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityDesired() {
        return quantityDesired;
    }

    public void setQuantityDesired(int quantityDesired) {
        if(quantityDesired >= 0 && quantityDesired <= 999)
            this.quantityDesired = quantityDesired;
        else
            throw new IndexOutOfBoundsException("Quantity out of range.");
    }

    public void increaseQuantityDesired() {
        if(quantityDesired < 999)
            quantityDesired++;
        //TODO else, throw an exception with a descriptive message
    }

    public void decreaseQuantityDesired() {
        if(quantityDesired > 0)
            quantityDesired--;

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}