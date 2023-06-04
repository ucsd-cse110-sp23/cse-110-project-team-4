package org.agilelovers.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EmailSetupController {
    /**
     * The first name field.
     */
    @FXML
    TextField firstNameField;
    /**
     * The last name field.
     */
    @FXML
    TextField lastNameField;
    /**
     * The display name field.
     */
    @FXML
    TextField displayNameField;
    /**
     * The email address field.
     */
    @FXML
    TextField emailField;
    /**
     * The password field.
     */
    @FXML
    PasswordField passwordField;
    /**
     * The SMTP host field.
     */
    @FXML
    TextField smtpHostField;
    /**
     * The TLS port field.
     */
    @FXML
    TextField tlsPortField;
    /**
     * The cancel button.
     */
    @FXML
    Button cancelButton;
    /**
     * The save button.
     */
    @FXML
    Button saveButton;

    /**
     * Initializes the email setup UI. Fills in all fields with user's previous input (if any).
     * Otherwise, fields are left blank.
     */
    @FXML
    private void initialize() {
        firstNameField.setText("");
        lastNameField.setText("");
        displayNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        smtpHostField.setText("");
    }

    /**
     * Saves the inputted information to the database
     *
     * @param event event triggered by "Save" button click
     * @throws IOException
     */
    public void saveInfo(ActionEvent event) throws IOException {

        // TODO: switch to main UI
    }

    /**
     * Cancels the email setup process and returns to the main screen.
     * @param event event triggered by "Cancel" button click
     * @throws IOException
     */
    public void cancelSetup(ActionEvent event) throws IOException {
        // TODO: switch to main UI
    }

}