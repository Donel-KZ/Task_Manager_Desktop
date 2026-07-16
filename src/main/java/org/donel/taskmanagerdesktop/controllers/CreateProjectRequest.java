package org.donel.taskmanagerdesktop.Controllers;


public class CreateProjectRequest {
    String title;
    String description;
    ProjectType type;
    String dueDate;
    long ownerId;

    public CreateProjectRequest() {
    }

    public CreateProjectRequest(String title, String description, ProjectType type, String dueDate, long ownerId) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.dueDate = dueDate;
        this.ownerId = ownerId;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public ProjectType type() {
        return type;
    }

    public String dueDate() {
        return dueDate;
    }

    public long ownerId() {
        return ownerId;
    }
}

