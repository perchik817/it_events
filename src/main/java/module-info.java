module whz.it_events.it_eventsdbapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jakarta.persistence;
    requires org.jdbi.v3.core;
    requires org.hibernate.orm.core;

    opens whz.it_events.it_eventsdbapp to javafx.fxml;
    opens whz.it_events.it_eventsdbapp.controller to javafx.fxml;
    opens whz.it_events.it_eventsdbapp.model to org.hibernate.orm.core;
//    я добавила
    opens whz.it_events.it_eventsdbapp.model.enums to org.hibernate.orm.core, javafx.base;

    exports whz.it_events.it_eventsdbapp;
    exports whz.it_events.it_eventsdbapp.service;
    exports whz.it_events.it_eventsdbapp.service.dto;
}