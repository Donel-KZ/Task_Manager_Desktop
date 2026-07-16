package org.donel.taskmanagerdesktop.Controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Builds the task-card Node used across Dashboard, Pending, Finished, and
 * Overdue views. Centralising this means all four views render tasks
 * identically instead of each FXML hand-rolling its own copy of the same
 * markup (which is how the duplicate-shell bug crept in previously).
 */
public final class TaskCardFactory {

    private TaskCardFactory() {
        // static utility, no instances
    }

    public static StackPane createCard(Task task) {
        StackPane card = new StackPane();
        card.setPrefHeight(80);
        card.getStyleClass().add("task-card");

        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));

        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(task.getStatus() == TaskStatus.FINISHED);
        checkbox.setOnAction(event -> TaskService.getInstance().toggleTaskCompletion(task.getId()));

        VBox textBox = new VBox(4);
        Label title = new Label(task.getTitle());
        title.getStyleClass().add("task-title");
        Label subtitle = new Label(task.getSubtitle());
        subtitle.getStyleClass().add("task-subtitle");
        textBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText(task));
        badge.getStyleClass().addAll("status-badge", badgeStyleClass(task));

        Button actionButton = new Button(task.getStatus() == TaskStatus.FINISHED ? "View" : "Edit");
        actionButton.getStyleClass().add("secondary-button");

        row.getChildren().addAll(checkbox, textBox, spacer, badge, actionButton);
        card.getChildren().add(row);

        return card;
    }

    private static String badgeText(Task task) {
        return switch (task.getStatus()) {
            case FINISHED -> "Done";
            case OVERDUE -> "Overdue";
            case PENDING -> task.getPriority() == null
                    ? ""
                    : switch (task.getPriority()) {
                        case HIGH -> "High";
                        case MEDIUM -> "Medium";
                        case LOW -> "Low";
                    };
        };
    }

    private static String badgeStyleClass(Task task) {
        return switch (task.getStatus()) {
            case FINISHED -> "status-badge-done";
            case OVERDUE -> "status-badge-overdue";
            case PENDING -> task.getPriority() == null
                    ? "status-badge-medium"
                    : switch (task.getPriority()) {
                        case HIGH -> "status-badge-high";
                        case MEDIUM -> "status-badge-medium";
                        case LOW -> "status-badge-low";
                    };
        };
    }
}
