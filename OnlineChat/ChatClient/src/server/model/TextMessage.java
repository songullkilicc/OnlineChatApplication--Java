package server.model;

public class TextMessage extends Message<String> {

    public TextMessage(String senderId, String receiverId, String content) {
        super(senderId, receiverId, content);
    }

    @Override
    public String getType() {
        return "TEXT";
    }

    public String toCsv() {
        return getType() + "," + getSenderId() + "," + getReceiverId() + ","
                + getTimestamp() + "," + getContent().replace(",", ";;");
    }

    public static TextMessage fromCsv(String line) {
        String[] parts = line.split(",", 5);
        String content = parts[4].replace(";;", ",");
        return new TextMessage(parts[1], parts[2], content);
    }
}

