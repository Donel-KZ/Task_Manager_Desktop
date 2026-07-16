package org.donel.taskmanagerdesktop.Dialogs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.donel.taskmanagerdesktop.Controllers.AddTaskDialogController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

public final class AddTaskDialog {

    private AddTaskDialog() {
    }

    public static Optional<NewTaskInput> show(Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(AddTaskDialog.class.getResource("/org/donel/taskmanagerdesktop/AddTaskDialog.fxml"));
        Node content = loader.load();
        AddTaskDialogController controller = loader.getController();

        Dialog<NewTaskInput> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Add Task");
        dialog.getDialogPane().getStyleClass().add("theme-dialog");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String title = controller.getTaskName();
                String description = controller.getDescription();
                LocalDate dueDate = controller.getDueDate();
                String priority = controller.getPriority();

                if (title == null || title.isBlank()) {
                    return null;
                }

                return new NewTaskInput(title, description, priority, dueDate);
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
