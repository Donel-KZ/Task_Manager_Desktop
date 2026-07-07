package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Task Manager");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        VBox content = new VBox();
        BorderPane root = new BorderPane(content);


        java.net.URL cssUrl = getClass().getResource("/org/donel/taskmanagerdesktop/styles/styles.css");
        if (cssUrl == null) {
            throw new IllegalStateException("styles.css not found on classpath");
        }
        root.getStylesheets().add(cssUrl.toExternalForm());        Label title = new Label("Login");
        BorderPane.setAlignment(title, Pos.CENTER);
        title.setId("login-label");
        root.setTop(title);

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }
}
