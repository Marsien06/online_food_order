package client;

import java.io.*;
import java.net.Socket;
import java.util.List;
import model.FoodItem;
import model.Order;

public class SocketClient {
    private String host;
    private int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public List<FoodItem> getMenu() throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject("GET_MENU");
            return (List<FoodItem>) in.readObject();
        }
    }

    public int placeOrder(Order order) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(order);
            return (int) in.readObject();
        }
    }

    public List<Order> getOrders() throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject("GET_ORDERS");
            return (List<Order>) in.readObject();
        }
    }

    public List<String> getOrderDetails(int orderId) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject("GET_ORDER_DETAILS:" + orderId);
            return (List<String>) in.readObject();
        }
    }

    public void deleteFood(int foodId) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject("DELETE_FOOD:" + foodId);
            in.readObject(); 
        }
    }

    public int addOrEditFood(FoodItem f) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(f);
            Object res = in.readObject();
            if (res instanceof Integer) return (Integer) res; // new food ID
            return 0; 
        }
    }
}
