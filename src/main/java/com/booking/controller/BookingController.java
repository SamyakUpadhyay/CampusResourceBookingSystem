package com.booking.controller;

import com.booking.exception.BookingException;
import com.booking.gui.SceneNavigator;
import com.booking.model.Booking;
import com.booking.model.Resource;
import com.booking.model.User;
import com.booking.service.BookingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * View controller for the booking form. Captures a date/time range for a
 * previously selected {@link Resource} and submits it through
 * {@link BookingService}, translating custom exceptions into user-facing alerts.
 */
public class BookingController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private Label resourceInfoLabel;

    @FXML
    private Label resourceDetailLabel;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> startTimeCombo;

    @FXML
    private ComboBox<String> endTimeCombo;

    @FXML
    private Label durationLabel;

    @FXML
    private TextArea notesArea;

    private AppController appController;
    private Stage stage;
    private User currentUser;
    private Resource resource;

    @FXML
    private void initialize() {
        List<String> slots = buildTimeSlots();
        startTimeCombo.setItems(FXCollections.observableArrayList(slots));
        endTimeCombo.setItems(FXCollections.observableArrayList(slots));
        datePicker.setValue(LocalDate.now());

        startTimeCombo.setOnAction(event -> updateDurationLabel());
        endTimeCombo.setOnAction(event -> updateDurationLabel());
    }

    /**
     * Injects the shared application controller.
     *
     * @param appController application controller
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Injects the primary stage for navigation.
     *
     * @param stage primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Supplies the authenticated user and the resource being booked.
     *
     * @param currentUser authenticated user making the booking
     * @param resource    resource selected on the previous screen
     */
    public void setContext(User currentUser, Resource resource) {
        this.currentUser = currentUser;
        this.resource = resource;
        resourceInfoLabel.setText(resource.getName() + " (" + resource.getResourceId() + ")");
        resourceDetailLabel.setText(resource.getResourceType() + " · " + resource.getLocation()
                + " · Capacity " + resource.getCapacity());
    }

    @FXML
    private void handleCancel() {
        returnToResourceList();
    }

    @FXML
    private void handleConfirm() {
        LocalDate date = datePicker.getValue();
        String startText = startTimeCombo.getValue();
        String endText = endTimeCombo.getValue();

        if (date == null || startText == null || endText == null) {
            showError("Missing details", "Please select a date, start time, and end time.");
            return;
        }

        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.parse(startText, TIME_FORMAT));
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.parse(endText, TIME_FORMAT));

        try {
            Booking booking = appController.getBookingService().createBooking(
                    currentUser.getUserId(),
                    resource.getResourceId(),
                    startTime,
                    endTime,
                    notesArea.getText());

            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Booking submitted");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Booking " + booking.getBookingId() + " is now "
                    + booking.getBookingStatus() + ".");
            confirmation.showAndWait();

            returnToResourceList();
        } catch (BookingException e) {
            showError("Booking failed", e.getMessage());
        }
    }

    private void returnToResourceList() {
        try {
            SceneNavigator.show(stage, AppController.RESOURCE_LIST_VIEW, controller -> {
                if (controller instanceof ResourceListController resourceListController) {
                    resourceListController.setAppController(appController);
                    resourceListController.setStage(stage);
                    resourceListController.setCurrentUser(currentUser);
                }
            });
            stage.setTitle("CRSBS - Campus Resources");
        } catch (IOException e) {
            showError("Navigation error", "Unable to return to the resource list.");
        }
    }

    private void updateDurationLabel() {
        String startText = startTimeCombo.getValue();
        String endText = endTimeCombo.getValue();
        if (startText == null || endText == null) {
            durationLabel.setText("");
            return;
        }

        LocalTime start = LocalTime.parse(startText, TIME_FORMAT);
        LocalTime end = LocalTime.parse(endText, TIME_FORMAT);
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0) {
            durationLabel.setText("End time must be after start time.");
        } else {
            durationLabel.setText("Duration: " + (minutes / 60) + "h " + (minutes % 60) + "m");
        }
    }

    private List<String> buildTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 7; hour <= 21; hour++) {
            slots.add(String.format("%02d:00", hour));
            slots.add(String.format("%02d:30", hour));
        }
        return slots;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
