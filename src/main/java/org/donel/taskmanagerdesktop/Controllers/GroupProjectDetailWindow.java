package org.donel.taskmanagerdesktop.Controllers;

import java.util.List;

import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.Dialogs.AddMemberDialog;
import org.donel.taskmanagerdesktop.Dialogs.CreateDeliverableDialog;
import org.donel.taskmanagerdesktop.services.ProjectService;
import org.donel.taskmanagerdesktop.services.Session;
import org.donel.taskmanagerdesktop.services.UserResponse;
import org.donel.taskmanagerdesktop.services.UserService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Opens the Group Project detail as its own window - deliverables (with
 * their tasks) on one tab, members on another. Ported from the iOS app's
 * GroupProjectDetailScreen + DeliverablesTabView + MembersTabView, minus
 * the file-attachments feature (deferred - see conversation notes) and
 * minus in-shell tab navigation (this app doesn't have a nested nav
 * stack yet, so a separate window is the pragmatic equivalent for now).
 */
public final class GroupProjectDetailWindow {

    public enum DetailTab {
        DELIVERABLES,
        MEMBERS
    }

    private final ProjectResponse project;
    private final ProjectService projectService = new ProjectService(new ApiClient());
    private final UserService userService = new UserService(new ApiClient());

    private final VBox deliverablesContainer = new VBox(15);
    private final VBox membersContainer = new VBox(10);
    private final Label deliverablesStatus = new Label("Loading...");
    private final Label membersStatus = new Label("Loading...");

    private boolean isOwner = false;
    private Button addMemberButton;

    private GroupProjectDetailWindow(ProjectResponse project) {
        this.project = project;
    }

    public static void show(ProjectResponse project) {
        show(project, DetailTab.DELIVERABLES);
    }

    public static void show(ProjectResponse project, DetailTab initialTab) {
        new GroupProjectDetailWindow(project).open(initialTab);
    }

    private void open(DetailTab initialTab) {
        Stage stage = new Stage();
        stage.setTitle(project.name());
        stage.setMinWidth(600);
        stage.setMinHeight(500);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("content-area");

        VBox header = new VBox(4);
        header.setPadding(new Insets(20, 25, 10, 25));
        Label titleLabel = new Label(project.name());
        titleLabel.getStyleClass().add("welcome-title");
        Label dueLabel = new Label("Due " + project.dueDate() + (project.pastDue() ? "  \u2022  Past due" : ""));
        dueLabel.getStyleClass().add("subtitle");
        header.getChildren().addAll(titleLabel, dueLabel);
        root.setTop(header);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab deliverablesTab = new Tab("Deliverables", buildDeliverablesTabContent());
        Tab membersTab = new Tab("Members", buildMembersTabContent());
        tabPane.getTabs().addAll(deliverablesTab, membersTab);
        if (initialTab == DetailTab.MEMBERS) {
            tabPane.getSelectionModel().select(membersTab);
        } else {
            tabPane.getSelectionModel().select(deliverablesTab);
        }

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(
                GroupProjectDetailWindow.class.getResource("/org/donel/taskmanagerdesktop/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();

        loadMembers();
        loadDeliverables();
    }

    // ---------------- Deliverables tab ----------------

    private VBox buildDeliverablesTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        HBox toolbar = new HBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button addDeliverableButton = new Button("+ Add Deliverable");
        addDeliverableButton.getStyleClass().add("primary-button");
        addDeliverableButton.setOnAction(e -> handleAddDeliverable(addDeliverableButton));
        toolbar.getChildren().addAll(new Label(""), spacer, addDeliverableButton);

        content.getChildren().addAll(toolbar, deliverablesStatus, deliverablesContainer);
        return content;
    }

    private void loadDeliverables() {
        deliverablesStatus.setText("Loading deliverables...");
        deliverablesStatus.setVisible(true);

        Task<List<DeliverableResponse>> task = new Task<>() {
            @Override
            protected List<DeliverableResponse> call() throws Exception {
                return projectService.getDeliverables(project.id());
            }
        };
        task.setOnSucceeded(e -> {
            List<DeliverableResponse> deliverables = task.getValue();
            deliverablesContainer.getChildren().clear();
            if (deliverables.isEmpty()) {
                deliverablesStatus.setText("No deliverables yet. Click \"+ Add Deliverable\" to create one.");
            } else {
                deliverablesStatus.setVisible(false);
                for (DeliverableResponse d : deliverables) {
                    deliverablesContainer.getChildren().add(buildDeliverableCard(d));
                }
            }
        });
        task.setOnFailed(e -> {
            deliverablesStatus.setText("Could not load deliverables.");
            task.getException().printStackTrace();
        });
        runInBackground(task);
    }

    private VBox buildDeliverableCard(DeliverableResponse deliverable) {
        VBox card = new VBox(8);
        card.getStyleClass().add("group-card");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox(2);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);
        Label titleLabel = new Label(deliverable.title());
        titleLabel.getStyleClass().add("group-title");
        Label metaLabel = new Label("Due " + deliverable.dueDate() + (deliverable.pastDue() ? "  \u2022  Past due" : ""));
        metaLabel.getStyleClass().add("group-meta");
        titleBlock.getChildren().addAll(titleLabel, metaLabel);

        CheckBox finishedCheck = new CheckBox("Finished");
        finishedCheck.setSelected(deliverable.status() == WorkStatus.FINISHED);
        finishedCheck.setOnAction(e -> toggleDeliverable(deliverable, finishedCheck));

        topRow.getChildren().addAll(titleBlock, finishedCheck);

        VBox tasksContainer = new VBox(6);
        Label tasksStatus = new Label("Loading tasks...");
        tasksStatus.getStyleClass().add("group-meta");

        HBox taskToolbar = new HBox();
        taskToolbar.setAlignment(Pos.CENTER_LEFT);
        Label tasksHeader = new Label("Tasks");
        tasksHeader.getStyleClass().add("group-meta");
        Region taskSpacer = new Region();
        HBox.setHgrow(taskSpacer, Priority.ALWAYS);
        Button addTaskButton = new Button("+ Add Task");
        addTaskButton.getStyleClass().add("secondary-button");
        addTaskButton.setOnAction(e -> handleAddDeliverableTask(deliverable, addTaskButton, tasksContainer, tasksStatus));
        taskToolbar.getChildren().addAll(tasksHeader, taskSpacer, addTaskButton);

        card.getChildren().addAll(topRow, taskToolbar, tasksStatus, tasksContainer);

        loadDeliverableTasks(deliverable.id(), tasksContainer, tasksStatus);

        return card;
    }

    private void loadDeliverableTasks(long deliverableId, VBox tasksContainer, Label tasksStatus) {
        Task<List<TaskItemResponse>> task = new Task<>() {
            @Override
            protected List<TaskItemResponse> call() throws Exception {
                return projectService.getTasks(deliverableId);
            }
        };
        task.setOnSucceeded(e -> {
            List<TaskItemResponse> tasks = task.getValue();
            tasksContainer.getChildren().clear();
            if (tasks.isEmpty()) {
                tasksStatus.setText("No tasks yet.");
                tasksStatus.setVisible(true);
            } else {
                tasksStatus.setVisible(false);
                for (TaskItemResponse t : tasks) {
                    tasksContainer.getChildren().add(buildTaskRow(t, tasksContainer, tasksStatus));
                }
            }
        });
        task.setOnFailed(e -> tasksStatus.setText("Could not load tasks."));
        runInBackground(task);
    }

    private HBox buildTaskRow(TaskItemResponse task, VBox tasksContainer, Label tasksStatus) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox check = new CheckBox(task.title());
        check.setSelected(task.status() == WorkStatus.FINISHED);
        check.setOnAction(e -> toggleTask(task, check, tasksContainer, tasksStatus));

        if (task.pastDue()) {
            Label overdueBadge = new Label("Overdue");
            overdueBadge.getStyleClass().addAll("status-badge", "status-badge-overdue");
            row.getChildren().addAll(check, overdueBadge);
        } else {
            row.getChildren().add(check);
        }
        return row;
    }

    private void toggleDeliverable(DeliverableResponse deliverable, CheckBox checkBox) {
        boolean wantsFinished = checkBox.isSelected();
        Task<DeliverableResponse> task = new Task<>() {
            @Override
            protected DeliverableResponse call() throws Exception {
                if (wantsFinished) {
                    return projectService.finishDeliverable(deliverable.id());
                } else {
                    return projectService.updateDeliverable(deliverable.id(), new UpdateDeliverableRequest(
                            deliverable.title(), deliverable.description(), WorkStatus.PENDING, deliverable.dueDate()
                    ));
                }
            }
        };
        task.setOnSucceeded(e -> loadDeliverables());
        task.setOnFailed(e -> {
            checkBox.setSelected(!wantsFinished); // revert on failure
            task.getException().printStackTrace();
        });
        runInBackground(task);
    }

    private void toggleTask(TaskItemResponse taskItem, CheckBox checkBox, VBox tasksContainer, Label tasksStatus) {
        boolean wantsFinished = checkBox.isSelected();
        Task<TaskItemResponse> task = new Task<>() {
            @Override
            protected TaskItemResponse call() throws Exception {
                if (wantsFinished) {
                    return projectService.finishTask(taskItem.id());
                } else {
                    return projectService.updateTask(taskItem.id(), new UpdateTaskItemRequest(
                            taskItem.title(), WorkStatus.PENDING, taskItem.dueDate()
                    ));
                }
            }
        };
        task.setOnFailed(e -> {
            checkBox.setSelected(!wantsFinished);
            task.getException().printStackTrace();
        });
        runInBackground(task);
    }

    private void handleAddDeliverable(Button sourceButton) {
        try {
            CreateDeliverableDialog.show(sourceButton.getScene().getWindow())
                    .ifPresent(input -> {
                        Task<DeliverableResponse> task = new Task<>() {
                            @Override
                            protected DeliverableResponse call() throws Exception {
                                return projectService.createDeliverable(project.id(), input.toRequest());
                            }
                        };
                        task.setOnSucceeded(e -> loadDeliverables());
                        task.setOnFailed(e -> task.getException().printStackTrace());
                        runInBackground(task);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddDeliverableTask(DeliverableResponse deliverable, Button sourceButton,
                                           VBox tasksContainer, Label tasksStatus) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Add Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Task title:");
        dialog.getDialogPane().getStylesheets().add(
                GroupProjectDetailWindow.class.getResource("/org/donel/taskmanagerdesktop/styles.css").toExternalForm()
        );
        dialog.showAndWait().ifPresent(title -> {
            if (title.isBlank()) return;
            Task<TaskItemResponse> task = new Task<>() {
                @Override
                protected TaskItemResponse call() throws Exception {
                    return projectService.createTask(deliverable.id(),
                            new CreateTaskItemRequest(title.trim(), null));
                }
            };
            task.setOnSucceeded(e -> loadDeliverableTasks(deliverable.id(), tasksContainer, tasksStatus));
            task.setOnFailed(e -> task.getException().printStackTrace());
            runInBackground(task);
        });
    }

    // ---------------- Members tab ----------------

    private VBox buildMembersTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        HBox toolbar = new HBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addMemberButton = new Button("+ Add Member");
        addMemberButton.getStyleClass().add("primary-button");
        addMemberButton.setVisible(false); // shown only if current user turns out to be the owner
        addMemberButton.setManaged(false);
        addMemberButton.setOnAction(e -> handleAddMember());
        toolbar.getChildren().addAll(new Label(""), spacer, addMemberButton);

        content.getChildren().addAll(toolbar, membersStatus, membersContainer);
        return content;
    }

    private void loadMembers() {
        membersStatus.setText("Loading members...");
        membersStatus.setVisible(true);

        Task<List<ProjectMemberResponse>> task = new Task<>() {
            @Override
            protected List<ProjectMemberResponse> call() throws Exception {
                return projectService.getMembers(project.id());
            }
        };
        task.setOnSucceeded(e -> {
            List<ProjectMemberResponse> members = task.getValue();
            membersContainer.getChildren().clear();

            UserResponse currentUser = Session.getInstance().getCurrentUser();
            isOwner = members.stream().anyMatch(m ->
                    m.role() == ProjectRole.OWNER && currentUser != null && m.user().id() == currentUser.id());
            addMemberButton.setVisible(isOwner);
            addMemberButton.setManaged(isOwner);

            if (members.isEmpty()) {
                membersStatus.setText("No members yet.");
            } else {
                membersStatus.setVisible(false);
                for (ProjectMemberResponse m : members) {
                    membersContainer.getChildren().add(buildMemberRow(m));
                }
            }
        });
        task.setOnFailed(e -> {
            membersStatus.setText("Could not load members.");
            task.getException().printStackTrace();
        });
        runInBackground(task);
    }

    private HBox buildMemberRow(ProjectMemberResponse member) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("settings-row");

        VBox nameBlock = new VBox(2);
        HBox.setHgrow(nameBlock, Priority.ALWAYS);
        Label nameLabel = new Label(member.user().displayName());
        nameLabel.getStyleClass().add("settings-label");
        Label emailLabel = new Label(member.user().email());
        emailLabel.getStyleClass().add("settings-sublabel");
        nameBlock.getChildren().addAll(nameLabel, emailLabel);

        Label roleBadge = new Label(member.role().name());
        roleBadge.getStyleClass().addAll("status-badge", "status-badge-medium");

        row.getChildren().addAll(nameBlock, roleBadge);

        if (isOwner && member.role() != ProjectRole.OWNER) {
            Button removeButton = new Button("Remove");
            removeButton.getStyleClass().add("danger-button");
            // NOTE: backend API doc (see conversation) has no DELETE members endpoint yet -
            // this button is wired up but will need that endpoint added server-side first.
            removeButton.setOnAction(e ->
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                            "Removing members isn't supported by the backend yet.").showAndWait());
            row.getChildren().add(removeButton);
        }

        return row;
    }

    private void handleAddMember() {
        try {
            AddMemberDialog.show(addMemberButton.getScene().getWindow())
                    .ifPresent(this::addMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMember(String emailOrName) {
        Task<ProjectMemberResponse> task = new Task<>() {
            @Override
            protected ProjectMemberResponse call() throws Exception {
                List<UserResponse> users = userService.getUsers();
                String needle = emailOrName.trim().toLowerCase();
                UserResponse match = users.stream()
                        .filter(u -> u.email().toLowerCase().equals(needle) || u.displayName().toLowerCase().equals(needle))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No user found matching \"" + emailOrName + "\""));
                return projectService.addMember(project.id(), new AddMemberRequest(match.id(), ProjectRole.MEMBER));
            }
        };
        task.setOnSucceeded(e -> loadMembers());
        task.setOnFailed(e -> {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    task.getException().getMessage()).showAndWait();
        });
        runInBackground(task);
    }

    private void runInBackground(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
