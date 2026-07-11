package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.donel.taskmanagerdesktop.controllers.Task;
import org.donel.taskmanagerdesktop.controllers.TaskService;
import org.donel.taskmanagerdesktop.controllers.TaskCardFactory;

public class DashboardController {

    @FXML private Label totalTasksValue;
    @FXML private Label pendingValue;
    @FXML private Label finishedValue;
    @FXML private Label overdueValue;

    @FXML private VBox todayTasksContainer;

    @FXML
    public void initialize() {
        TaskService service = TaskService.getInstance();

        totalTasksValue.setText(String.valueOf(service.getTotalCount()));
        pendingValue.setText(String.valueOf(service.getPendingCount()));
        finishedValue.setText(String.valueOf(service.getFinishedCount()));
        overdueValue.setText(String.valueOf(service.getOverdueCount()));

        todayTasksContainer.getChildren().clear();
        for (Task task : service.getTodayTasks()) {
            todayTasksContainer.getChildren().add(TaskCardFactory.createCard(task));
        }
    }
}
