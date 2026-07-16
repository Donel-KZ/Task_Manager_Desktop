package org.donel.taskmanagerdesktop.Controllers;


import org.donel.taskmanagerdesktop.services.UserResponse;

public class ProjectMemberResponse{
    long id;
    UserResponse user;
    ProjectRole role;

    public long id() {
        return id;
    }

    public UserResponse user() {
        return user;
    }

    public ProjectRole role() {
        return role;
    }
}
