package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.donel.taskmanagerdesktop.controllers.Task;
import org.donel.taskmanagerdesktop.controllers.TaskService;
import org.donel.taskmanagerdesktop.controllers.TaskCardFactory;


import java.util.List;

public class PendingController {

    @FXML private Label pendingCountLabel;
    @FXML private VBox taskListContainer;

    @FXML
    public void initialize() {
        List<Task> pending = TaskService.getInstance().getPendingTasks();

        pendingCountLabel.setText("Pending Tasks (" + pending.size() + ")");

        taskListContainer.getChildren().clear();
        for (Task task : pending) {
            taskListContainer.getChildren().add(TaskCardFactory.createCard(task));
        }
    }
}
