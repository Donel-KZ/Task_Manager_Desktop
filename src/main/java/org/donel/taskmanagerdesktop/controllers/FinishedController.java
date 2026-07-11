package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.donel.taskmanagerdesktop.controllers.Task;
import org.donel.taskmanagerdesktop.controllers.TaskService;
import org.donel.taskmanagerdesktop.controllers.TaskCardFactory;

import java.util.List;

public class FinishedController {

    @FXML private Label finishedCountLabel;
    @FXML private VBox taskListContainer;

    @FXML
    public void initialize() {
        List<Task> finished = TaskService.getInstance().getFinishedTasks();

        finishedCountLabel.setText("Finished Tasks (" + finished.size() + ")");

        taskListContainer.getChildren().clear();
        for (Task task : finished) {
            taskListContainer.getChildren().add(TaskCardFactory.createCard(task));
        }
    }
}
