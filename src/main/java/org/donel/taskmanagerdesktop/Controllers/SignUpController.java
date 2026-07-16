package org.donel.taskmanagerdesktop.Controllers;

import org.donel.taskmanagerdesktop.Navigator.SceneNavigator;
import org.donel.taskmanagerdesktop.services.Session;
import org.donel.taskmanagerdesktop.services.UserResponse;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Button loginNavButton;

    @FXML
    private void handleSignUp() {
        String displayName = fullNameField == null || fullNameField.getText() == null ? "Guest" : fullNameField.getText().trim();
        String email = emailField == null || emailField.getText() == null ? "" : emailField.getText().trim();

        Session.getInstance().setCurrentUser(new UserResponse(0L, email, displayName.isBlank() ? "Guest" : displayName, null));
        SceneNavigator.switchTo("Shell.fxml");
    }

    @FXML
    private void openLogin() {
        SceneNavigator.switchTo("LoginView.fxml");
    }
}
