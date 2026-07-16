package org.donel.taskmanagerdesktop.services;

import org.donel.taskmanagerdesktop.Controllers.CreateProjectRequest;
import org.donel.taskmanagerdesktop.Controllers.ProjectType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record NewProjectInput(String title, LocalDate dueDate) {

    public CreateProjectRequest toRequest() {
        long ownerId = Session.getInstance().getCurrentUser().id();
        String isoDate = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return new CreateProjectRequest(title, null, ProjectType.GROUP, isoDate, ownerId);
    }
}
