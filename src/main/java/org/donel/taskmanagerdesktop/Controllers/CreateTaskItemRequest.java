package org.donel.taskmanagerdesktop.Controllers;

public class CreateTaskItemRequest{
    String title;
    String dueDate;
    long deliverableId;

    public CreateTaskItemRequest() {
    }

    public CreateTaskItemRequest(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public CreateTaskItemRequest(String title, String dueDate, long deliverableId) {
        this.title = title;
        this.dueDate = dueDate;
        this.deliverableId = deliverableId;
    }

    public String title() {
        return title;
    }

    public String dueDate() {
        return dueDate;
    }

    public long deliverableId() {
        return deliverableId;
    }
}
