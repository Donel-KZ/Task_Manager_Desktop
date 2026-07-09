package org.donel.taskmanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignUpView extends Application {
    /**
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sign Up");
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("SignUpView.fxml")));
        stage.setScene(scene);
        stage.show();

    }
}
