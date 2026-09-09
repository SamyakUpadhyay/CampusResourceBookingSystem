module com.example.groupproject {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.booking.model;
    exports com.booking.service;
    exports com.booking.fileio;
    exports com.booking.exception;
    exports com.booking.util;
    exports com.booking.gui;
    exports com.booking.controller;

    opens com.booking.gui to javafx.fxml;
    opens com.booking.controller to javafx.fxml;
    opens com.booking.model to javafx.base;
}
