module lk.ijse.practical1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.practical1 to javafx.fxml;
    exports lk.ijse.practical1;

    opens lk.ijse.practical1.controller to javafx.fxml;
}