package server.tests;

import org.junit.jupiter.api.Test;
import server.model.TextMessage;
import static org.junit.jupiter.api.Assertions.*;

public class TextMessageTest {

    @Test
    public void testMessageCreation() {
        TextMessage msg = new TextMessage("alice", "bob", "Hello!");
        assertEquals("alice", msg.getSenderId());
        assertEquals("bob", msg.getReceiverId());
        assertEquals("Hello!", msg.getContent());
    }

    @Test
    public void testGetType() {
        TextMessage msg = new TextMessage("alice", "bob", "Hello!");
        assertEquals("TEXT", msg.getType());
    }

    @Test
    public void testToCsv() {
        TextMessage msg = new TextMessage("alice", "bob", "Hello!");
        String csv = msg.toCsv();
        assertTrue(csv.startsWith("TEXT,alice,bob,"));
        assertTrue(csv.endsWith(",Hello!"));
    }

    @Test
    public void testFromCsv() {
        TextMessage msg = new TextMessage("alice", "bob", "Hello!");
        String csv = msg.toCsv();
        TextMessage restored = TextMessage.fromCsv(csv);
        assertEquals("alice", restored.getSenderId());
        assertEquals("bob", restored.getReceiverId());
        assertEquals("Hello!", restored.getContent());
    }

    @Test
    public void testContentWithComma() {
        TextMessage msg = new TextMessage("alice", "bob", "Hello, world!");
        String csv = msg.toCsv();
        TextMessage restored = TextMessage.fromCsv(csv);
        assertEquals("Hello, world!", restored.getContent());
    }
}