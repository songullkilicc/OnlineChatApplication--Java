package server.network;

import server.exceptions.InvalidUserException;
import server.model.TextMessage;
import server.model.User;
import server.storage.MessageStore;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private final MessageStore store;

    private PrintWriter out;
    private BufferedReader in;
    private User currentUser;

    public ClientHandler(Socket socket, ChatServer server, MessageStore store) {
        this.socket = socket;
        this.server = server;
        this.store = store;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                handleCommand(line);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    // Komut formatı:  KOMUT|param1|param2|...
    private void handleCommand(String raw) throws IOException {
        String[] parts = raw.split("\\|", -1);
        String cmd = parts[0];

        switch (cmd) {
            case "LOGIN"    -> handleLogin(parts);
            case "REGISTER" -> handleRegister(parts);
            case "MSG"      -> handleMessage(parts);
            case "HISTORY"  -> handleHistory();
            case "USERS"    -> handleUsers();
            default         -> send("ERROR|Unknown command");
        }
    }

    private void handleLogin(String[] parts) {
        // LOGIN|username|password
        try {
            User user = store.findUser(parts[1], parts[2]);
            this.currentUser = user;
            server.registerClient(user.getUsername(), this);
            send("LOGIN_OK|" + user.getDisplayName());
            server.broadcast("JOINED|" + user.getUsername(), this);
        } catch (InvalidUserException e) {
            send("ERROR|" + e.getMessage());
        } catch (IOException e) {
            send("ERROR|Server error");
        }
    }

    private void handleRegister(String[] parts) {
        // REGISTER|username|password|displayName
        try {
            if (store.userExists(parts[1])) {
                send("ERROR|Username already taken");
                return;
            }
            User newUser = new User(parts[1], parts[2], parts[3]);
            store.saveUser(newUser);
            send("REGISTER_OK");
        } catch (IOException e) {
            send("ERROR|Server error");
        }
    }

    private void handleMessage(String[] parts) {
        // MSG|receiverId|content
        if (currentUser == null) { send("ERROR|Please login first"); return; }
        try {
            TextMessage msg = new TextMessage(currentUser.getUsername(), parts[1], parts[2]);
            store.saveMessage(msg);
            // Alıcı online ise ilet
            server.sendToClient(parts[1], "MSG|" + currentUser.getUsername() + "|" + parts[2]);
            // Gönderene de echo
            send("MSG_SENT|" + parts[1] + "|" + parts[2]);
        } catch (IOException e) {
            send("ERROR|Message could not be saved");
        }
    }

    private void handleHistory() throws IOException {
        List<TextMessage> messages = store.loadAllMessages();
        for (TextMessage m : messages) {
            if (currentUser != null &&
               (m.getSenderId().equals(currentUser.getUsername()) ||
                m.getReceiverId().equals(currentUser.getUsername()))) {
                send("HISTORY_MSG|" + m.getSenderId() + "|" + m.getReceiverId()
                        + "|" + m.getContent() + "|" + m.getTimestamp());
            }
        }
        send("HISTORY_END");
    }

    private void handleUsers() {
        String users = String.join(",", server.getOnlineUsers());
        send("USERS|" + users);
    }

    public void send(String message) {
        out.println(message);
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    private void disconnect() {
        try {
            if (currentUser != null) {
                server.removeClient(currentUser.getUsername());
                server.broadcast("LEFT|" + currentUser.getUsername(), this);
            }
            socket.close();
        } catch (IOException ignored) {}
    }
} 


