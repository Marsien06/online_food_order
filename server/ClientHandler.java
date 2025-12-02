package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket client;
    private OrderRepository repository;

    public ClientHandler(Socket client, OrderRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {

            String request;
            while ((request = reader.readLine()) != null) {
                String response = repository.processOrder(request);
                writer.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
