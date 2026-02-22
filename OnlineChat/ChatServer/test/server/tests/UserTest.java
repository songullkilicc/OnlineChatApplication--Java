package server.tests;

import org.junit.jupiter.api.Test;
import server.model.User;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User("john", "pass123", "John Doe");
        assertEquals("john", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("John Doe", user.getDisplayName());
    }

    @Test
    public void testToCsv() {
        User user = new User("john", "pass123", "John Doe");
        assertEquals("john,pass123,John Doe", user.toCsv());
    }

    @Test
    public void testFromCsv() {
        User user = User.fromCsv("john,pass123,John Doe");
        assertEquals("john", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("John Doe", user.getDisplayName());
    }

    @Test
    public void testSetDisplayName() {
        User user = new User("john", "pass123", "John");
        user.setDisplayName("John Doe");
        assertEquals("John Doe", user.getDisplayName());
    }
}