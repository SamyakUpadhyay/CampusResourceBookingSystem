package com.booking.controller;

import com.booking.exception.UnauthorizedAccessException;
import com.booking.fileio.ResourceFileHandler;
import com.booking.model.Resource;
import com.booking.model.Role;
import com.booking.model.User;
import com.booking.service.AuthService;
import com.booking.service.BookingService;
import com.booking.service.ResourceManager;
import com.booking.util.SampleDataFactory;

import java.io.IOException;

/**
 * Primary application controller coordinating between the GUI and service layers.
 */
public class AppController {

    public static final String APP_TITLE = "CRSBS - Campus Resource Booking";
    public static final String LOGIN_VIEW = "/com/booking/view/login-view.fxml";
    public static final String STUDENT_DASHBOARD_VIEW = "/com/booking/view/student-dashboard.fxml";
    public static final String STAFF_DASHBOARD_VIEW = "/com/booking/view/staff-dashboard.fxml";
    public static final String ADMIN_DASHBOARD_VIEW = "/com/booking/view/admin-dashboard.fxml";
    public static final String RESOURCE_LIST_VIEW = "/com/booking/view/resource-list.fxml";
    public static final String BOOKING_VIEW = "/com/booking/view/booking-view.fxml";

    private final AuthService authService;
    private final BookingService bookingService;
    private final ResourceFileHandler resourceFileHandler;

    /**
     * Constructs an application controller with the required services.
     *
     * @param authService         service for authentication and access control
     * @param bookingService      service for booking management
     * @param resourceFileHandler handler for persisting the resource catalogue
     */
    public AppController(AuthService authService, BookingService bookingService,
                          ResourceFileHandler resourceFileHandler) {
        this.authService = authService;
        this.bookingService = bookingService;
        this.resourceFileHandler = resourceFileHandler;
    }

    /**
     * Initialises the controller and loads persisted application data, seeding
     * sample users/resources on first run so the application is usable immediately.
     */
    public void initialize() {
        authService.loadUsers();
        bookingService.loadBookings();
        loadResources();
        seedSampleUsersIfNeeded();
        seedSampleResourcesIfNeeded();
    }

    /**
     * Handles user login requests from the view layer.
     *
     * @param username plain-text username
     * @param password plain-text password
     * @return login outcome including navigation target on success
     */
    public LoginResult handleLogin(String username, String password) {
        try {
            User user = authService.login(username, password);
            return LoginResult.success(user, getDashboardViewPath(user.getRole()));
        } catch (UnauthorizedAccessException e) {
            return LoginResult.failure(e.getMessage());
        }
    }

    /**
     * Handles user logout requests from the view layer.
     */
    public void handleLogout() {
        authService.logout();
    }

    /**
     * Returns the dashboard window title for the authenticated user.
     *
     * @param user authenticated user
     * @return dashboard title text
     */
    public String getDashboardTitle(User user) {
        return switch (user.getRole()) {
            case STUDENT -> "CRSBS - Student Dashboard";
            case STAFF -> "CRSBS - Staff Dashboard";
            case ADMIN -> "CRSBS - Admin Dashboard";
        };
    }

    /**
     * Resolves the FXML path for a role-specific dashboard.
     *
     * @param role authenticated user role
     * @return dashboard FXML classpath location
     */
    public String getDashboardViewPath(Role role) {
        return switch (role) {
            case STUDENT -> STUDENT_DASHBOARD_VIEW;
            case STAFF -> STAFF_DASHBOARD_VIEW;
            case ADMIN -> ADMIN_DASHBOARD_VIEW;
        };
    }

    public AuthService getAuthService() {
        return authService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    /**
     * Returns the shared, in-memory resource catalogue.
     *
     * @return the {@link ResourceManager} backing this application session
     */
    public ResourceManager<Resource> getResourceManager() {
        return bookingService.getResourceManager();
    }

    /**
     * Persists the current resource catalogue to disk.
     */
    public void saveResources() {
        try {
            resourceFileHandler.save(getResourceManager().getAllResources());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save resources.", e);
        }
    }

    /**
     * Persists the current user directory to disk.
     */
    public void saveUsers() {
        authService.saveUsers();
    }

    private void loadResources() {
        ResourceManager<Resource> resourceManager = getResourceManager();
        for (Resource resource : resourceFileHandler.load()) {
            if (resourceManager.findById(resource.getResourceId()) == null) {
                resourceManager.addResource(resource);
            }
        }
    }

    private void seedSampleUsersIfNeeded() {
        if (!authService.getUsers().isEmpty()) {
            return;
        }

        authService.getUsers().addAll(SampleDataFactory.createSampleUsers());

        try {
            authService.saveUsers();
        } catch (RuntimeException ignored) {
            // Sample seeding is best-effort for first launch.
        }
    }

    private void seedSampleResourcesIfNeeded() {
        ResourceManager<Resource> resourceManager = getResourceManager();
        if (resourceManager.getResourceCount() > 0) {
            return;
        }

        for (Resource resource : SampleDataFactory.createSampleResources()) {
            resourceManager.addResource(resource);
        }

        try {
            saveResources();
        } catch (RuntimeException ignored) {
            // Sample seeding is best-effort for first launch.
        }
    }
}
