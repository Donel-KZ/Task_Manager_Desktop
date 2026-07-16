package org.donel.taskmanagerdesktop.Controllers;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewDeliverableInput {
    String title;
    String description;
    LocalDate dueDate;

    public NewDeliverableInput(String trim, String description, java.time.LocalDate dueDate) {
    }

    public CreateDeliverableRequest toRequest() {
        String isoDate = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return new CreateDeliverableRequest(title, description.isBlank() ? null : description, isoDate);
    }
}
