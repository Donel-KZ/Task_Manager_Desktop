package org.donel.taskmanagerdesktop.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.donel.taskmanagerdesktop.controllers.Task;
import org.donel.taskmanagerdesktop.controllers.TaskPriority;
import org.donel.taskmanagerdesktop.controllers.TaskStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single in-memory source of truth for tasks, shared by Dashboard, Pending,
 * Finished, and Overdue controllers so their counts and lists always agree.
 *
 * This is a placeholder for the real Spring Boot backend. When that's wired
 * in, this class is the only place that needs to change - swap the seeded
 * ObservableList for data fetched from the API, and every screen that reads
 * through TaskService keeps working unchanged.
 */
public final class TaskService {

    private static final TaskService INSTANCE = new TaskService();

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    private TaskService() {
        seed();
    }

    public static TaskService getInstance() {
        return INSTANCE;
    }

    private void seed() {
        tasks.addAll(
                new Task(1, "Finish JavaFX Desktop UI", "Due Today",
                        TaskStatus.PENDING, TaskPriority.HIGH, true),
                new Task(2, "Write project README", "Due in 2 days",
                        TaskStatus.PENDING, TaskPriority.MEDIUM, false),
                new Task(3, "Review pull request #42", "Due in 5 days",
                        TaskStatus.PENDING, TaskPriority.LOW, false),

                new Task(4, "Connect Spring Boot Backend", "Completed Yesterday",
                        TaskStatus.FINISHED, null, true),
                new Task(5, "Push iOS TaskManager to GitHub", "Completed 3 days ago",
                        TaskStatus.FINISHED, null, false),

                new Task(6, "Fix Room 2.6.1 + KSP2 conflict", "Was due 2 days ago",
                        TaskStatus.OVERDUE, null, false),
                new Task(7, "Submit research proposal draft", "Was due yesterday",
                        TaskStatus.OVERDUE, null, false)
        );
    }

    public ObservableList<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getPendingTasks() {
        return byStatus(TaskStatus.PENDING);
    }

    public List<org.donel.taskmanagerdesktop.controllers.Task> getFinishedTasks() {
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

    private List<Task> byStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }
}
