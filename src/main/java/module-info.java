module com.example.pension {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Nécessaire pour PostgreSQL
    requires org.postgresql.jdbc; // Driver PostgreSQL
    requires org.apache.pdfbox;

    opens com.example.pension.shared to javafx.fxml;
    opens com.example.pension to javafx.fxml;
    opens com.example.pension.controller to javafx.fxml;
    opens com.example.pension.controller.Popup to javafx.fxml;
    opens com.example.pension.util;
    opens com.example.pension.model to javafx.base; // si tu utilises les modèles dans des TableView

    exports com.example.pension;
}