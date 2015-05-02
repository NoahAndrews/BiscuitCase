package me.noahandrews.biscuitcaselibrary;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private Section section;

    public Category(String name, Section section){
        this.name = name;
        this.section = section;
    }

    public Category(int id, @NonNull String name, Section section) {
        this(name, section);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = ItemsDataSource.toCategoryName(name);
        this.name = name;
    }

    public Section getSection() {
        return section;
    }
}
