package lk.ijse.practical1.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.practical1.util.FileUtil;
import java.io.*;
import java.net.Socket;

public class ClientController {

    @FXML private TextArea textArea;
    @FXML private TextField textField;
    @FXML private TextField textUsername;
    @FXML private Text textName;

    private DataOutputStream dos;

    @FXML
    public void initialize() {
        textName.setText("Offline");
        textUsername.setOnAction(event -> handleUsernameSubmit());
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        sendTextMessage();
    }

    @FXML
    void textFieldOnAction(ActionEvent event) {
        sendTextMessage();
    }

    private void sendTextMessage() {
        if (dos == null) return;
        try {
            String msg = textField.getText().trim();
            if (msg.isEmpty()) return;
            FileUtil.sendText(msg, dos);
            textArea.appendText("Me: " + msg + "\n");
            textField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnFileOnAction(ActionEvent event) {
        if (dos == null) return;
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog((Stage) textField.getScene().getWindow());

        if (file != null && file.exists()) {
            new Thread(() -> {
                try {
                    FileUtil.sendFile(file, dos);
                    Platform.runLater(() -> textArea.appendText("Me shared file: " + file.getName() + "\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        textField.appendText("😊");
        textField.requestFocus();
    }

    public void handleUsernameSubmit() {
        String name = textUsername.getText().trim();
        if (!name.isEmpty()) {
            connectToServer(name);
            textName.setText(name);
            textUsername.setDisable(true);
        }
    }

    private void connectToServer(String name) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 6055);
                dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                dos.writeUTF(name);
                dos.flush();

                Platform.runLater(() -> textArea.appendText("[System]: Connected to server!\n"));

                while (true) {
                    int type = dis.readInt();
                    if (type == FileUtil.TYPE_TEXT) {
                        String msg = dis.readUTF();
                        Platform.runLater(() -> textArea.appendText(msg + "\n"));
                    } else if (type == FileUtil.TYPE_FILE) {
                        String absolutePath = FileUtil.receiveAndSaveFile(dis, "downloads");
                        Platform.runLater(() -> textArea.appendText("[FILE RECEIVED]: Saved to " + absolutePath + "\n"));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> textArea.appendText("Disconnected from Server.\n"));
            }
        }).start();
    }
}