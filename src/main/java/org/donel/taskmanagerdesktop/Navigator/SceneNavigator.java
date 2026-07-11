package org.donel.taskmanagerdesktop.Navigator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles full SCENE-level transitions (e.g. Login screen -> main app Shell).
 *
 * This is deliberately separate from ShellController.loadContent(), which only
 * swaps FRAGMENTS inside the shell's contentArea (Home/Pending/Calendar/etc).
 * SceneNavigator replaces the entire scene root - use it only when moving
 * between top-level screens that are NOT nested inside the shell
 * (Login -> Shell, Shell -> Login on logout, Signup -> Login, etc).
 *
 * Usage:
 *   In HelloApplication.start(): SceneNavigator.init(stage);
 *   Anywhere after that:         SceneNavigator.switchTo("LoginView.fxml");
 */
public final class SceneNavigator {

    // All top-level FXML files live directly under this resources package.
    private static final String BASE = "/org/donel/taskmanagerdesktop/";

    private static Stage primaryStage;

    private SceneNavigator() {
        // static utility, no instances
    }

    /**
     * Must be called once, in HelloApplication.start(), before any switchTo() call.
     */
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Loads the given FXML (relative to the resources root package) and makes it
     * the new scene root. Reuses the existing Scene object if one already exists,
     * so the window doesn't flicker or reset size/position between screens.
     *
     * @return the controller instance associated with the loaded FXML, so callers
     *         can immediately wire data into it (e.g. passing a logged-in user).
     */
    public static <T> T switchTo(String fxml) {
        requireInit();
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(BASE + fxml));
            Parent root = loader.load();

            Scene currentScene = primaryStage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root, 1280, 720);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

            return loader.getController();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load scene: " + BASE + fxml, e);
        }
    }

    /**
     * Same as switchTo(String), but resizes the window to the given dimensions.
     * Handy if Login/Signup screens are a different size than the main Shell.
     */
    public static <T> T switchTo(String fxml, double width, double height) {
        T controller = switchTo(fxml);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        return controller;
    }

    public static Stage getPrimaryStage() {
        requireInit();
        return primaryStage;
    }

    private static void requireInit() {
        if (primaryStage == null) {
            throw new IllegalStateException(
                    "SceneNavigator.init(stage) must be called before switchTo(). " +
                    "Call it once from HelloApplication.start()."
            );
        }
    }
}
