package org.donel.taskmanagerdesktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddTaskDialogController {

    @FXML private TextField taskNameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;

    @FXML
    public void initialize() {
        // Intentionally empty; form wiring is simple and declarative.
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
}
