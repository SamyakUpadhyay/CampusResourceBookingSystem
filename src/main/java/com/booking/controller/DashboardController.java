package com.booking.controller;

import com.booking.model.User;
import javafx.stage.Stage;

/**
 * Contract for role-specific dashboard view controllers.
 */
public interface DashboardController {

    /**
     * Injects the shared application controller.
     *
     * @param appController application controller
     */
    void setAppController(AppController appController);

    /**
     * Injects the primary stage for navigation.
     *
     * @param stage primary stage
     */
    void setStage(Stage stage);

    /**
     * Binds the authenticated user to the dashboard view.
     *
     * @param user authenticated user
     */
    void bindUser(User user);
}
