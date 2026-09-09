package com.booking.controller;

import com.booking.gui.SceneNavigator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * View controller for the login screen.
 * Delegates authentication to {@link AppController} and handles UI feedback only.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private AppController appController;
    private Stage stage;

    /**
     * Injects the application controller responsible for business operations.
     *
     * @param appController shared application controller
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Injects the primary stage used for scene navigation.
     *
     * @param stage primary application stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the login button and password field submit action.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isBlank(username) || isBlank(password)) {
            showError("Missing credentials", "Please enter both username and password.");
            return;
        }

        LoginResult result = appController.handleLogin(username.trim(), password);
        if (!result.isSuccess()) {
            showError("Login failed", result.getErrorMessage());
            passwordField.clear();
            if (result.getErrorMessage() != null && result.getErrorMessage().startsWith("Too many failed attempts")) {
                lockLoginButtonFor30Seconds();
            }
            return;
        }

        try {
            SceneNavigator.show(stage, result.getDashboardViewPath(), controller -> {
                if (controller instanceof DashboardController dashboardController) {
                    dashboardController.setAppController(appController);
                    dashboardController.setStage(stage);
                    dashboardController.bindUser(result.getUser());
                }
            });
            stage.setTitle(appController.getDashboardTitle(result.getUser()));
        } catch (IOException e) {
            showError("Navigation error", "Unable to open the dashboard view.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Disables the login button for 30 seconds to mirror the server-side
     * lockout enforced by {@code AuthService}, giving the user clear visual
     * feedback that further attempts are pointless until it re-enables.
     */
    private void lockLoginButtonFor30Seconds() {
        loginButton.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(30));
        pause.setOnFinished(event -> loginButton.setDisable(false));
        pause.play();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
