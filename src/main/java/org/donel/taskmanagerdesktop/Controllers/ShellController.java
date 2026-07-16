package org.donel.taskmanagerdesktop.Controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.services.ProjectService;
import org.donel.taskmanagerdesktop.services.Session;
import org.donel.taskmanagerdesktop.services.UserResponse;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    @FXML private Button notificationButton;
    @FXML private Label notificationCountLabel;
    @FXML private Button profileButton;

    private final ProjectService projectService = new ProjectService(new ApiClient());

    @FXML
    public void initialize() {
        showHome();
        refreshTopBar();
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

    private void refreshTopBar() {
        UserResponse currentUser = Session.getInstance().getCurrentUser();

        String displayName = currentUser == null || currentUser.displayName() == null || currentUser.displayName().isBlank()
                ? "Guest"
                : currentUser.displayName();

        profileButton.setText(displayName);
        profileButton.setGraphic(buildAvatarGraphic(currentUser));

        int dueSoonTasks = (int) TaskService.getInstance().getAllTasks().stream()
                .filter(task -> task.getDueDate() != null && !task.getStatus().equals(TaskStatus.FINISHED))
                .filter(task -> ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate()) >= 0)
                .filter(task -> ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate()) <= 3)
                .count();

        notificationButton.setText(dueSoonTasks > 0 ? "🔔" : "🔔");
        notificationCountLabel.setText(String.valueOf(dueSoonTasks));
        notificationCountLabel.setVisible(dueSoonTasks > 0);
        notificationCountLabel.setManaged(dueSoonTasks > 0);

        Task<List<ProjectResponse>> projectTask = new Task<>() {
            @Override
            protected List<ProjectResponse> call() throws Exception {
                return projectService.getProjects();
            }
        };
        projectTask.setOnSucceeded(event -> {
            int dueSoonProjects = (int) projectTask.getValue().stream()
                    .filter(project -> project.dueDate() != null)
                    .filter(project -> !project.pastDue())
                    .filter(project -> {
                        try {
                            LocalDate dueDate = LocalDate.parse(project.dueDate());
                            return !dueDate.isBefore(LocalDate.now()) && ChronoUnit.DAYS.between(LocalDate.now(), dueDate) <= 3;
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .count();
            int totalNotifications = dueSoonTasks + dueSoonProjects;
            notificationCountLabel.setText(String.valueOf(totalNotifications));
            notificationCountLabel.setVisible(totalNotifications > 0);
            notificationCountLabel.setManaged(totalNotifications > 0);
        });
        projectTask.setOnFailed(event -> {
            notificationCountLabel.setText(String.valueOf(dueSoonTasks));
            notificationCountLabel.setVisible(dueSoonTasks > 0);
            notificationCountLabel.setManaged(dueSoonTasks > 0);
        });

        Thread thread = new Thread(projectTask);
        thread.setDaemon(true);
        thread.start();
    }

    private ImageView buildAvatarGraphic(UserResponse currentUser) {
        ImageView avatar = new ImageView();
        avatar.setFitWidth(24);
        avatar.setFitHeight(24);
        avatar.setPreserveRatio(true);

        if (currentUser != null && currentUser.profilePictureUrl() != null && !currentUser.profilePictureUrl().isBlank()) {
            try {
                avatar.setImage(new Image(currentUser.profilePictureUrl(), true));
                return avatar;
            } catch (Exception ignored) {
                // fall through to default icon
            }
        }

        Image fallback = new Image(getClass().getResourceAsStream("/org/donel/taskmanagerdesktop/taskmanager_ios_icon_1024.png"));
        avatar.setImage(fallback);
        return avatar;
    }
}
