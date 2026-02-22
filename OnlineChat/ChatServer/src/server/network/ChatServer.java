package server.network;

import server.storage.MessageStore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    public static final int PORT = 12345;

    private final MessageStore store = new MessageStore();
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public void start() {
    	System.out.println("Server starting on port: " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this, store);
                new Thread(handler).start();
            }
        } catch (IOException e) {
        	System.err.println("Server error: " + e.getMessage());
        }
    }

    public void registerClient(String username, ClientHandler handler) {
        clients.put(username, handler);
    }

    public void removeClient(String username) {
        clients.remove(username);
    }

    public void sendToClient(String username, String message) {
        ClientHandler handler = clients.get(username);
        if (handler != null) handler.send(message);
    }

    public void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler h : clients.values()) {
            if (h != exclude) h.send(message);
        }
    }

    public Collection<String> getOnlineUsers() {
        return clients.keySet();
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
