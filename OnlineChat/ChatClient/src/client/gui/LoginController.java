package client.gui;

import client.network.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final ServerConnection connection = new ServerConnection();

    @FXML
    public void initialize() {
        try {
            connection.connect();
            connection.setOnMessageReceived(this::handleServerMessage);
        } catch (Exception e) {
            errorLabel.setText("Cannot connect to server!");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            return;
        }
        connection.login(username, password);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            return;
        }
        connection.register(username, password, username);
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split("\\|", -1);
        switch (parts[0]) {
            case "LOGIN_OK" -> Platform.runLater(() -> openChatWindow(parts[1]));
            case "REGISTER_OK" -> Platform.runLater(() ->
                    errorLabel.setText("Account created! You can now login."));
            case "ERROR" -> Platform.runLater(() ->
                    errorLabel.setText(parts[1]));
        }
    }

    private void openChatWindow(String displayName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent root = loader.load();

            ChatController controller = loader.getController();
            controller.init(connection, displayName);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("ChatApp - " + displayName);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Failed to open chat window!");
            e.printStackTrace();
        }
    }
}