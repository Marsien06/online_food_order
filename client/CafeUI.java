package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CafeUI {

    private Menu menu;
    private CafeClient client;

    public CafeUI() {
        menu = new Menu();
        try {
            client = new CafeClient(); // may throw Exception
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to server: " + e.getMessage());
            System.exit(1);
        }
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Cafe Order System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel menuLabel = new JLabel("Select Item:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(menuLabel, gbc);

        JComboBox<String> menuBox = new JComboBox<>();
        for (String item : menu.getItems()) {
            menuBox.addItem(item);
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(menuBox, gbc);

        JLabel qtyLabel = new JLabel("Quantity:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(qtyLabel, gbc);

        JTextField qtyField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(qtyField, gbc);

        JButton orderBtn = new JButton("Place Order");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(orderBtn, gbc);

        JLabel resultLabel = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(resultLabel, gbc);

        orderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedItem = (String) menuBox.getSelectedItem();
                    int quantity = Integer.parseInt(qtyField.getText());
                    Order order = new Order(selectedItem, quantity);
                    String response = client.sendOrder(order.serialize());
                    resultLabel.setText(response);
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Enter a valid quantity!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    resultLabel.setText("Error sending order.");
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CafeUI());
    }
}
