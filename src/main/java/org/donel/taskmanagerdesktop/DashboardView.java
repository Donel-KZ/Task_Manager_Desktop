package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardView extends Application {
    /**
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Dashboard");
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Dashboard.fxml")));
        stage.setScene(scene);
        stage.show();

    }
}
