package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import java.util.List;

/**
 * Controls the persistent app shell: sidebar, top bar, and contentArea.
 * This is loaded ONCE by SceneNavigator (after a successful login) and stays
 * mounted for the rest of the session. It is NOT reloaded per-page.
 *
 * Renamed from HomeController: this class was never really about the "Home"
 * page specifically - it owns the whole shell and swaps FRAGMENTS in and out
 * of contentArea. "Home" is just one of those fragments (Dashboard.fxml).
 *
 * IMPORTANT: every FXML loaded via loadContent() must be a plain fragment
 * (e.g. a root ScrollPane or VBox) - NOT a full BorderPane with its own
 * <left>/<top>. Wrapping a fragment in another copy of the shell layout is
 * what caused the duplicate-sidebar bug. See Dashboard.fxml for the correct
 * shape a fragment should have.
 */
public class ShellController {

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
        showHome();
    }

    @FXML
    private void showHome() {
        loadContent("Dashboard.fxml", homeButton);
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
            java.net.URL url = getClass().getResource(CONTENT_BASE + fxml);

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
