package view;

import model.CartItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import java.util.List;

public class CartUI extends JDialog {
    private DefaultTableModel model;
    private JTable table;
    private MenuUI parent;
    private List<CartItem> cart;

    public CartUI(MenuUI parent, List<CartItem> cart) {
        super(parent, "Cart", true);
        this.parent = parent;
        this.cart = cart;
        setSize(600, 350);
        setLocationRelativeTo(parent);
        model = new DefaultTableModel(new Object[]{"Food", "Qty", "Price", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JButton btnRemove = new JButton("Remove Selected");
        JButton btnChangeQty = new JButton("Change Quantity");
        JButton btnCheckout = new JButton("Checkout");

        JPanel p = new JPanel();
        p.add(btnRemove);
        p.add(btnChangeQty);
        p.add(btnCheckout);

        add(sp, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        reload();

        btnRemove.addActionListener(e -> removeSelected());
        btnChangeQty.addActionListener(e -> changeQty());
        btnCheckout.addActionListener(e -> doCheckout());
    }

    private void reload() {
        model.setRowCount(0);
        for (CartItem ci : cart) {
            model.addRow(new Object[]{ci.getFood().getName(), ci.getQuantity(), String.format("%.2f", ci.getFood().getPrice()), String.format("%.2f", ci.getSubtotal())});
        }
    }

    private void removeSelected() {
        int r = table.getSelectedRow();
        if (r == -1) return;
        cart.remove(r);
        reload();
        parent.updateTotalLabel();
    }

    private void changeQty() {
        int r = table.getSelectedRow();
        if (r == -1) return;
        String s = JOptionPane.showInputDialog(this, "New quantity:");
        try {
            int q = Integer.parseInt(s);
            if (q <= 0) throw new NumberFormatException();
            cart.get(r).setQuantity(q);
            reload();
            parent.updateTotalLabel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.");
        }
    }

    private void doCheckout() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name required.");
            return;
        }
        parent.checkout(name.trim());
        reload();
        parent.updateTotalLabel();
    }
}
