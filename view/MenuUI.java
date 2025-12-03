package view;

import dao.FoodDAO;
import dao.OrderDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.CartItem;
import model.FoodItem;
import model.Order;

public class MenuUI extends JFrame {
    private FoodDAO foodDAO = new FoodDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private List<CartItem> cart = new ArrayList<>();

    private DefaultTableModel menuModel;
    private JTable menuTable;
    private JLabel totalLabel;

    public MenuUI() {
        setTitle("Online Food Ordering - Customer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        menuModel = new DefaultTableModel(new Object[]{"ID","Name","Category","Price"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(menuModel);
        JScrollPane spMenu = new JScrollPane(menuTable);

        JButton btnRefresh = new JButton("Refresh Menu");

        JButton btnAddToCart = new JButton("Add to Cart");
        JButton btnViewCart = new JButton("View Cart");
        JButton btnAdmin = new JButton("Admin Panel");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnAddToCart);
        top.add(btnViewCart);
        top.add(btnAdmin);
        top.add(btnRefresh);

        totalLabel = new JLabel("Cart: 0 items, Total: $0.00");

        add(top, BorderLayout.NORTH);
        add(spMenu, BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);

        loadMenu();

        btnAddToCart.addActionListener(e -> addSelectedToCart());
        btnViewCart.addActionListener(e -> new CartUI(this, cart).setVisible(true));
        btnAdmin.addActionListener(e -> {AdminUI admin = new AdminUI();
            admin.setVisible(true);
        });
        btnRefresh.addActionListener(e->refresh());
    }


    //methods
    public void refresh() {
        loadMenu();
        updateTotalLabel();
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        try {
            for (FoodItem f : foodDAO.findAll()) {
                menuModel.addRow(new Object[]{f.getId(), f.getName(), f.getCategory(), String.format("%.2f", f.getPrice())});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSelectedToCart() {
        int r = menuTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }
        int id = Integer.parseInt(menuModel.getValueAt(r,0).toString());
        try {
            FoodItem f = foodDAO.findById(id);
            if (f == null) return;
            String qtyStr = JOptionPane.showInputDialog(this, "Quantity:", "1");
            int q = 1;
            try { q = Integer.parseInt(qtyStr); if (q <= 0) throw new NumberFormatException(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid quantity. Using 1."); q = 1; }
            boolean found = false;
            for (CartItem ci : cart) {
                if (ci.getFood().getId() == f.getId()) {
                    ci.setQuantity(ci.getQuantity() + q);
                    found = true;
                    break;
                }
            }
            if (!found) cart.add(new CartItem(f, q));
            updateTotalLabel();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    public void updateTotalLabel() {
        int totalItems = cart.stream().mapToInt(CartItem::getQuantity).sum();
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        totalLabel.setText(String.format("Cart: %d items, Total: $%.2f", totalItems, total));
    }

    public void checkout(String customerName) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        Order order = new Order(customerName, total, cart);
        try {
            int orderId = orderDAO.createOrder(order);
            JOptionPane.showMessageDialog(this, "Order placed. ID: " + orderId);
            cart.clear();
            updateTotalLabel();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to place order: " + ex.getMessage());
        }
    }
}
