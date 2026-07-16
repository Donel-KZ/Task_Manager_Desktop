package org.donel.taskmanagerdesktop.Controllers;

import java.io.IOException;
import java.util.List;

import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.Dialogs.CreateProjectDialog;
import org.donel.taskmanagerdesktop.services.NewProjectInput;
import org.donel.taskmanagerdesktop.services.ProjectService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GroupProjectsController {

    @FXML private VBox projectListContainer;
    @FXML private Label statusLabel;
    @FXML private Button newProjectButton;

    private final ProjectService projectService = new ProjectService(new ApiClient());

    @FXML
    public void initialize() {
        loadProjects();
    }

    private void loadProjects() {
        showStatus("Loading projects...");

        Task<List<ProjectResponse>> task = new Task<>() {
            @Override
            protected List<ProjectResponse> call() throws Exception {
                return projectService.getProjects();
            }
        };

        task.setOnSucceeded(e -> {
            hideStatus();
            List<ProjectResponse> projects = task.getValue();
            projectListContainer.getChildren().clear();
            if (projects.isEmpty()) {
                showStatus("No group projects yet. Click \"+ New Project\" to create one.");
            } else {
                for (ProjectResponse project : projects) {
                    projectListContainer.getChildren().add(buildProjectCard(project));
                }
            }
        });

        task.setOnFailed(e -> {
            showStatus("Could not load projects. Check your connection and try again.");
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleNewProject() throws IOException {
        CreateProjectDialog.show(newProjectButton.getScene().getWindow())
                .ifPresent(this::createProject);
    }

    private void createProject(NewProjectInput input) {

        newProjectButton.setDisable(true);
        showStatus("Creating project...");

        Task<ProjectResponse> task = new Task<>() {
            @Override
            protected ProjectResponse call() throws Exception {
                return projectService.createProject(input.toRequest());
            }
        };

        task.setOnSucceeded(event -> {
            newProjectButton.setDisable(false);
            hideStatus();
            loadProjects();
        });

        task.setOnFailed(event -> {
            newProjectButton.setDisable(false);
            showStatus("Could not create the project.");

            if (task.getException() != null) {
                task.getException().printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox buildProjectCard(ProjectResponse project) {
        VBox card = new VBox();
        card.getStyleClass().add("group-card");
        card.setSpacing(12);

        HBox topRow = new HBox();
        topRow.setSpacing(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox();
        titleBlock.setSpacing(2);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);

        Label titleLabel = new Label(project.name());
        titleLabel.getStyleClass().add("group-title");

        Label metaLabel = new Label("Due " + project.dueDate() + (project.pastDue() ? "  \u2022  Past due" : ""));
        metaLabel.getStyleClass().add("group-meta");
        if (project.pastDue()) {
            metaLabel.getStyleClass().add("status-badge-overdue");
        }

        titleBlock.getChildren().addAll(titleLabel, metaLabel);

        HBox actionRow = new HBox(8);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Button openButton = new Button("Open");
        openButton.getStyleClass().add("secondary-button");
        openButton.setOnAction(e -> GroupProjectDetailWindow.show(project, GroupProjectDetailWindow.DetailTab.DELIVERABLES));

        Button deliverablesButton = new Button("Deliverables");
        deliverablesButton.getStyleClass().add("secondary-button");
        deliverablesButton.setOnAction(e -> GroupProjectDetailWindow.show(project, GroupProjectDetailWindow.DetailTab.DELIVERABLES));

        Button membersButton = new Button("Members");
        membersButton.getStyleClass().add("secondary-button");
        membersButton.setOnAction(e -> GroupProjectDetailWindow.show(project, GroupProjectDetailWindow.DetailTab.MEMBERS));

        actionRow.getChildren().addAll(openButton, deliverablesButton, membersButton);
        topRow.getChildren().addAll(titleBlock, actionRow);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("group-progress-bar");
        progressBar.setMaxWidth(Double.MAX_VALUE);

        Label progressLabel = new Label("Loading deliverables...");
        progressLabel.getStyleClass().add("group-meta");

        card.getChildren().addAll(topRow, progressBar, progressLabel);

        loadDeliverableProgress(project.id(), progressBar, progressLabel);

        return card;
    }

    private void loadDeliverableProgress(long projectId, ProgressBar progressBar, Label progressLabel) {
        Task<List<DeliverableResponse>> task = new Task<>() {
            @Override
            protected List<DeliverableResponse> call() throws Exception {
                return projectService.getDeliverables(projectId);
            }
        };

        task.setOnSucceeded(e -> {
            List<DeliverableResponse> deliverables = task.getValue();
            if (deliverables.isEmpty()) {
                progressBar.setProgress(0);
                progressLabel.setText("No deliverables yet");
                return;
            }
            long finished = deliverables.stream()
                    .filter(d -> d.status() == WorkStatus.FINISHED)
                    .count();
            double fraction = (double) finished / deliverables.size();
            progressBar.setProgress(fraction);
            progressLabel.setText(finished + " / " + deliverables.size() + " deliverables complete");
        });

        task.setOnFailed(e -> progressLabel.setText("Could not load deliverables"));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}
