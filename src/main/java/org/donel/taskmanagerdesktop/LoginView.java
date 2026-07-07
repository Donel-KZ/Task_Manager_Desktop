package org.donel.taskmanagerdesktop;

import javafx.application.Application;
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
        VBox content = new VBox();
        BorderPane root = new BorderPane(content);
        root.getStyleClass().add("custom-ui-panel");

        java.net.URL cssUrl = getClass().getResource("/org/donel/taskmanagerdesktop/styles.css");
        if (cssUrl == null) {
            throw new IllegalStateException("styles.css not found on classpath");
        }
        root.getStylesheets().add(cssUrl.toExternalForm());

        Label title = new Label("Login");
        title.setId("login-label");
        title.getStyleClass().add("login-label");
        title.setText("Login");


        if (stage.getHeight() == Screen.getPrimary().getBounds().getHeight() & stage.getWidth() == Screen.getPrimary().getBounds().getWidth()) {
            VBox topBox = new VBox(title);
            topBox.setAlignment(Pos.CENTER_RIGHT);
            topBox.setSpacing(40);
            //root.setTop(topBox);
            root.setCenter(topBox);

            Scene scene = new Scene(root, 800, 500);
            stage.setScene(scene);

        }
        else {
            VBox topBox = new VBox(title);
            topBox.setAlignment(Pos.CENTER);
            topBox.setSpacing(10);
            root.setTop(topBox);

            Scene scene = new Scene(root, 800, 500);
            stage.setScene(scene);
        }








        stage.show();
    }
}
