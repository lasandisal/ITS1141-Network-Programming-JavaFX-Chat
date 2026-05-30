package lk.ijse.practical1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerLauncher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Just point to the correct FXML path and load it normally
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/ijse/practical1/Server.fxml"));

        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Chat Server Dashboard");
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show(); // Added this missing line so the UI actually displays!
    }

    public static void main(String[] args) {
        launch(args);
    }
}