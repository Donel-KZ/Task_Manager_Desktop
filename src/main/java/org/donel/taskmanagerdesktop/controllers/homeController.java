package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class homeController {
    @FXML
    private StackPane contentArea;
    @FXML private Button homeButton;

    @FXML private Button pending;
    @FXML private Button finished;
    @FXML private Button overdue;
    @FXML private Button groupProjects;
    @FXML private Button settings;
    @FXML private Button calendar;


    @FXML
    public void initialize() {
        showHome();
    }

    @FXML
    private void showHome() {
        loadScene("Dashboard.fxml", homeButton);
    }

    @FXML
    private void showPending() {
        loadScene("PendingView.fxml", pending);
    }

    @FXML
    private void showFinished() {
        loadScene("FinishedView.fxml", finished);
    }
    @FXML
    private void showOverdue() {
        loadScene("OverdueView.fxml", overdue);
    }

    @FXML
    private void showGroupProjects() {
        loadScene("GroupProjectView.fxml", groupProjects);

    }

    @FXML
    private void showCalender() throws IOException {
        loadScene("CalenderView.fxml", calendar);
    }



    private void loadScene(String s, Button button) {
        try {
            Parent loader =  FXMLLoader.load(getClass().getResource(s));
            contentArea.getChildren().setAll(loader);
            setActiveButton(button);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
