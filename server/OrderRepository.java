package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderRepository {
    private Connection conn;

    public OrderRepository(Connection conn) {
        this.conn = conn;
    }

    public String processOrder(String order) {
        try {
            String[] parts = order.split(":");
            String sql = "INSERT INTO orders (item, quantity) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, parts[0]);
            ps.setInt(2, Integer.parseInt(parts[1]));
            ps.executeUpdate();

            System.out.println("Order saved: " + order);
            return "Order placed: " + order;

        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to place order: SQL error";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to place order: invalid input";
        }
    }
}
