package client.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Consumer<String> onMessageReceived;

    public void connect() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Sunucudan gelen mesajları dinle (ayrı thread)
        Thread listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(line);
                    }
                }
            } catch (IOException e) {
                if (onMessageReceived != null) {
                    onMessageReceived.accept("ERROR|Connection lost");
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void send(String message) {
        if (out != null) out.println(message);
    }

    public void login(String username, String password) {
        send("LOGIN|" + username + "|" + password);
    }

    public void register(String username, String password, String displayName) {
        send("REGISTER|" + username + "|" + password + "|" + displayName);
    }

    public void sendMessage(String receiverId, String content) {
        send("MSG|" + receiverId + "|" + content);
    }

    public void requestHistory() {
        send("HISTORY");
    }

    public void requestOnlineUsers() {
        send("USERS");
    }

    public void setOnMessageReceived(Consumer<String> handler) {
        this.onMessageReceived = handler;
    }

    public void disconnect() throws IOException {
        if (socket != null) socket.close();
    }
}
