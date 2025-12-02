package client;

import java.io.*;
import java.net.Socket;

public class CafeClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public CafeClient() throws Exception {
        socket = new Socket("localhost", 5000);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendOrder(String order) throws IOException {
        writer.println(order);
        return reader.readLine();
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
