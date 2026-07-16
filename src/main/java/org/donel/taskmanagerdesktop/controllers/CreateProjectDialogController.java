package org.donel.taskmanagerdesktop.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class CreateProjectDialogController {

    @FXML private TextField titleField;
    @FXML private DatePicker dueDatePicker;

    @FXML
    public void initialize() {
        dueDatePicker.setValue(LocalDate.now().plusWeeks(1));
    }

    public TextField getTitleField() {
        return titleField;
    }

    public String getTitle() {
        return titleField.getText();
    }

    public LocalDate getDueDate() {
        return dueDatePicker.getValue();
    }
}
