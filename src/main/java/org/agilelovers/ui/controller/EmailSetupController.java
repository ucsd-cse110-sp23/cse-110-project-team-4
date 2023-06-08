package org.agilelovers.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.agilelovers.ui.MainApplication;
import org.agilelovers.ui.enums.SceneType;
import org.agilelovers.ui.object.EmailConfig;
import org.agilelovers.ui.util.FrontEndAPIUtils;

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
    private void initialize() throws IOException, InterruptedException {
        EmailConfig config = FrontEndAPIUtils.getEmailConfig(MainController.getUid());
        firstNameField.setText(config.getFirstName());
        lastNameField.setText(config.getLastName());
        displayNameField.setText(config.getDisplayName());
        emailField.setText(config.getEmail());
        passwordField.setText(config.getEmailPassword());
        smtpHostField.setText(config.getSmtpHost());
        tlsPortField.setText(config.getTlsPort());
    }

    /**
     * Saves the inputted information to the database
     *
     * @param event event triggered by "Save" button click
     * @throws IOException
     */
    @FXML
    public void saveInfo(ActionEvent event) throws IOException, InterruptedException {
        EmailConfig config = new EmailConfig(firstNameField.getText(), lastNameField.getText(), displayNameField.getText(), emailField.getText(), passwordField.getText(), smtpHostField.getText(), tlsPortField.getText());
        FrontEndAPIUtils.setEmailConfig(config, MainController.getUid());
        SceneChanger.getInstance().switchScene(MainApplication.getInstance().getCurrentStage(), SceneType.MAIN_UI);
    }

    /**
     * Cancels the email setup process and returns to the main screen.
     * @param event event triggered by "Cancel" button click
     * @throws IOException
     */
    @FXML
    public void cancel(ActionEvent event) throws IOException {
        SceneChanger.getInstance().switchScene(MainApplication.getInstance().getCurrentStage(), SceneType.MAIN_UI);
    }

}