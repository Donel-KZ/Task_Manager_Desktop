package org.donel.taskmanagerdesktop.Controllers;


import org.donel.taskmanagerdesktop.services.UserResponse;

public class ProjectResponse{
    long id;
    String name;
    String description;
    ProjectType type;
    WorkStatus status;
    String dueDate;
    boolean pastDue;
    UserResponse owner;

    public ProjectResponse() {
    }

    public ProjectResponse(long id, String name, String description, String type, String status, String dueDate, boolean pastDue, UserResponse owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type != null ? ProjectType.valueOf(type) : ProjectType.GROUP;
        this.status = status != null ? WorkStatus.valueOf(status) : WorkStatus.PENDING;
        this.dueDate = dueDate;
        this.pastDue = pastDue;
        this.owner = owner;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public ProjectType type() {
        return type;
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

    public UserResponse owner() {
        return owner;
    }
}
