
package org.donel.taskmanagerdesktop.Dialogs;

import java.time.LocalDate;

/**
 * Minimal DTO returned by the add-task dialog.
 */
public record NewTaskInput(String title, String description, String priority, LocalDate dueDate) {
}
