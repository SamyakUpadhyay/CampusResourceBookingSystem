package com.booking.controller;

import com.booking.exception.BookingException;
import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Resource;
import com.booking.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard view controller for staff users. Extends the student dashboard so
 * staff keep the same "My Bookings" behaviour, adding a pending-approvals queue.
 */
public class StaffDashboardController extends StudentDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    private TableView<Booking> approvalsTable;

    @FXML
    private TableColumn<Booking, String> approvalBookingIdColumn;

    @FXML
    private TableColumn<Booking, String> approvalUserColumn;

    @FXML
    private TableColumn<Booking, String> approvalResourceColumn;

    @FXML
    private TableColumn<Booking, String> approvalStartColumn;

    @FXML
    private TableColumn<Booking, String> approvalEndColumn;

    private boolean approvalsColumnsInitialized;

    private void setupApprovalsTableColumns() {
        if (approvalsColumnsInitialized) {
            return;
        }
        approvalBookingIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBookingId()));
        approvalUserColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUserId()));
        approvalResourceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(resolveResourceLabel(data.getValue().getResourceId())));
        approvalStartColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStartTime().format(DATE_TIME_FORMAT)));
        approvalEndColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEndTime().format(DATE_TIME_FORMAT)));
        approvalsColumnsInitialized = true;
    }

    @Override
    protected String getRoleBadgeStyleClass() {
        return "role-badge-staff";
    }

    @Override
    protected void onUserBound() {
        super.onUserBound();
        refreshApprovals();
    }

    @FXML
    protected void handleRefreshApprovals() {
        refreshApprovals();
    }

    @FXML
    protected void handleApprove() {
        withSelectedApproval(booking -> {
            try {
                appController.getAuthService().setCurrentUser(currentUser);
                appController.getBookingService().approveBooking(booking.getBookingId());
                refreshApprovals();
            } catch (BookingException e) {
                showError("Unable to approve booking", e.getMessage());
            }
        });
    }

    @FXML
    protected void handleReject() {
        withSelectedApproval(booking -> {
            try {
                appController.getAuthService().setCurrentUser(currentUser);
                appController.getBookingService().rejectBooking(booking.getBookingId());
                refreshApprovals();
            } catch (BookingException e) {
                showError("Unable to reject booking", e.getMessage());
            }
        });
    }

    private void withSelectedApproval(java.util.function.Consumer<Booking> action) {
        Booking selected = approvalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No booking selected", "Please select a pending booking first.");
            return;
        }
        action.accept(selected);
    }

    private void refreshApprovals() {
        if (appController == null) {
            return;
        }
        setupApprovalsTableColumns();
        List<Booking> pending = appController.getBookingService().getAllBookings().stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.PENDING)
                .collect(Collectors.toList());
        approvalsTable.setItems(FXCollections.observableArrayList(pending));
    }

    private String resolveResourceLabel(String resourceId) {
        Resource resource = appController.getResourceManager().findById(resourceId);
        return resource == null ? resourceId : resource.getName() + " (" + resourceId + ")";
    }
}
