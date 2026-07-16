package org.donel.taskmanagerdesktop.services;

public record LoginResponse(UserResponse user, String token) {
}
