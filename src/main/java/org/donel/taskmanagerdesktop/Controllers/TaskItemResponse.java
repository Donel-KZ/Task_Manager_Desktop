package org.donel.taskmanagerdesktop.Controllers;


public class TaskItemResponse {
    long id;
    long deliverableId;
    String title;
    WorkStatus status;
    String dueDate;
    boolean pastDue;

    public long id() {
        return id;
    }

    public long deliverableId() {
        return deliverableId;
    }

    public String title() {
        return title;
    }

    public WorkStatus status() {
        return status;
    }

    public String dueDate() {
        return dueDate;
    }

    public boolean pastDue() {
        return pastDue;
    }
}
