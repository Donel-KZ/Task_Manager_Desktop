package org.donel.taskmanagerdesktop.Controllers;

import java.time.LocalDate;

/**
 * Plain in-memory task model. Once the Spring Boot backend is wired in,
 * this shape should line up closely with the API's task DTO/entity -
 * TaskService is the only place that will need to change to fetch these
 * from the network instead of an in-memory list.
 */
public class Task {

    private final int id;
    private final String title;
    private final String subtitle;      // e.g. "Due Today", "Was due yesterday", "Completed Yesterday"
    private final TaskStatus status;
    private final TaskPriority priority; // only meaningful for PENDING tasks; null otherwise
    private final boolean dueToday;      // used to populate Home's "Today's Tasks" section
    private final LocalDate dueDate;

    public Task(int id, String title, String subtitle, TaskStatus status,
                TaskPriority priority, boolean dueToday, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.status = status;
        this.priority = priority;
        this.dueToday = dueToday;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public boolean isDueToday() {
        return dueToday;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Task withStatus(TaskStatus updatedStatus) {
        return new Task(id, title, subtitle, updatedStatus, priority, dueToday, dueDate);
    }
}
