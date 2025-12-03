package model;

import java.io.Serializable;

public class FoodItem implements Serializable{
    private int id;
    private String name;
    private String category;
    private double price;

    public FoodItem(int id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public FoodItem(String name, String category, double price) {
        this(0, name, category, price);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (" + category + ") - $" + String.format("%.2f", price);
    }
}
