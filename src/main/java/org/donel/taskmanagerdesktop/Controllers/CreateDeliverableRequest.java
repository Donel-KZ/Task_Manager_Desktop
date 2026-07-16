package org.donel.taskmanagerdesktop.Controllers;


public class CreateDeliverableRequest{
    String title;
    String description;
    String dueDate;

    public CreateDeliverableRequest() {
    }

    public CreateDeliverableRequest(String title, String description, String dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String dueDate() {
        return dueDate;
    }
}
