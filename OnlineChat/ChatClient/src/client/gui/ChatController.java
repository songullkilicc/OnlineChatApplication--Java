package client.gui;

import client.network.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatController {

    @FXML private ListView<String> userListView;
    @FXML private VBox messageBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;
    @FXML private Text chatTitle;
    @FXML private Text loggedInLabel;

    private ServerConnection connection;
    private String currentUser;
    private String selectedUser;

    public void init(ServerConnection connection, String displayName) {
        this.connection = connection;
        this.currentUser = displayName;
        loggedInLabel.setText("Logged in as: " + displayName);

        connection.setOnMessageReceived(this::handleServerMessage);
        connection.requestOnlineUsers();
        connection.requestHistory();

        // Kullanıcı listesinden birine tıklanınca
        userListView.setOnMouseClicked(e -> {
            String selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedUser = selected;
                chatTitle.setText("Chat with " + selected);
            }
        });
    }

    @FXML
    private void handleSend() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;
        if (selectedUser == null) {
            showSystemMessage("Please select a user from the list first!");
            return;
        }
        connection.sendMessage(selectedUser, text);
        messageField.clear();
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split("\\|", -1);
        switch (parts[0]) {
            case "MSG" ->
                Platform.runLater(() -> showMessage(parts[1], parts[2], false));
            case "MSG_SENT" ->
                Platform.runLater(() -> showMessage("You", parts[2], true));
            case "USERS" ->
                Platform.runLater(() -> updateUserList(parts[1]));
            case "HISTORY_MSG" ->
                Platform.runLater(() -> showHistoryMessage(parts[1], parts[2], parts[3]));
            case "JOINED" ->
                Platform.runLater(() -> {
                    showSystemMessage(parts[1] + " joined the chat");
                    connection.requestOnlineUsers();
                });
            case "LEFT" ->
                Platform.runLater(() -> {
                    showSystemMessage(parts[1] + " left the chat");
                    connection.requestOnlineUsers();
                });
            case "ERROR" ->
                Platform.runLater(() -> showSystemMessage("Error: " + parts[1]));
        }
    }

    private void showMessage(String sender, String content, boolean isMine) {
        HBox row = new HBox();
        Text bubble = new Text(sender + ": " + content);
        bubble.setWrappingWidth(350);
        bubble.setStyle("-fx-fill: white;");

        String bgColor = isMine ? "#7c3aed" : "#2e2e3e";
        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane(bubble);
        pane.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10; -fx-background-radius: 12;");
        pane.setMaxWidth(400);

        if (isMine) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            row.getChildren().addAll(spacer, pane);
        } else {
            row.getChildren().add(pane);
        }

        messageBox.getChildren().add(row);
        scrollPane.setVvalue(1.0);
    }

    private void showHistoryMessage(String sender, String receiver, String content) {
        boolean isMine = sender.equals(currentUser);
        showMessage(isMine ? "You" : sender, content, isMine);
    }

    private void showSystemMessage(String text) {
        Text msg = new Text("⚙ " + text);
        msg.setStyle("-fx-fill: #888888; -fx-font-style: italic;");
        HBox row = new HBox(msg);
        row.setAlignment(javafx.geometry.Pos.CENTER);
        messageBox.getChildren().add(row);
        scrollPane.setVvalue(1.0);
    }

    private void updateUserList(String usersRaw) {
        userListView.getItems().clear();
        if (usersRaw.isBlank()) return;
        String[] users = usersRaw.split(",");
        for (String u : users) {
            if (!u.equals(currentUser)) {
                userListView.getItems().add(u);
            }
        }
    }
}