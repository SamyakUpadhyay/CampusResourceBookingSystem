package com.booking.controller;

import com.booking.exception.BookingException;
import com.booking.model.Booking;
import com.booking.model.Resource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard view controller for student users.
 */
public class StudentDashboardController extends AbstractDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    protected TableView<Booking> bookingsTable;

    @FXML
    protected TableColumn<Booking, String> bookingIdColumn;

    @FXML
    protected TableColumn<Booking, String> resourceColumn;

    @FXML
    protected TableColumn<Booking, String> startColumn;

    @FXML
    protected TableColumn<Booking, String> endColumn;

    @FXML
    protected TableColumn<Booking, String> statusColumn;

    private boolean bookingsColumnsInitialized;

    private void setupBookingsTableColumns() {
        if (bookingsColumnsInitialized) {
            return;
        }
        bookingIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBookingId()));
        resourceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(resolveResourceName(data.getValue().getResourceId())));
        startColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStartTime().format(DATE_TIME_FORMAT)));
        endColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEndTime().format(DATE_TIME_FORMAT)));
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBookingStatus().name()));
        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                javafx.scene.control.Label pill = new javafx.scene.control.Label(status);
                pill.getStyleClass().addAll("status-pill", "status-pill-" + status.toLowerCase());
                setGraphic(pill);
                setText(null);
            }
        });
        bookingsColumnsInitialized = true;
    }

    @Override
    protected String getRoleBadgeStyleClass() {
        return "role-badge-student";
    }

    @Override
    protected void onUserBound() {
        refreshBookings();
    }

    @FXML
    protected void handleRefreshBookings() {
        refreshBookings();
    }

    @FXML
    protected void handleCancelBooking() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No booking selected", "Please select a booking to cancel.");
            return;
        }

        try {
            appController.getAuthService().setCurrentUser(currentUser);
            appController.getBookingService().cancelBooking(selected.getBookingId());
            refreshBookings();
        } catch (BookingException e) {
            showError("Unable to cancel booking", e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Cannot cancel booking", e.getMessage());
        }
    }

    protected void refreshBookings() {
        if (appController == null || currentUser == null) {
            return;
        }
        setupBookingsTableColumns();
        List<Booking> myBookings = appController.getBookingService()
                .getBookingsByUser(currentUser.getUserId());
        bookingsTable.setItems(FXCollections.observableArrayList(myBookings));
    }

    private String resolveResourceName(String resourceId) {
        Resource resource = appController.getResourceManager().findById(resourceId);
        return resource == null ? resourceId : resource.getName() + " (" + resourceId + ")";
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
