module com.pension {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Nécessaire pour PostgreSQL

    opens com.pension to javafx.fxml;
    exports com.pension;
}