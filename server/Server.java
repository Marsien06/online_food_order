package server;

import dao.FoodDAO;
import dao.OrderDAO;
import model.FoodItem;
import model.Order;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private static FoodDAO foodDAO = new FoodDAO();
    private static OrderDAO orderDAO = new OrderDAO();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server running on port 5000...");

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }

    private static void handleClient(Socket client) {
        try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {

            Object request = in.readObject();

            if (request instanceof String) {
                String req = (String) request;

                switch (req) {
                    case "GET_MENU":
                        List<FoodItem> menu = foodDAO.findAll();
                        out.writeObject(menu);
                        break;

                    case "GET_ORDERS":
                        List<Order> orders = orderDAO.findAll();
                        out.writeObject(orders);
                        break;

                    default:
                        if (req.startsWith("GET_ORDER_DETAILS:")) {
                            int orderId = Integer.parseInt(req.split(":")[1]);
                            List<String> details = orderDAO.findOrderDetails(orderId);
                            out.writeObject(details);
                        } else if (req.startsWith("DELETE_FOOD:")) {
                            int foodId = Integer.parseInt(req.split(":")[1]);
                            foodDAO.delete(foodId);
                            out.writeObject("OK");
                        } else {
                            out.writeObject("UNKNOWN_COMMAND");
                        }
                        break;
                }

            } else if (request instanceof FoodItem) {
                FoodItem f = (FoodItem) request;
                if (f.getId() == 0) { // new food
                    foodDAO.insert(f);
                    out.writeObject(f.getId());
                } else { // edit food
                    foodDAO.update(f);
                    out.writeObject("OK");
                }

            } else if (request instanceof Order) {
                Order order = (Order) request;
                int orderId = orderDAO.createOrder(order);
                out.writeObject(orderId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
