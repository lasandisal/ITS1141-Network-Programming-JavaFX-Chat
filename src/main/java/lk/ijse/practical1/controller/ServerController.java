package lk.ijse.practical1.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lk.ijse.practical1.util.FileUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController {

    @FXML private TextArea textArea;
    @FXML private TextField textField;
    @FXML private TextField textUsername;
    @FXML private Text textName;

    protected final List<ClientHandler> clients = new ArrayList<>();

    public void initialize() {
        textUsername.setDisable(true);
        textName.setText("SERVER-ADMIN");

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(6055)){
                Platform.runLater(() -> textArea.appendText("Server Started on port 3000...\n"));
                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket, this);
                    clients.add(handler);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void broadcastText(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            // If sender is null, it goes to everyone. If sender matches, it skips them.
            if (sender == null || client != sender) {
                client.sendRawText(message);
            }
        }
        Platform.runLater(() -> textArea.appendText(message + "\n"));
    }

    public void broadcastFile(String fileName, long size, byte[] data, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendRawFile(fileName, size, data);
            }
        }
        Platform.runLater(() -> textArea.appendText("[FILE SHARED]: " + fileName + "\n"));
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String msg = textField.getText().trim();
        if (!msg.isEmpty()) {
            // Pass null so the admin message is delivered to all clients
            broadcastText("Server-Admin: " + msg, null);
            textField.clear();
        }
    }

    @FXML void btnEmojiOnAction(ActionEvent event) { textField.appendText("😎"); }
    @FXML void btnFileOnAction(ActionEvent event) { Platform.runLater(() -> textArea.appendText("Admin cannot send local files directly.\n")); }
}