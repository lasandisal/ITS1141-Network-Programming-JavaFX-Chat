package lk.ijse.practical1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientLauncher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Just point to the correct FXML path and load it normally
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/ijse/practical1/Client.fxml"));

        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Chat Client Chatroom");
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}