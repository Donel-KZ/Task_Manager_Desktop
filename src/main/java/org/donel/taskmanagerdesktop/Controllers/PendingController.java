package org.donel.taskmanagerdesktop.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.donel.taskmanagerdesktop.Dialogs.AddTaskDialog;
import org.donel.taskmanagerdesktop.Dialogs.NewTaskInput;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PendingController {

    @FXML private Label pendingCountLabel;
    @FXML private VBox taskListContainer;
    @FXML private Button addTaskButton;

    @FXML
    public void initialize() {
        refreshTasks();
    }

    @FXML
    private void handleAddTask() {
        try {
            Optional<NewTaskInput> result = AddTaskDialog.show(addTaskButton.getScene().getWindow());
            result.ifPresent(TaskService.getInstance()::addFromInput);
            refreshTasks();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open Add Task dialog", e);
        }
    }

    private void refreshTasks() {
        List<Task> pending = TaskService.getInstance().getPendingTasks();
        pendingCountLabel.setText("Pending Tasks (" + pending.size() + ")");

        taskListContainer.getChildren().clear();
        for (Task task : pending) {
            taskListContainer.getChildren().add(TaskCardFactory.createCard(task));
        }
    }
}
