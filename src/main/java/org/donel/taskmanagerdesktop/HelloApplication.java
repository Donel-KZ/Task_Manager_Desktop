package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.stage.Stage;
import org.donel.taskmanagerdesktop.Navigator.SceneNavigator;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        SceneNavigator.init(stage);

        stage.setTitle("Task Manager");
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        // SceneNavigator.switchTo("LoginView.fxml") and have the login
        // controller call SceneNavigator.switchTo("Shell.fxml") on success.
        SceneNavigator.switchTo("Shell.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}