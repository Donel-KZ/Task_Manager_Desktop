package org.donel.taskmanagerdesktop.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextInputDialog;
import org.donel.taskmanagerdesktop.Dialogs.NewTaskInput;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarController {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button newSessionButton;

    private YearMonth currentMonth = YearMonth.now();
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final List<LocalDate> sessionDates = new ArrayList<>();

    @FXML
    public void initialize() {
        seedCalendarFromTasks();
        prevMonthButton.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendar();
        });
        nextMonthButton.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendar();
        });
        newSessionButton.setOnAction(e -> openAddSessionDialog());
        renderCalendar();
    }

    private void seedCalendarFromTasks() {
        List<Task> tasks = TaskService.getInstance().getAllTasks();
        for (Task task : tasks) {
            if (task.getDueDate() != null) {
                sessionDates.add(task.getDueDate());
            }
        }
    }

    private void renderCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.format(monthFormatter));

        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < headers.length; i++) {
            Label dayHeader = new Label(headers[i]);
            dayHeader.getStyleClass().add("calendar-day-header");
            calendarGrid.add(dayHeader, i, 0);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int leadingBlank = firstOfMonth.getDayOfWeek().getValue() % 7;
        LocalDate dateCursor = firstOfMonth.minusDays(leadingBlank);

        for (int row = 1; row <= 5; row++) {
            for (int col = 0; col < 7; col++) {
                VBox cell = new VBox(4);
                cell.setPadding(new Insets(8));
                cell.getStyleClass().add("calendar-cell");

                LocalDate currentDate = dateCursor.plusDays(col + (row - 1) * 7L);
                Label dateLabel = new Label(String.valueOf(currentDate.getDayOfMonth()));
                dateLabel.getStyleClass().add("calendar-cell-label");
                if (currentDate.getMonth() != currentMonth.getMonth()) {
                    dateLabel.getStyleClass().add("calendar-cell-muted");
                }
                if (currentDate.isEqual(LocalDate.now())) {
                    cell.getStyleClass().add("calendar-cell-today");
                }

                cell.getChildren().add(dateLabel);
                if (sessionDates.contains(currentDate)) {
                    Label sessionPill = new Label("Session");
                    sessionPill.getStyleClass().addAll("status-badge", "status-badge-medium");
                    cell.getChildren().add(sessionPill);
                }
                calendarGrid.add(cell, col, row);
            }
        }
    }

    private void openAddSessionDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(newSessionButton.getScene().getWindow());
        dialog.setTitle("Add Session");
        dialog.setHeaderText("Create a calendar session");
        dialog.setContentText("Session title:");
        dialog.getDialogPane().getStylesheets().add(
                CalendarController.class.getResource("/org/donel/taskmanagerdesktop/styles.css").toExternalForm()
        );

        dialog.showAndWait().ifPresent(title -> {
            if (title == null || title.isBlank()) {
                return;
            }
            LocalDate dueDate = LocalDate.now();
            TaskService.getInstance().addFromInput(new NewTaskInput(title.trim(), "", "Medium", dueDate));
            sessionDates.add(dueDate);
            renderCalendar();
        });
    }
}
