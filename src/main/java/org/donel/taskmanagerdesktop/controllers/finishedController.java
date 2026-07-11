package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class finishedController {
    @FXML
    private StackPane contentArea;
    @FXML private Button homeButton;

    @FXML private Button pending;
    @FXML private Button finished;
    @FXML private Button overdue;
    @FXML private Button groupProjects;
    @FXML private Button settings;

    @FXML
    public void initialize() throws IOException {
        showfinished();
    }

    private void showfinished() throws IOException {
        loadScreen("FinishedView.fxml", finished);
    }

    @FXML
    private void showPending() throws IOException {
        loadScreen("PendindView.fxml", pending);
    }



    @FXML
    private void overdue() throws IOException {
        loadScreen("OverdueView.fxml", overdue);
    }

    @FXML
    private void groupProjects() throws IOException {
        loadScreen("GroupProjectView.fxml", groupProjects);
    }

    @FXML
    private void settings() throws IOException {
        loadScreen("SettingsView.fxml", settings);
    }

    @FXML
    private void showCalender() throws IOException {
        loadScreen("CalenderView.fxml", finished);
    }


    @FXML
    private void loadScreen(String s, Button button) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource(s));
        contentArea.getChildren().add(loader);
        setActiveButton(button);
    }

    private void setActiveButton(Button activeButton) {
        List<Button> navbuttons = List.of(homeButton, pending, finished, overdue, groupProjects, settings);

        for (Button button1 : navbuttons) {
            button1.getStyleClass().remove("nav-button-active");
            if (!button1.getStyleClass().contains("nav-button")) {
                button1.getStyleClass().add("nav-button");
            }
        }
        activeButton.getStyleClass().remove("nav-button");
        activeButton.getStyleClass().add("nav-button-active");


    }

}
