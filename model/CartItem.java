package model;

public class CartItem {
    private FoodItem food;
    private int quantity;

    public CartItem(FoodItem food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public FoodItem getFood() { return food; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public double getSubtotal() { return food.getPrice() * quantity; }
}
