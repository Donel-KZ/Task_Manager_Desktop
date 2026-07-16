package org.donel.taskmanagerdesktop.Controllers;

import org.donel.taskmanagerdesktop.Navigator.SceneNavigator;
import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.services.Session;
import org.donel.taskmanagerdesktop.services.UserService;
import org.donel.taskmanagerdesktop.services.UserResponse;
import org.donel.taskmanagerdesktop.services.ApiException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button signupNavButton;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService(new ApiClient());

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField == null || emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField == null || passwordField.getText() == null ? "" : passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            showError("Email and password are required");
            return;
        }

        loginButton.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                UserResponse user = userService.login(email, password);
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    SceneNavigator.switchTo("Shell.fxml");
                });
            } catch (ApiException e) {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    showError("Login failed: " + e.getMessage());
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    showError("Connection error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void openSignUp() {
        SceneNavigator.switchTo("SignUpView.fxml");
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
