package com.booking.controller;

import com.booking.fileio.LogManager;
import com.booking.model.Admin;
import com.booking.model.AvailabilityStatus;
import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Resource;
import com.booking.model.ResourceType;
import com.booking.model.Role;
import com.booking.model.Staff;
import com.booking.model.Student;
import com.booking.model.User;
import com.booking.service.ResourceFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard view controller for administrator users. Extends the staff
 * dashboard (pending approvals + own bookings) and layers on full resource
 * management, user directory viewing, audit log inspection, and a
 * sidebar-navigated overview matching the Part A UI prototype.
 */
public class AdminDashboardController extends StaffDashboardController {

    // ---------- Sidebar navigation ----------

    @FXML
    private VBox overviewPane;

    @FXML
    private VBox approvalsPane;

    @FXML
    private VBox resourcesPane;

    @FXML
    private VBox usersPane;

    @FXML
    private VBox auditPane;

    @FXML
    private VBox myBookingsPane;

    @FXML
    private Button navOverview;

    @FXML
    private Button navApprovals;

    @FXML
    private Button navResources;

    @FXML
    private Button navUsers;

    @FXML
    private Button navAuditLogs;

    @FXML
    private Button navMyBookings;

    // ---------- Overview ----------

    @FXML
    private Label totalBookingsValue;

    @FXML
    private Label pendingApprovalsValue;

    @FXML
    private Label totalUsersValue;

    @FXML
    private Label activeResourcesValue;

    @FXML
    private BarChart<String, Number> bookingsChart;

    // ---------- Manage Resources ----------

    @FXML
    private TableView<Resource> resourcesTable;

    @FXML
    private TableColumn<Resource, String> resIdColumn;

    @FXML
    private TableColumn<Resource, String> resNameColumn;

    @FXML
    private TableColumn<Resource, String> resTypeColumn;

    @FXML
    private TableColumn<Resource, String> resLocationColumn;

    @FXML
    private TableColumn<Resource, String> resCapacityColumn;

    @FXML
    private TableColumn<Resource, String> resStatusColumn;

    @FXML
    private TextField newResourceIdField;

    @FXML
    private TextField newResourceNameField;

    @FXML
    private TextField newResourceLocationField;

    @FXML
    private TextField newResourceCapacityField;

    @FXML
    private ComboBox<ResourceType> newResourceTypeCombo;

    @FXML
    private ComboBox<AvailabilityStatus> newResourceStatusCombo;

    @FXML
    private Button saveResourceButton;

    @FXML
    private Button cancelEditResourceButton;

    /** Resource ID currently loaded into the form for editing, or {@code null} in "add new" mode. */
    private String editingResourceId;

    // ---------- Manage Users ----------

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> userFullNameColumn;

    @FXML
    private TableColumn<User, String> userRoleColumn;

    @FXML
    private TableColumn<User, String> userEmailColumn;

    @FXML
    private TableColumn<User, String> userActiveColumn;

    @FXML
    private ComboBox<Role> roleReassignCombo;

    @FXML
    private Button toggleActiveButton;

    @FXML
    private Button reassignRoleButton;

    // ---------- Audit Log ----------

    @FXML
    private TextArea auditLogArea;

    private boolean resourceColumnsInitialized;
    private boolean userColumnsInitialized;

    @Override
    protected String getRoleBadgeStyleClass() {
        return "role-badge-admin";
    }

    @Override
    protected void onUserBound() {
        super.onUserBound();
        newResourceTypeCombo.setItems(FXCollections.observableArrayList(ResourceType.values()));
        newResourceStatusCombo.setItems(FXCollections.observableArrayList(AvailabilityStatus.values()));
        roleReassignCombo.setItems(FXCollections.observableArrayList(Role.values()));
        refreshResources();
        refreshUsers();
        refreshAuditLog();
        refreshOverview();
        showOverview();
    }

    // ---------- Sidebar navigation ----------

    @FXML
    private void showOverview() {
        refreshOverview();
        activatePane(overviewPane, navOverview);
    }

    @FXML
    private void showApprovals() {
        handleRefreshApprovals();
        activatePane(approvalsPane, navApprovals);
    }

    @FXML
    private void showResources() {
        refreshResources();
        activatePane(resourcesPane, navResources);
    }

    @FXML
    private void showUsers() {
        refreshUsers();
        activatePane(usersPane, navUsers);
    }

    @FXML
    private void showAuditLogs() {
        refreshAuditLog();
        activatePane(auditPane, navAuditLogs);
    }

    @FXML
    private void showMyBookings() {
        refreshBookings();
        activatePane(myBookingsPane, navMyBookings);
    }

    private void activatePane(VBox pane, Button navButton) {
        for (VBox candidate : List.of(overviewPane, approvalsPane, resourcesPane, usersPane, auditPane, myBookingsPane)) {
            boolean active = candidate == pane;
            candidate.setVisible(active);
            candidate.setManaged(active);
        }
        for (Button button : List.of(navOverview, navApprovals, navResources, navUsers, navAuditLogs, navMyBookings)) {
            button.getStyleClass().setAll(button == navButton ? "sidebar-nav-button-active" : "sidebar-nav-button");
        }
    }

    // ---------- Overview stats & chart ----------

    private void refreshOverview() {
        if (appController == null) {
            return;
        }
        List<Booking> allBookings = appController.getBookingService().getAllBookings();
        long pendingCount = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING)
                .count();

        totalBookingsValue.setText(String.valueOf(allBookings.size()));
        pendingApprovalsValue.setText(String.valueOf(pendingCount));
        totalUsersValue.setText(String.valueOf(appController.getAuthService().getUsers().size()));
        activeResourcesValue.setText(String.valueOf(appController.getResourceManager().getResourceCount()));

        Map<String, Integer> countsByResourceId = new HashMap<>();
        for (Booking booking : allBookings) {
            if (booking.getBookingStatus() == BookingStatus.CANCELLED
                    || booking.getBookingStatus() == BookingStatus.REJECTED) {
                continue;
            }
            countsByResourceId.merge(booking.getResourceId(), 1, Integer::sum);
        }

        int totalActiveBookings = countsByResourceId.values().stream().mapToInt(Integer::intValue).sum();

        List<Map.Entry<String, Integer>> topFive = countsByResourceId.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .toList();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : topFive) {
            Resource resource = appController.getResourceManager().findById(entry.getKey());
            String label = resource != null ? resource.getResourceId() : entry.getKey();
            double utilizationPercent = totalActiveBookings == 0
                    ? 0
                    : (entry.getValue() * 100.0) / totalActiveBookings;
            series.getData().add(new XYChart.Data<>(label, utilizationPercent));
        }

        bookingsChart.getData().clear();
        bookingsChart.getData().add(series);
    }

    // ---------- Manage Resources ----------

    @FXML
    private void handleSaveResource() {
        try {
            if (editingResourceId != null) {
                saveResourceEdits();
            } else {
                saveNewResource();
            }
        } catch (NumberFormatException e) {
            showError("Invalid capacity", "Capacity must be a whole number.");
        } catch (IllegalArgumentException e) {
            showError("Unable to save resource", e.getMessage());
        }
    }

    private void saveNewResource() {
        String id = require(newResourceIdField.getText(), "Resource ID");
        String name = require(newResourceNameField.getText(), "Name");
        String location = require(newResourceLocationField.getText(), "Location");
        int capacity = requirePositiveCapacity(newResourceCapacityField.getText());
        ResourceType type = newResourceTypeCombo.getValue();
        AvailabilityStatus status = newResourceStatusCombo.getValue();
        if (type == null || status == null) {
            showError("Missing details", "Please choose a resource type and availability status.");
            return;
        }

        Resource resource = ResourceFactory.createResource(type, id, name, location, capacity, status);
        appController.getResourceManager().addResource(resource);
        appController.saveResources();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId()
                + " added resource " + id);

        clearResourceForm();
        refreshResources();
        refreshOverview();
    }

    private void saveResourceEdits() {
        Resource resource = appController.getResourceManager().findById(editingResourceId);
        if (resource == null) {
            showError("Resource not found", "The resource being edited no longer exists.");
            clearResourceForm();
            refreshResources();
            return;
        }

        String name = require(newResourceNameField.getText(), "Name");
        String location = require(newResourceLocationField.getText(), "Location");
        int capacity = requirePositiveCapacity(newResourceCapacityField.getText());
        AvailabilityStatus status = newResourceStatusCombo.getValue();
        if (status == null) {
            showError("Missing details", "Please choose an availability status.");
            return;
        }

        // Only the common Resource fields are editable here; the resource's
        // type-specific attributes (e.g. whiteboard, equipment quantity) and
        // its ID/type stay unchanged, so we mutate in place rather than
        // recreating the object via ResourceFactory.
        resource.setName(name);
        resource.setLocation(location);
        resource.setCapacity(capacity);
        resource.setAvailabilityStatus(status);

        appController.getResourceManager().updateResource(resource);
        appController.saveResources();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId()
                + " updated resource " + resource.getResourceId());

        clearResourceForm();
        refreshResources();
        refreshOverview();
    }

    private int requirePositiveCapacity(String rawValue) {
        int capacity = Integer.parseInt(require(rawValue, "Capacity"));
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        return capacity;
    }

    @FXML
    private void handleEditResource() {
        Resource selected = resourcesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No resource selected", "Please select a resource to edit.");
            return;
        }

        editingResourceId = selected.getResourceId();
        newResourceIdField.setText(selected.getResourceId());
        newResourceIdField.setDisable(true);
        newResourceNameField.setText(selected.getName());
        newResourceLocationField.setText(selected.getLocation());
        newResourceCapacityField.setText(String.valueOf(selected.getCapacity()));
        newResourceTypeCombo.setValue(selected.getResourceType());
        newResourceTypeCombo.setDisable(true);
        newResourceStatusCombo.setValue(selected.getAvailabilityStatus());

        saveResourceButton.setText("Save Changes");
        cancelEditResourceButton.setVisible(true);
        cancelEditResourceButton.setManaged(true);
    }

    @FXML
    private void handleCancelEditResource() {
        clearResourceForm();
    }

    private void clearResourceForm() {
        editingResourceId = null;
        newResourceIdField.clear();
        newResourceIdField.setDisable(false);
        newResourceNameField.clear();
        newResourceLocationField.clear();
        newResourceCapacityField.clear();
        newResourceTypeCombo.setValue(null);
        newResourceTypeCombo.setDisable(false);
        newResourceStatusCombo.setValue(null);
        saveResourceButton.setText("Add Resource");
        cancelEditResourceButton.setVisible(false);
        cancelEditResourceButton.setManaged(false);
    }

    @FXML
    private void handleDeleteResource() {
        Resource selected = resourcesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No resource selected", "Please select a resource to delete.");
            return;
        }
        appController.getResourceManager().removeResource(selected.getResourceId());
        appController.saveResources();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId()
                + " removed resource " + selected.getResourceId());
        refreshResources();
        refreshOverview();
    }

    @FXML
    private void handleToggleMaintenance() {
        Resource selected = resourcesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No resource selected", "Please select a resource to update.");
            return;
        }
        AvailabilityStatus next = selected.getAvailabilityStatus() == AvailabilityStatus.MAINTENANCE
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.MAINTENANCE;
        selected.setAvailabilityStatus(next);
        appController.getResourceManager().updateResource(selected);
        appController.saveResources();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId()
                + " set resource " + selected.getResourceId() + " to " + next);
        refreshResources();
    }

    @FXML
    private void handleRefreshUsers() {
        refreshUsers();
    }

    @FXML
    private void handleToggleUserActive() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No user selected", "Please select a user to enable or disable.");
            return;
        }
        if (selected.getUserId().equals(currentUser.getUserId())) {
            showError("Not allowed", "You cannot disable your own account.");
            return;
        }
        selected.setActive(!selected.isActive());
        appController.getAuthService().saveUsers();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId() + " set account "
                + selected.getUserId() + " to " + (selected.isActive() ? "ACTIVE" : "DISABLED"));
        refreshUsers();
    }

    @FXML
    private void handleReassignRole() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        Role newRole = roleReassignCombo.getValue();
        if (selected == null) {
            showError("No user selected", "Please select a user to reassign.");
            return;
        }
        if (newRole == null) {
            showError("No role selected", "Please choose a role to assign.");
            return;
        }
        if (selected.getUserId().equals(currentUser.getUserId())) {
            showError("Not allowed", "You cannot change your own role.");
            return;
        }
        if (newRole == selected.getRole()) {
            showError("No change", selected.getUsername() + " already has the " + newRole + " role.");
            return;
        }

        User replacement = buildUserWithRole(selected, newRole);
        List<User> users = appController.getAuthService().getUsers();
        int index = users.indexOf(selected);
        if (index >= 0) {
            users.set(index, replacement);
        }
        appController.getAuthService().saveUsers();
        LogManager.getInstance().logInfo("Admin " + currentUser.getUserId() + " reassigned "
                + selected.getUserId() + " from " + selected.getRole() + " to " + newRole);
        roleReassignCombo.setValue(null);
        refreshUsers();
    }

    /**
     * Rebuilds {@code original} as a different concrete {@link User} subclass
     * for the given target role, preserving the shared identity fields
     * (ID, username, password hash, name, email, active flag). Role-specific
     * fields (student/staff ID, department) are carried over where the
     * source role has an equivalent, or generated as a placeholder otherwise
     * — since role is expressed as the concrete Java type in this model
     * rather than a plain field, reassignment necessarily means constructing
     * a new instance.
     */
    private User buildUserWithRole(User original, Role newRole) {
        String department = "Unassigned";
        if (original instanceof Student student) {
            department = student.getDepartment();
        } else if (original instanceof Staff staff) {
            department = staff.getDepartment();
        }

        User replacement = switch (newRole) {
            case STUDENT -> new Student(original.getUserId(), original.getUsername(), original.getPasswordHash(),
                    original.getFullName(), original.getEmail(),
                    original instanceof Student s ? s.getStudentId() : "STU-" + original.getUserId(),
                    department);
            case STAFF -> new Staff(original.getUserId(), original.getUsername(), original.getPasswordHash(),
                    original.getFullName(), original.getEmail(),
                    original instanceof Staff s ? s.getStaffId() : "STF-" + original.getUserId(),
                    department);
            case ADMIN -> new Admin(original.getUserId(), original.getUsername(), original.getPasswordHash(),
                    original.getFullName(), original.getEmail(),
                    original instanceof Admin a ? a.getAdminId() : "ADM-" + original.getUserId());
        };
        replacement.setActive(original.isActive());
        return replacement;
    }

    @FXML
    private void handleRefreshAuditLog() {
        refreshAuditLog();
    }

    private void setupResourceColumns() {
        if (resourceColumnsInitialized) {
            return;
        }
        resIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResourceId()));
        resNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        resTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResourceType().name()));
        resLocationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
        resCapacityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCapacity())));
        resStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAvailabilityStatus().name()));
        resourceColumnsInitialized = true;
    }

    private void setupUserColumns() {
        if (userColumnsInitialized) {
            return;
        }
        userIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        userFullNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        userRoleColumn.setCellValueFactory(data -> new SimpleStringProperty(roleLabel(data.getValue())));
        userEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        userActiveColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isActive() ? "Active" : "Disabled"));
        userColumnsInitialized = true;
    }

    private String roleLabel(User user) {
        Role role = user.getRole();
        return role == null ? "" : role.name();
    }

    private void refreshResources() {
        setupResourceColumns();
        List<Resource> all = appController.getResourceManager().getAllResources();
        resourcesTable.setItems(FXCollections.observableArrayList(all));
    }

    private void refreshUsers() {
        setupUserColumns();
        List<User> all = appController.getAuthService().getUsers();
        usersTable.setItems(FXCollections.observableArrayList(all));
    }

    private void refreshAuditLog() {
        try {
            Path path = Path.of(LogManager.getInstance().getLogFilePath());
            if (Files.exists(path)) {
                auditLogArea.setText(Files.readString(path));
                auditLogArea.positionCaret(auditLogArea.getText().length());
            } else {
                auditLogArea.setText("No audit log entries yet.");
            }
        } catch (IOException e) {
            auditLogArea.setText("Unable to read audit log: " + e.getMessage());
        }
    }

    private String require(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
