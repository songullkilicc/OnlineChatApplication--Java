package server.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.exceptions.InvalidUserException;
import server.model.TextMessage;
import server.model.User;
import server.storage.MessageStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageStoreTest {

    private MessageStore store;

    @BeforeEach
    public void setUp() {
        // Her testten önce dosyaları temizle
        new File("users.csv").delete();
        new File("messages.csv").delete();
        store = new MessageStore();
    }

    @Test
    public void testSaveAndLoadUser() throws IOException {
        User user = new User("alice", "pass", "Alice");
        store.saveUser(user);

        List<User> users = store.loadAllUsers();
        assertEquals(1, users.size());
        assertEquals("alice", users.get(0).getUsername());
    }

    @Test
    public void testUserExists() throws IOException {
        User user = new User("alice", "pass", "Alice");
        store.saveUser(user);
        assertTrue(store.userExists("alice"));
        assertFalse(store.userExists("bob"));
    }

    @Test
    public void testFindUserSuccess() throws IOException, InvalidUserException {
        User user = new User("alice", "pass", "Alice");
        store.saveUser(user);
        User found = store.findUser("alice", "pass");
        assertEquals("alice", found.getUsername());
    }

    @Test
    public void testFindUserInvalidThrowsException() throws IOException {
        assertThrows(InvalidUserException.class, () -> {
            store.findUser("nobody", "wrongpass");
        });
    }

    @Test
    public void testSaveAndLoadMessage() throws IOException {
        TextMessage msg = new TextMessage("alice", "bob", "Hey!");
        store.saveMessage(msg);

        List<TextMessage> messages = store.loadAllMessages();
        assertEquals(1, messages.size());
        assertEquals("alice", messages.get(0).getSenderId());
        assertEquals("Hey!", messages.get(0).getContent());
    }
}