package org.donel.taskmanagerdesktop.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.donel.taskmanagerdesktop.api.ApiClient;

public class UserService {

    private ApiClient apiClient;

    public UserService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public UserResponse login(String email, String password) throws IOException, InterruptedException, ApiException {
        LoginRequest request = new LoginRequest(email, password);
        LoginResponse response = apiClient.post("/auth/login", request, LoginResponse.class);
        
        if (response != null && response.token() != null) {
            Session.getInstance().setAuthToken(response.token());
            Session.getInstance().setCurrentUser(response.user());
            return response.user();
        }
        throw new ApiException("Login failed: no token received");
    }

    public List<UserResponse> getUsers() throws IOException, InterruptedException, ApiException {
        UserResponse[] response = apiClient.get("/users", UserResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public UserResponse getCurrentUser() throws IOException, InterruptedException, ApiException {
        return apiClient.get("/users/me", UserResponse.class);
    }

    public Record uploadProfilePicture(Path imagePath) throws IOException, InterruptedException, ApiException {
        byte[] fileBytes = Files.readAllBytes(imagePath);
        String fileName = imagePath.getFileName().toString().toLowerCase();
        String mimeType = detectMimeType(fileName);

        return apiClient.uploadFile("/users/me/profile-picture", imagePath.getFileName().toString(), mimeType, fileBytes, UserResponse.class);
    }

    private String detectMimeType(String fileName) {
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".webp")) {
            return "image/webp";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}
