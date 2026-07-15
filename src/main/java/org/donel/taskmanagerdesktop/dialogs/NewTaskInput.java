
package org.donel.taskmanagerdesktop.dialogs;

import java.time.LocalDate;

/**
 * Minimal DTO returned by the add-task dialog.
 */
public record NewTaskInput(String title, String description, String priority, LocalDate dueDate) {
}
