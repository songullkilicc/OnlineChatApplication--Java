package server.model;

import java.time.LocalDateTime;

public abstract class Message<T> {
    private final String senderId;
    private final String receiverId;
    private final LocalDateTime timestamp;
    private T content;

    public Message(String senderId, String receiverId, T content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public T getContent() { return content; }
    public void setContent(T content) { this.content = content; }

    public abstract String getType();

    @Override
    public String toString() {
        return "[" + timestamp + "] " + senderId + " -> " + receiverId + ": " + content;
    }
}