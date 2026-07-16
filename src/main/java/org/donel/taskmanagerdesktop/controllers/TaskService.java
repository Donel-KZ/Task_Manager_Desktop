package org.donel.taskmanagerdesktop.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.donel.taskmanagerdesktop.Dialogs.NewTaskInput;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Central in-memory source of truth for the desktop task UI.
 *
 * The hard-coded task examples have been removed so the application now starts
 * empty and only displays data that is created through the dialogs.
 */
public final class TaskService {

    private static final TaskService INSTANCE = new TaskService();

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private TaskService() {
        // intentionally empty: no seeded demo tasks
    }

    public static TaskService getInstance() {
        return INSTANCE;
    }

    public ObservableList<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getPendingTasks() {
        return byStatus(TaskStatus.PENDING);
    }

    public List<Task> getFinishedTasks() {
        return byStatus(TaskStatus.FINISHED);
    }

    public List<Task> getOverdueTasks() {
        return byStatus(TaskStatus.OVERDUE);
    }

    public List<Task> getTodayTasks() {
        return tasks.stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());
    }

    public int getTotalCount() {
        return tasks.size();
    }

    public int getPendingCount() {
        return getPendingTasks().size();
    }

    public int getFinishedCount() {
        return getFinishedTasks().size();
    }

    public int getOverdueCount() {
        return getOverdueTasks().size();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addFromInput(NewTaskInput input) {
        if (input == null || input.title() == null || input.title().isBlank()) {
            return;
        }

        LocalDate dueDate = input.dueDate();
        TaskStatus status = determineStatus(dueDate);
        TaskPriority priority = parsePriority(input.priority());
        boolean dueToday = dueDate != null && dueDate.isEqual(LocalDate.now());

        Task task = new Task(
                idGenerator.getAndIncrement(),
                input.title().trim(),
                input.description() == null ? "" : input.description().trim(),
                status,
                priority,
                dueToday,
                dueDate
        );

        tasks.add(task);
    }

    public void toggleTaskCompletion(int taskId) {
        tasks.replaceAll(task -> {
            if (task.getId() != taskId) {
                return task;
            }
            TaskStatus nextStatus = task.getStatus() == TaskStatus.FINISHED
                    ? determineStatus(task.getDueDate())
                    : TaskStatus.FINISHED;
            return task.withStatus(nextStatus);
        });
    }

    private TaskStatus determineStatus(LocalDate dueDate) {
        if (dueDate == null) {
            return TaskStatus.PENDING;
        }
        return dueDate.isBefore(LocalDate.now()) ? TaskStatus.OVERDUE : TaskStatus.PENDING;
    }

    private TaskPriority parsePriority(String priorityText) {
        if (priorityText == null || priorityText.isBlank()) {
            return TaskPriority.MEDIUM;
        }
        try {
            return TaskPriority.valueOf(priorityText.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return TaskPriority.MEDIUM;
        }
    }

    private List<Task> byStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }
}
