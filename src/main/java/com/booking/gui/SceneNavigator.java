package com.booking.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility for loading FXML views and switching application scenes.
 */
public final class SceneNavigator {

    private SceneNavigator() {
    }

    /**
     * Loads an FXML view and replaces the content of the given stage.
     *
     * @param stage          target stage
     * @param fxmlPath       classpath location of the FXML file
     * @param controllerSetup optional callback to configure the loaded controller
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void show(Stage stage, String fxmlPath, Consumer<Object> controllerSetup)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        Parent root = loader.load();

        if (controllerSetup != null) {
            controllerSetup.accept(loader.getController());
        }

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        if (!scene.getStylesheets().contains(getStylesheetPath())) {
            scene.getStylesheets().add(getStylesheetPath());
        }
    }

    /**
     * Returns the classpath URL for the shared application stylesheet.
     *
     * @return stylesheet URL string
     */
    public static String getStylesheetPath() {
        return SceneNavigator.class.getResource("/com/booking/view/styles.css").toExternalForm();
    }
}
