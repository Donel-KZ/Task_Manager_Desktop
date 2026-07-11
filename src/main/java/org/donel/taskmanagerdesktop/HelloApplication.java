package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HelloApplication extends Application {



    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Task Manager");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        Scene root = new Scene(FXMLLoader.load(getClass().getResource("Dashboard.fxml")));
        stage.setScene(root);
        stage.show();
    }
    



}
