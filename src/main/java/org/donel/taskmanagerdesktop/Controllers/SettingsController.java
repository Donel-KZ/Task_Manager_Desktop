package org.donel.taskmanagerdesktop.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.services.ApiException;
import org.donel.taskmanagerdesktop.services.Session;
import org.donel.taskmanagerdesktop.services.UserResponse;
import org.donel.taskmanagerdesktop.services.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class SettingsController {

    @FXML private ImageView avatarImageView;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Button uploadProfilePictureButton;

    private final UserService userService = new UserService(new ApiClient());

    @FXML
    public void initialize() {
        refreshProfile();
    }

    @FXML
    private void handleUploadProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp", "*.gif")
        );

        File selected = chooser.showOpenDialog(uploadProfilePictureButton.getScene().getWindow());
        if (selected == null) {
            return;
        }

        try {
            UserResponse updatedUser = (UserResponse) userService.uploadProfilePicture(Path.of(selected.toURI()));
            if (updatedUser != null) {
                Session.getInstance().setCurrentUser(updatedUser);
                refreshProfile();
                showInfo("Profile picture updated.");
            }
        } catch (IOException | InterruptedException | ApiException e) {
            showError("Could not upload the profile picture. Please try again.");
            e.printStackTrace();
        }
    }

    private void refreshProfile() {
        UserResponse currentUser = Session.getInstance().getCurrentUser();
        String fallbackImage = "/org/donel/taskmanagerdesktop/taskmanager_ios_icon_1024.png";

        if (currentUser == null) {
            nameLabel.setText("Guest User");
            emailLabel.setText("No account loaded");
            avatarImageView.setImage(new Image(getClass().getResourceAsStream(fallbackImage)));
            return;
        }

        nameLabel.setText(currentUser.displayName() == null || currentUser.displayName().isBlank()
                ? "Unknown User" : currentUser.displayName());
        emailLabel.setText(currentUser.email() == null || currentUser.email().isBlank()
                ? "No email provided" : currentUser.email());

        String pictureUrl = currentUser.profilePictureUrl();
        if (pictureUrl != null && !pictureUrl.isBlank()) {
            try {
                avatarImageView.setImage(new Image(pictureUrl, true));
            } catch (Exception ignored) {
                avatarImageView.setImage(new Image(getClass().getResourceAsStream(fallbackImage)));
            }
        } else {
            avatarImageView.setImage(new Image(getClass().getResourceAsStream(fallbackImage)));
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Picture");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Profile Picture");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
