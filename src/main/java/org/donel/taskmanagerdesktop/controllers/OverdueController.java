package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.donel.taskmanagerdesktop.controllers.Task;
import org.donel.taskmanagerdesktop.controllers.TaskService;
import org.donel.taskmanagerdesktop.controllers.TaskCardFactory;

import java.util.List;

public class OverdueController {

    @FXML private Label overdueCountLabel;
    @FXML private VBox taskListContainer;

    @FXML
    public void initialize() {
        List<Task> overdue = TaskService.getInstance().getOverdueTasks();

        overdueCountLabel.setText("Overdue Tasks (" + overdue.size() + ")");

        taskListContainer.getChildren().clear();
        for (Task task : overdue) {
            taskListContainer.getChildren().add(TaskCardFactory.createCard(task));
        }
    }
}
