package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class CafeServer {
    private ServerSocket serverSocket;
    private OrderRepository repository;

    public CafeServer(OrderRepository repo) {
        this.repository = repo;
    }

    public void start() throws Exception {
        serverSocket = new ServerSocket(5000);
        System.out.println("Server running...");

        while (true) {
            Socket client = serverSocket.accept();
            ClientHandler handler = new ClientHandler(client, repository);
            handler.start();
        }
    }

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/order_food";
        String user = "root";
        String password = "Marsien06@0608";
        Connection conn = DriverManager.getConnection(url, user, password);

        OrderRepository repo = new OrderRepository(conn);
        CafeServer server = new CafeServer(repo);
        server.start();
    }
}
