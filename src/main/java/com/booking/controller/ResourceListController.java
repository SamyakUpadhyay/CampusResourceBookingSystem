package com.booking.controller;

import com.booking.gui.SceneNavigator;
import com.booking.model.AvailabilityStatus;
import com.booking.model.Resource;
import com.booking.model.ResourceType;
import com.booking.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View controller for the resource listing screen. Lets any authenticated
 * user search/filter the campus resource catalogue and hand off a selected
 * resource to the booking form via an inline "Book Now" action per row.
 */
public class ResourceListController {

    private static final String ALL = "All";

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeFilter;

    @FXML
    private ComboBox<String> availabilityFilter;

    @FXML
    private TableView<Resource> resourceTable;

    @FXML
    private TableColumn<Resource, String> idColumn;

    @FXML
    private TableColumn<Resource, String> nameColumn;

    @FXML
    private TableColumn<Resource, ResourceType> typeColumn;

    @FXML
    private TableColumn<Resource, String> locationColumn;

    @FXML
    private TableColumn<Resource, Integer> capacityColumn;

    @FXML
    private TableColumn<Resource, AvailabilityStatus> statusColumn;

    @FXML
    private TableColumn<Resource, Void> actionColumn;

    private AppController appController;
    private Stage stage;
    private User currentUser;

    /**
     * Wires up table columns and filter drop-downs. Invoked automatically by
     * the {@link javafx.fxml.FXMLLoader} once the view is loaded.
     */
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("resourceType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(AvailabilityStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }
                Label pill = new Label(prettify(status.name()));
                pill.getStyleClass().addAll("status-pill", pillStyleClass(status));
                setGraphic(pill);
                setText(null);
            }
        });

        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button bookButton = new Button("Book Now");

            {
                bookButton.getStyleClass().add("primary-button");
                bookButton.setStyle("-fx-font-size: 11.5px; -fx-padding: 6 14 6 14;");
                bookButton.setOnAction(event -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    navigateToBooking(resource);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Resource resource = getTableView().getItems().get(getIndex());
                bookButton.setDisable(resource.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE);
                setGraphic(bookButton);
            }
        });

        ObservableList<String> typeOptions = FXCollections.observableArrayList(ALL);
        for (ResourceType type : ResourceType.values()) {
            typeOptions.add(type.name());
        }
        typeFilter.setItems(typeOptions);
        typeFilter.setValue(ALL);

        ObservableList<String> availabilityOptions = FXCollections.observableArrayList(ALL);
        for (AvailabilityStatus status : AvailabilityStatus.values()) {
            availabilityOptions.add(status.name());
        }
        availabilityFilter.setItems(availabilityOptions);
        availabilityFilter.setValue(ALL);

        typeFilter.setOnAction(event -> refreshTable());
        availabilityFilter.setOnAction(event -> refreshTable());
        searchField.setOnAction(event -> refreshTable());
    }

    /**
     * Injects the shared application controller.
     *
     * @param appController application controller
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
        refreshTable();
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
     * Injects the currently authenticated user, used when creating a booking.
     *
     * @param currentUser authenticated user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    @FXML
    private void handleBack() {
        try {
            SceneNavigator.show(stage, appController.getDashboardViewPath(currentUser.getRole()),
                    controller -> {
                        if (controller instanceof DashboardController dashboardController) {
                            dashboardController.setAppController(appController);
                            dashboardController.setStage(stage);
                            dashboardController.bindUser(currentUser);
                        }
                    });
            stage.setTitle(appController.getDashboardTitle(currentUser));
        } catch (IOException e) {
            showError("Navigation error", "Unable to return to the dashboard.");
        }
    }

    private void navigateToBooking(Resource resource) {
        try {
            SceneNavigator.show(stage, AppController.BOOKING_VIEW, controller -> {
                if (controller instanceof BookingController bookingController) {
                    bookingController.setAppController(appController);
                    bookingController.setStage(stage);
                    bookingController.setContext(currentUser, resource);
                }
            });
            stage.setTitle("CRSBS - Book Resource");
        } catch (IOException e) {
            showError("Navigation error", "Unable to open the booking form.");
        }
    }

    private void refreshTable() {
        if (appController == null) {
            return;
        }

        List<Resource> allResources = appController.getResourceManager().getAllResources();
        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedType = typeFilter.getValue() == null ? ALL : typeFilter.getValue();
        String selectedAvailability = availabilityFilter.getValue() == null ? ALL : availabilityFilter.getValue();

        List<Resource> filtered = allResources.stream()
                .filter(resource -> selectedType.equals(ALL)
                        || resource.getResourceType().name().equals(selectedType))
                .filter(resource -> selectedAvailability.equals(ALL)
                        || resource.getAvailabilityStatus().name().equals(selectedAvailability))
                .filter(resource -> searchText.isEmpty()
                        || resource.getName().toLowerCase().contains(searchText)
                        || resource.getLocation().toLowerCase().contains(searchText)
                        || resource.getResourceId().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        resourceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private String pillStyleClass(AvailabilityStatus status) {
        return switch (status) {
            case AVAILABLE -> "status-pill-available";
            case UNAVAILABLE -> "status-pill-booked";
            case MAINTENANCE -> "status-pill-maintenance";
        };
    }

    private String prettify(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                builder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase())
                        .append(' ');
            }
        }
        return builder.toString().trim();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
