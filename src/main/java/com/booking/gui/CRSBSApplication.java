package com.booking.gui;

import com.booking.controller.AppController;
import com.booking.controller.LoginController;
import com.booking.fileio.BookingFileHandler;
import com.booking.fileio.ResourceFileHandler;
import com.booking.fileio.UserFileHandler;
import com.booking.model.Resource;
import com.booking.service.AuthService;
import com.booking.service.BookingService;
import com.booking.service.ResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX application entry point for the Campus Resource &amp; Study Space Booking System.
 */
public class CRSBSApplication extends Application {

    /**
     * Initialises and displays the primary application window.
     *
     * @param primaryStage the main stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        AuthService authService = new AuthService(new UserFileHandler());
        ResourceManager<Resource> resourceManager = new ResourceManager<>();
        BookingService bookingService = new BookingService(
                new BookingFileHandler(),
                authService,
                resourceManager
        );

        AppController appController = new AppController(
                authService, bookingService, new ResourceFileHandler());
        appController.initialize();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(AppController.LOGIN_VIEW));
        Scene scene = new Scene(loader.load(), 960, 640);
        scene.getStylesheets().add(SceneNavigator.getStylesheetPath());

        LoginController loginController = loader.getController();
        loginController.setAppController(appController);
        loginController.setStage(primaryStage);

        primaryStage.setTitle(AppController.APP_TITLE);
        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(560);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
