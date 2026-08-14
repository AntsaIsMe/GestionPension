module com.example.pension {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Nécessaire pour PostgreSQL
    
    opens com.example.pension.shared to javafx.fxml;
    opens com.example.pension to javafx.fxml;
    exports com.example.pension;
}