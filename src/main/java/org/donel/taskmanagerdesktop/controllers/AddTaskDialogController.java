package org.donel.taskmanagerdesktop.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddTaskDialogController {

    @FXML private TextField taskNameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> priorityComboBox;

    @FXML
    public void initialize() {
        if (priorityComboBox != null) {
            priorityComboBox.getItems().setAll("LOW", "MEDIUM", "HIGH");
            priorityComboBox.setValue("MEDIUM");
        }
    }

    public TextField getTaskNameField() {
        return taskNameField;
    }

    public String getTaskName() {
        return taskNameField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public java.time.LocalDate getDueDate() {
        return dueDatePicker.getValue();
    }

    public String getPriority() {
        return priorityComboBox == null || priorityComboBox.getValue() == null
                ? "MEDIUM"
                : priorityComboBox.getValue();
    }
}
