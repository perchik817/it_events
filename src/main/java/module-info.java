module whz.it_events.it_eventsdbapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jakarta.persistence;


    opens whz.it_events.it_eventsdbapp to javafx.fxml;
    exports whz.it_events.it_eventsdbapp;
}