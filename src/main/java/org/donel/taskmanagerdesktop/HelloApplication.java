package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.donel.taskmanagerdesktop.Navigator.SceneNavigator;
import org.kordamp.ikonli.javafx.Icon;

import javax.swing.*;
import java.awt.*;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        SceneNavigator.init(stage);

        stage.setTitle("Task Manager");
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        Image icon = new Image(getClass().getResourceAsStream("taskmanager_ios_icon_1024.png"));
        stage.getIcons().add(icon);
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                java.awt.Image dockIcon = new ImageIcon(
                        getClass().getResource("/org/donel/taskmanagerdesktop/taskmanager_ios_icon_1024.png")
                ).getImage();
                taskbar.setIconImage(dockIcon);
            }
        }
        // SceneNavigator.switchTo("LoginView.fxml") and have the login
        // controller call SceneNavigator.switchTo("Shell.fxml") on success.
        SceneNavigator.switchTo("Shell.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}