package com.booking.controller;

import com.booking.gui.SceneNavigator;
import com.booking.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Shared behaviour for role-specific dashboard screens.
 */
public abstract class AbstractDashboardController implements DashboardController {

    @FXML
    protected Label welcomeLabel;

    @FXML
    protected Label roleBadge;

    @FXML
    protected Button logoutButton;

    protected AppController appController;
    protected Stage stage;
    protected User currentUser;

    @Override
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void bindUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName());
        roleBadge.setText(user.getRole().name());
        roleBadge.getStyleClass().setAll(getRoleBadgeStyleClass());
        onUserBound();
    }

    /**
     * Hook invoked once the authenticated user has been bound to the dashboard,
     * so subclasses can populate their own tables/widgets.
     */
    protected void onUserBound() {
        // Default: no additional behaviour.
    }

    /**
     * Returns the CSS style class for the role badge.
     *
     * @return role badge style class name
     */
    protected abstract String getRoleBadgeStyleClass();

    @FXML
    protected void handleLogout() {
        appController.handleLogout();
        try {
            SceneNavigator.show(stage, AppController.LOGIN_VIEW, controller -> {
                if (controller instanceof LoginController loginController) {
                    loginController.setAppController(appController);
                    loginController.setStage(stage);
                }
            });
            stage.setTitle(AppController.APP_TITLE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to return to the login screen.", e);
        }
    }

    /**
     * Navigates to the resource listing screen, browsable by every role.
     */
    @FXML
    protected void handleBrowseResources() {
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
            throw new IllegalStateException("Unable to open the resource list.", e);
        }
    }
}
