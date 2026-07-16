package org.donel.taskmanagerdesktop.Dialogs;

import java.io.IOException;
import java.util.Optional;

import org.donel.taskmanagerdesktop.Controllers.CreateDeliverableDialogController;
import org.donel.taskmanagerdesktop.Controllers.NewDeliverableInput;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

public final class CreateDeliverableDialog {

    private CreateDeliverableDialog() {
    }

    public static Optional<NewDeliverableInput> show(Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                CreateDeliverableDialog.class.getResource("CreateDeliverableDialog.fxml")
        );
        DialogPane dialogPane = loader.load();
        CreateDeliverableDialogController controller = loader.getController();

        Dialog<NewDeliverableInput> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Add Deliverable");
        dialog.getDialogPane().getStyleClass().add("theme-dialog");
        dialog.setDialogPane(dialogPane);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        Button addButton = (Button) dialogPane.lookupButton(addButtonType);
        addButton.getStyleClass().add("primary-button");
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("secondary-button");

        addButton.setDisable(true);
        controller.getTitleField().textProperty().addListener((obs, oldText, newText) ->
                addButton.setDisable(newText == null || newText.isBlank())
        );

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                return new NewDeliverableInput(
                        controller.getTitle().trim(),
                        controller.getDescription(),
                        controller.getDueDate()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
