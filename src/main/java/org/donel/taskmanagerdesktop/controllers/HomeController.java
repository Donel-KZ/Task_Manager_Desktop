package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class HomeController {

    // Content fragments live in org.donel.taskmanagerdesktop (resources root package),
    // but this controller lives one package deeper, in .controllers.
    // getClass().getResource() resolves relative to the CLASS's package by default,
    // so a plain "HomeContent.fxml" would look inside
    // src/main/resources/org/donel/taskmanagerdesktop/controllers/ and find nothing.
    // The leading "/" makes it resolve from the resources root instead.
    private static final String CONTENT_BASE = "/org/donel/taskmanagerdesktop/";

    @FXML private ScrollPane contentArea;

    @FXML private Button homeButton;
    @FXML private Button pending;
    @FXML private Button finished;
    @FXML private Button overdue;
    @FXML private Button calendar;
    @FXML private Button groupProjects;
    @FXML private Button settings;

    @FXML
    public void initialize() {
        setActiveButton(homeButton);
    }

    @FXML
    private void showHome() {
        loadContent("Home.fxml", homeButton);
    }

    @FXML
    private void showPending() {
        loadContent("PendingView.fxml", pending);
    }

    @FXML
    private void showFinished() {
        loadContent("FinishedView.fxml", finished);
    }

    @FXML
    private void showOverdue() {
        loadContent("OverdueView.fxml", overdue);
    }

    @FXML
    private void showCalendar() {
        loadContent("CalendarView.fxml", calendar);
    }

    @FXML
    private void showGroupProjects() {
        loadContent("GroupProjectsView.fxml", groupProjects);
    }

    @FXML
    private void showSettings() {
        loadContent("SettingsView.fxml", settings);
    }

    private void loadContent(String fxml, Button activeButton) {
        try {

            java.net.URL url = getClass().getResource("/org/donel/taskmanagerdesktop/" + fxml);

            System.out.println("Loading: " + url);

            FXMLLoader loader = new FXMLLoader(url);

            Parent root = loader.load();

            contentArea.setContent(root);

            setActiveButton(activeButton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button active) {
        List<Button> navButtons = List.of(
                homeButton, pending, finished, overdue, calendar, groupProjects, settings
        );
        for (Button b : navButtons) {
            b.getStyleClass().remove("nav-button-active");
            if (!b.getStyleClass().contains("nav-button")) {
                b.getStyleClass().add("nav-button");
            }
        }
        active.getStyleClass().remove("nav-button");
        active.getStyleClass().add("nav-button-active");
    }
}
