package com.booking.controller;

import com.booking.model.User;

/**
 * Outcome of a login attempt, used to drive view-layer feedback and navigation.
 */
public class LoginResult {

    private final boolean success;
    private final User user;
    private final String errorMessage;
    private final String dashboardViewPath;

    private LoginResult(boolean success, User user, String errorMessage, String dashboardViewPath) {
        this.success = success;
        this.user = user;
        this.errorMessage = errorMessage;
        this.dashboardViewPath = dashboardViewPath;
    }

    /**
     * Creates a successful login result with the authenticated user and target view.
     *
     * @param user              authenticated user
     * @param dashboardViewPath FXML path for role-based navigation
     * @return successful login result
     */
    public static LoginResult success(User user, String dashboardViewPath) {
        return new LoginResult(true, user, null, dashboardViewPath);
    }

    /**
     * Creates a failed login result with an error message.
     *
     * @param errorMessage message to display to the user
     * @return failed login result
     */
    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, null, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDashboardViewPath() {
        return dashboardViewPath;
    }
}
