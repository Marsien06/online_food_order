package client;

public class Order {
    private String item;
    private int quantity;

    public Order(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public String serialize() {
        return item + ":" + quantity;
    }

    public static Order deserialize(String data) {
        String[] parts = data.split(":");
        return new Order(parts[0], Integer.parseInt(parts[1]));
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
