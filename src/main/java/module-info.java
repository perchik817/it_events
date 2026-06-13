module whz.it_events.it_eventsdbapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jakarta.persistence;
    requires org.jdbi.v3.core;


    opens whz.it_events.it_eventsdbapp to javafx.fxml;
    exports whz.it_events.it_eventsdbapp;
    exports whz.it_events.it_eventsdbapp.service;
    exports whz.it_events.it_eventsdbapp.service.dto;
}
