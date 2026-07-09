package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Task Manager");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("LoginView.fxml")));
        stage.setScene(scene);








        stage.show();
    }
}
