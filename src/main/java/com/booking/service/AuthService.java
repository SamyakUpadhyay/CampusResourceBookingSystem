package com.booking.service;

import com.booking.exception.UnauthorizedAccessException;
import com.booking.fileio.LogManager;
import com.booking.fileio.UserFileHandler;
import com.booking.model.Role;
import com.booking.model.User;
import com.booking.util.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles user authentication and role-based access control.
 */
public class AuthService {

    private final UserFileHandler userFileHandler;
    private User currentUser;
    private final List<User> users;

    /** Maximum failed login attempts before an account is temporarily locked out. */
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    /** Lockout duration applied after {@link #MAX_LOGIN_ATTEMPTS} consecutive failures. */
    private static final long LOCKOUT_DURATION_MILLIS = 30_000L;

    private final Map<String, Integer> failedLoginAttempts = new HashMap<>();
    private final Map<String, Long> lockedUntilMillis = new HashMap<>();

    /**
     * Constructs an authentication service with the given file handler.
     *
     * @param userFileHandler handler for persisting and loading user data
     */
    public AuthService(UserFileHandler userFileHandler) {
        this.userFileHandler = userFileHandler;
        this.users = new ArrayList<>();
    }

    /**
     * Authenticates a user with the given credentials.
     *
     * @param username plain-text username
     * @param password plain-text password
     * @return the authenticated {@link User}
     * @throws UnauthorizedAccessException if credentials are invalid
     */
    public User login(String username, String password) throws UnauthorizedAccessException {
        loadUsers();

        if (username == null || password == null) {
            throw new UnauthorizedAccessException("Invalid username or password.");
        }

        String key = username.trim().toLowerCase();

        Long lockedUntil = lockedUntilMillis.get(key);
        if (lockedUntil != null) {
            long remainingMillis = lockedUntil - System.currentTimeMillis();
            if (remainingMillis > 0) {
                long remainingSeconds = (remainingMillis + 999) / 1000;
                throw new UnauthorizedAccessException(
                        "Too many failed attempts. Try again in " + remainingSeconds + " second(s).");
            }
            // Lockout window has elapsed; clear it and give the user a fresh set of attempts.
            lockedUntilMillis.remove(key);
            failedLoginAttempts.remove(key);
        }

        User matchedUser = null;
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                matchedUser = user;
                break;
            }
        }

        if (matchedUser == null || !PasswordEncoder.matches(password, matchedUser.getPasswordHash())) {
            LogManager.getInstance().logWarning("Failed login attempt for username: " + username);
            int attempts = failedLoginAttempts.merge(key, 1, Integer::sum);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                lockedUntilMillis.put(key, System.currentTimeMillis() + LOCKOUT_DURATION_MILLIS);
                failedLoginAttempts.remove(key);
                LogManager.getInstance().logWarning("Account locked for 30s after repeated failures: " + username);
                throw new UnauthorizedAccessException(
                        "Too many failed attempts. Account locked for 30 seconds.");
            }
            int remaining = MAX_LOGIN_ATTEMPTS - attempts;
            throw new UnauthorizedAccessException(
                    "Invalid credentials. Remaining attempts: " + remaining);
        }

        if (!matchedUser.isActive()) {
            LogManager.getInstance().logWarning("Login rejected for disabled account: " + username);
            throw new UnauthorizedAccessException("This account has been disabled. Contact an administrator.");
        }

        // Successful login clears any prior failure tracking for this username.
        failedLoginAttempts.remove(key);
        lockedUntilMillis.remove(key);

        currentUser = matchedUser;
        LogManager.getInstance().logInfo("User " + matchedUser.getUserId()
                + " (" + matchedUser.getRole() + ") logged in.");
        return matchedUser;
    }

    /**
     * Clears the current authenticated user session.
     */
    public void logout() {
        if (currentUser != null) {
            LogManager.getInstance().logInfo("User " + currentUser.getUserId() + " logged out.");
        }
        currentUser = null;
    }

    /**
     * Returns the currently authenticated user, if any.
     *
     * @return the active {@link User}, or {@code null} if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently authenticated user.
     *
     * @param currentUser the user to set as active
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Returns all registered users loaded into the service.
     *
     * @return list of system users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Checks whether the current user holds the specified role.
     *
     * @param role the role to verify
     * @return {@code true} if the current user has the given role
     */
    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    /**
     * Loads user data from persistent storage.
     */
    public void loadUsers() {
        users.clear();
        users.addAll(userFileHandler.load());
    }

    /**
     * Persists all user data to storage.
     */
    public void saveUsers() {
        try {
            userFileHandler.save(users);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save users.", e);
        }
    }
}
