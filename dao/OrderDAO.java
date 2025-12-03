package dao;

import model.CartItem;
import model.Order;
import model.FoodItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int createOrder(Order order) throws SQLException {
        String insertOrder = "INSERT INTO orders (customer_name, total) VALUES (?, ?)";
        String insertItem = "INSERT INTO order_item (order_id, food_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psOrder = c.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getCustomerName());
                psOrder.setDouble(2, order.getTotal());
                psOrder.executeUpdate();
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) {
                        int orderId = keys.getInt(1);
                        try (PreparedStatement psItem = c.prepareStatement(insertItem)) {
                            for (CartItem ci : order.getItems()) {
                                psItem.setInt(1, orderId);
                                psItem.setInt(2, ci.getFood().getId());
                                psItem.setInt(3, ci.getQuantity());
                                psItem.setDouble(4, ci.getFood().getPrice());
                                psItem.addBatch();
                            }
                            psItem.executeBatch();
                        }
                        c.commit();
                        return orderId;
                    } else {
                        c.rollback();
                        throw new SQLException("Failed to create order, no ID obtained.");
                    }
                }
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT id, customer_name, total, created_at FROM orders ORDER BY created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = new Order(rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getDouble("total"),
                        rs.getTimestamp("created_at").toLocalDateTime());
                orders.add(o);
            }
        }
        return orders;
    }

    public List<String> findOrderDetails(int orderId) throws SQLException {
        List<String> details = new ArrayList<>();
        String sql = "SELECT oi.quantity, oi.price, fi.name FROM order_item oi JOIN food_item fi ON oi.food_id = fi.id WHERE oi.order_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int q = rs.getInt(1);
                    double p = rs.getDouble(2);
                    String name = rs.getString(3);
                    details.add(String.format("%s x%d - $%.2f each", name, q, p));
                }
            }
        }
        return details;
    }
}
