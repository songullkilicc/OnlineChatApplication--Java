package server.storage;

import server.exceptions.InvalidUserException;
import server.model.TextMessage;
import server.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageStore {

    private static final String MESSAGES_FILE = "messages.csv";
    private static final String USERS_FILE = "users.csv";

    public synchronized void saveUser(User user) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(user.toCsv());
            writer.newLine();
        }
    }

    public List<User> loadAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    users.add(User.fromCsv(line));
                }
            }
        }
        return users;
    }

    public User findUser(String username, String password)
            throws IOException, InvalidUserException {
        List<User> users = loadAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        throw new InvalidUserException("Invalid username or password: " + username);
    }

    public boolean userExists(String username) throws IOException {
        List<User> users = loadAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) return true;
        }
        return false;
    }

    public synchronized void saveMessage(TextMessage message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGES_FILE, true))) {
            writer.write(message.toCsv());
            writer.newLine();
        }
    }

    public List<TextMessage> loadAllMessages() throws IOException {
        List<TextMessage> messages = new ArrayList<>();
        File file = new File(MESSAGES_FILE);
        if (!file.exists()) return messages;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    messages.add(TextMessage.fromCsv(line));
                }
            }
        }
        return messages;
    }
}