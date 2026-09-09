package com.booking.gui;

/**
 * Launcher class for the modular JavaFX runtime.
 * Required when using Java modules with JavaFX.
 */
public class Launcher {

    /**
     * Private constructor to prevent instantiation.
     */
    private Launcher() {
        // Launcher utility — no instances permitted.
    }

    /**
     * Delegates to the JavaFX application launcher.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        CRSBSApplication.main(args);
    }
}
