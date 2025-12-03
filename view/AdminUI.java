package view;

import client.SocketClient;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.FoodItem;
import model.Order;

public class AdminUI extends JFrame {
    private SocketClient client = new SocketClient("localhost", 5000);
    private DefaultTableModel foodModel, orderModel;
    private JTable foodTable, orderTable;

    public AdminUI() {
        setTitle("Admin Panel");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // close only this window

        JTabbedPane tabs = new JTabbedPane();

        // Food tab
        foodModel = new DefaultTableModel(new Object[]{"ID","Name","Category","Price"},0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        foodTable = new JTable(foodModel);
        JPanel foodPanel = new JPanel(new BorderLayout());
        foodPanel.add(new JScrollPane(foodTable), BorderLayout.CENTER);

        JPanel foodBtns = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        foodBtns.add(btnAdd); foodBtns.add(btnEdit); foodBtns.add(btnDelete);
        foodPanel.add(foodBtns, BorderLayout.SOUTH);

        // Orders tab
        orderModel = new DefaultTableModel(new Object[]{"ID","Customer","Total","Created At"},0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        orderTable = new JTable(orderModel);
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        JButton btnDetails = new JButton("Details");
        orderPanel.add(btnDetails, BorderLayout.SOUTH);

        tabs.add("Menu", foodPanel);
        tabs.add("Orders", orderPanel);
        add(tabs);

        loadFoods();
        loadOrders();

        btnAdd.addActionListener(e -> addFood());
        btnEdit.addActionListener(e -> editFood());
        btnDelete.addActionListener(e -> deleteFood());
        btnDetails.addActionListener(e -> showDetails());
    }

    //methods
    private void loadFoods() {
        foodModel.setRowCount(0);
        try {
            for(FoodItem f : client.getMenu()){
                foodModel.addRow(new Object[]{f.getId(), f.getName(), f.getCategory(), String.format("%.2f", f.getPrice())});
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error loading foods: "+ex.getMessage());
        }
    }

    private void loadOrders(){
        orderModel.setRowCount(0);
        try {
            for(Order o : client.getOrders()){
                orderModel.addRow(new Object[]{o.getId(), o.getCustomerName(), String.format("%.2f", o.getTotal()), o.getCreatedAt()});
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error loading orders: "+ex.getMessage());
        }
    }

    private void addFood(){
        JTextField name=new JTextField(), cat=new JTextField(), price=new JTextField();
        Object[] fields={"Name:",name,"Category:",cat,"Price:",price};
        if(JOptionPane.showConfirmDialog(this, fields, "Add Food", JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return;
        try {
            double p = Double.parseDouble(price.getText());
            FoodItem f = new FoodItem(name.getText().trim(), cat.getText().trim(), p);
            client.addOrEditFood(f);
            loadFoods();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Invalid data: "+ex.getMessage()); }
    }

    private void editFood(){
        int r=foodTable.getSelectedRow();
        if(r==-1) return;
        int id=Integer.parseInt(foodModel.getValueAt(r,0).toString());
        try {
            String name=foodModel.getValueAt(r,1).toString();
            String cat=foodModel.getValueAt(r,2).toString();
            double price=Double.parseDouble(foodModel.getValueAt(r,3).toString());
            JTextField fn=new JTextField(name), fc=new JTextField(cat), fp=new JTextField(String.valueOf(price));
            Object[] fields={"Name:",fn,"Category:",fc,"Price:",fp};
            if(JOptionPane.showConfirmDialog(this, fields,"Edit Food",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return;
            FoodItem f=new FoodItem(id, fn.getText().trim(), fc.getText().trim(), Double.parseDouble(fp.getText()));
            client.addOrEditFood(f);
            loadFoods();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void deleteFood(){
        int r=foodTable.getSelectedRow();
        if(r==-1) return;
        int id=Integer.parseInt(foodModel.getValueAt(r,0).toString());
        if(JOptionPane.showConfirmDialog(this,"Delete this item?")!=JOptionPane.YES_OPTION) return;
        try {
            client.deleteFood(id);
            loadFoods();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Can't delete: "+ex.getMessage()); }
    }

    private void showDetails(){
        int r=orderTable.getSelectedRow();
        if(r==-1) return;
        int id=Integer.parseInt(orderModel.getValueAt(r,0).toString());
        try {
            List<String> details = client.getOrderDetails(id);
            StringBuilder sb=new StringBuilder();
            for(String s: details) sb.append(s).append("\n");
            JOptionPane.showMessageDialog(this,sb.length()>0?sb.toString():"No details.");
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error loading details: "+ex.getMessage()); }
    }
}
