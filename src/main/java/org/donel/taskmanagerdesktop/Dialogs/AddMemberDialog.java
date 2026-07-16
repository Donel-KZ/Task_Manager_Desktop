package org.donel.taskmanagerdesktop.Dialogs;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

/**
 * Simple one-field dialog for adding a member by email/display name.
 * Kept as a styled TextInputDialog rather than a full FXML dialog since
 * it's just one input - the lookup-by-email logic itself lives in
 * GroupProjectDetailWindow.addMember(), mirroring the iOS app's
 * TaskManagerSyncService.addMember().
 */
public final class AddMemberDialog {

    private AddMemberDialog() {
    }

    public static Optional<String> show(Window owner) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(owner);
        dialog.setTitle("Add Member");
        dialog.setHeaderText(null);
        dialog.setContentText("Member's email or display name:");
        dialog.getDialogPane().getStylesheets().add(
                AddMemberDialog.class.getResource("/org/donel/taskmanagerdesktop/styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().addAll("theme-dialog", "add-task-dialog");

        return dialog.showAndWait().filter(text -> !text.isBlank());
    }
}
