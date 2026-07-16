package org.donel.taskmanagerdesktop.services;

public final class Session {
    private static final Session INSTANCE = new Session();

    private UserResponse currentUser;
    private String authToken;

    private Session() {
    }

    public static Session getInstance() {
        return INSTANCE;
    }

    public UserResponse getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserResponse currentUser) {
        this.currentUser = currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public boolean isAuthenticated() {
        return authToken != null && !authToken.isBlank() && currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
        this.authToken = null;
    }
}
