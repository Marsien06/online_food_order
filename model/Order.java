package model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private double total;
    private LocalDateTime createdAt;
    private List<CartItem> items;

    public Order(int id, String customerName, double total, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Order(String customerName, double total, List<CartItem> items) {
        this(0, customerName, total, LocalDateTime.now());
        this.items = items;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public double getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CartItem> getItems() { return items; }
}
