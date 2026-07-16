package org.donel.taskmanagerdesktop.Dialogs;

import java.io.IOException;
import java.util.Optional;

import org.donel.taskmanagerdesktop.services.NewProjectInput;
import org.donel.taskmanagerdesktop.Controllers.CreateProjectDialogController;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

public final class CreateProjectDialog {

    private CreateProjectDialog() {
    }

    public static Optional<NewProjectInput> show(Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                CreateProjectDialog.class.getResource("CreateProjectDialog.fxml")
        );
        DialogPane dialogPane = loader.load();
        CreateProjectDialogController controller = loader.getController();

        Dialog<NewProjectInput> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("New Group Project");
        dialog.getDialogPane().getStyleClass().add("theme-dialog");
        dialog.setDialogPane(dialogPane);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        Button createButton = (Button) dialogPane.lookupButton(createButtonType);
        createButton.getStyleClass().add("primary-button");
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("secondary-button");

        createButton.setDisable(true);
        controller.getTitleField().textProperty().addListener((obs, oldText, newText) ->
                createButton.setDisable(newText == null || newText.isBlank())
        );

        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                return new NewProjectInput(controller.getTitle().trim(), controller.getDueDate());
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
