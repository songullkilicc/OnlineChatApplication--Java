package server.model;

public class TextMessage extends Message<String> {

    public TextMessage(String senderId, String receiverId, String content) {
        super(senderId, receiverId, content);
    }

    @Override
    public String getType() {
        return "TEXT";
    }

    // CSV formatında kaydetmek için
    public String toCsv() {
        return getType() + "," + getSenderId() + "," + getReceiverId() + ","
                + getTimestamp() + "," + getContent().replace(",", ";;");
    }

    public static TextMessage fromCsv(String line) {
        String[] parts = line.split(",", 5);
        // parts[0]=type, parts[1]=sender, parts[2]=receiver, parts[3]=timestamp, parts[4]=content
        String content = parts[4].replace(";;", ",");
        return new TextMessage(parts[1], parts[2], content);
    }
}
