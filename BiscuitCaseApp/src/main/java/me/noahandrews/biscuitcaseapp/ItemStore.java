package me.noahandrews.biscuitcaseapp;

import java.io.Serializable;

public class ItemStore implements Serializable {
    private long id;
    private String name = "";
    private double price = 0;
    private String category = "";

    private int quantityAvailable = 0;
    private int quantityDesired = 0;

    //    public ItemMenu(long id, String name, double price, int quantityAvailable){
//        this.setId(id);
//        this.setName(name);
//        this.setPrice(price);
//        this.setQuantityAvailable(quantityAvailable);
//    }
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
        if(quantityDesired >= 0 && quantityDesired <= quantityAvailable)
            this.quantityDesired = quantityDesired;
        else
            throw new IndexOutOfBoundsException("Quantity out of range.");
    }

    public void increaseQuantityDesired() {
        if(quantityDesired < quantityAvailable)
            quantityDesired++;
        else
            throw new IndexOutOfBoundsException("We have no more of that item.");
    }

    public void decreaseQuantityDesired() {
        if(quantityDesired > 0)
            quantityDesired--;

    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}