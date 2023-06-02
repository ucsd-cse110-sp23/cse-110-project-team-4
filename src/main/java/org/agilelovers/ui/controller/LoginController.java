package org.agilelovers.ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.UserCredential;
import org.agilelovers.ui.util.FrontEndAPIUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Controller class for the login UI.
 * This class is responsible for handling user login and account creation.
 */
public class LoginController {
    /**
     * The Username field.
     */
    @FXML
    protected TextField usernameField;
    /**
     * The Password field.
     */
    @FXML
    protected PasswordField passwordField;
    /**
     * The Create account button.
     */
    @FXML
    protected Button createAccButton;
    /**
     * The Login button.
     */
    @FXML
    protected Button loginButton;
    /**
     * The Warning label. This label displays a warning message if the user enters invalid credentials.
     */
    @FXML
    protected Label warningLabel;

    /**
     * Initializes the login UI.
     * This method is called when the login UI is first loaded. It checks if the user has enabled auto login and
     * automatically logs the user in if they have.
     * If auto login is not enabled, the user is prompted to enter their credentials.
     *
     * @throws IOException if the user token file cannot be read
     */
    @FXML
    private void initialize() throws IOException {
        // check if autologin is enabled
        File f = new File(Constants.USER_TOKEN_PATH);
        if (f.exists() && !f.isDirectory()) {
            MainController.setId(new BufferedReader(new FileReader(Constants.USER_TOKEN_PATH)).readLine());
            switchToMainUI();
        }

    }

    /**
     * Checks if username and password fields are empty. If they are, a warning message is displayed.
     *
     * @return true if either fields are empty
     */
    private boolean verifyEmptyFields() {
        if (this.usernameField.getText().isEmpty() || this.passwordField.getText().isEmpty()) {
            this.warningLabel.setText("Please enter a username and password.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Disables the login and create account buttons.
     * Should be called when the login or create account buttons are already pressed to prevent multiple requests from
     * being sent to the backend.
     */
    private void disableButtons() {
        this.createAccButton.setDisable(true);
        this.loginButton.setDisable(true);
    }

    /**
     * Enables the login and create account buttons. Should be called if the login or create account request fails.
     */
    private void enableButtons() {
        this.createAccButton.setDisable(false);
        this.loginButton.setDisable(false);
    }

    /**
     * Prompts the user to enable auto login. If the user accepts, their user token is saved to a file.
     * The user is then logged in.
     * TODO: switching to main UI should be done in a separate method, by a different class (Not SRP rn)
     * @param uid the user's unique ID
     * @throws IOException if the user token file cannot be written to
     */
    private void promptAutoLogin(String uid) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Auto Login");
        alert.setContentText("Do you want to enable auto login on this device?");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.USER_TOKEN_PATH));
            writer.write(uid);
            writer.close();
        }

        MainController.setId(uid);

        switchToMainUI();
    }

    /**
     * Triggered when the user clicks the "Login" button.
     * Checks the login credentials and logs the user in if they are valid.
     * If the credentials are invalid, a warning message is displayed.
     * If the credentials are valid, the user is prompted to enable auto login.
     *
     * @param event event triggered by the "Login" button click
     */
    public void login(ActionEvent event) {
        if (verifyEmptyFields()) {
            return;
        }
        this.disableButtons();
        Platform.runLater(() -> {
            try {
                UserCredential credential = FrontEndAPIUtils.login(this.usernameField.getText(), this.passwordField.getText(), MainController.getId());
                this.promptAutoLogin(credential.getId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                this.loginFailed();
            }
        });
    }

    /**
     * Helper method that handles login failure.
     * Displays an error message and re-enables the login and create account buttons.
     */
    private void loginFailed() {
        this.warningLabel.setText("Login Failed. Incorrect user credentials.");
        this.enableButtons();
    }

    /**
     * Helper method that handles create account failure.
     * Displays an error message and re-enables the login and create account buttons.
     */
    private void createAccountFailed() {
        this.warningLabel.setText("Username already exists. Try again.");
        this.enableButtons();
    }

    /**
     * Triggered when the user clicks the "Create Account" button.
     * Creates a new account with the given credentials.
     * If the username already exists, an error message is displayed.
     *
     * @param event event triggered by the "Create Account" button click
     */
    public void createAccount(ActionEvent event) {
        if (verifyEmptyFields()) {
            return;
        }
        this.disableButtons();
        // add username and password to database
        Platform.runLater(() -> {
            try {
                UserCredential credential = FrontEndAPIUtils.createAccount(this.usernameField.getText(), this.passwordField.getText());
                this.promptAutoLogin(credential.getId());
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                this.createAccountFailed();
            }
        });
    }

    /**
     * Switches to the main UI. This method is called when the user is successfully logged in.
     * Removes the current login UI and loads the main UI.
     * TODO: switching to main UI should be done in a separate method, by a different class (Not SRP rn)
     *
     * @throws IOException if the main UI cannot be loaded
     */
    public void switchToMainUI() throws IOException {
        Stage stage = (Stage) this.loginButton.getScene().getWindow();
        stage.close();

        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MainExperimental.fxml"));
        Parent root = fxmlLoader.load();
        MainController.instance = fxmlLoader.getController();
        stage = new Stage();
        stage.setTitle("SayItAssistant");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }


}