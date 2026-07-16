package org.donel.taskmanagerdesktop.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.donel.taskmanagerdesktop.Dialogs.AddTaskDialog;
import org.donel.taskmanagerdesktop.Dialogs.NewTaskInput;

import java.io.IOException;
import java.util.Optional;

public class DashboardController {

    @FXML private Label totalTasksValue;
    @FXML private Label pendingValue;
    @FXML private Label finishedValue;
    @FXML private Label overdueValue;

    @FXML private VBox todayTasksContainer;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    @FXML
    private void addTask() {
        try {
            Optional<NewTaskInput> result = AddTaskDialog.show(todayTasksContainer.getScene().getWindow());
            result.ifPresent(TaskService.getInstance()::addFromInput);
            refreshDashboard();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open Add Task dialog", e);
        }
    }

    private void refreshDashboard() {
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
