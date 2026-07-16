package org.donel.taskmanagerdesktop.Controllers;

public class DeliverableResponse {
    long id;
    long projectId;
    String title;
    String description;
    WorkStatus status;
    String dueDate;
    boolean pastDue;

    public long id() {
        return id;
    }

    public long projectId() {
        return projectId;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
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
