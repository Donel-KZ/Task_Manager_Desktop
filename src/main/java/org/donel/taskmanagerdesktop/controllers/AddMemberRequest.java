package org.donel.taskmanagerdesktop.Controllers;

public class AddMemberRequest{
    long userId;
    ProjectRole role;

    public AddMemberRequest() {
    }

    public AddMemberRequest(long userId, ProjectRole role) {
        this.userId = userId;
        this.role = role;
    }

    public long userId() {
        return userId;
    }

    public ProjectRole role() {
        return role;
    }
}

