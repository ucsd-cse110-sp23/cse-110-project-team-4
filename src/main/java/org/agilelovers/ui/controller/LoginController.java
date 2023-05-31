package org.agilelovers.ui.controller;

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
     *
     * @throws IOException
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
     * Checks if username and password fields are empty.
     *
     * @return true if either are emtpy
     */
    private boolean areFieldsEmpty() {
        if (this.usernameField.getText().isEmpty() || this.passwordField.getText().isEmpty()) {
            this.warningLabel.setText("Please enter a username and password.");
            return true;
        } else {
            return false;
        }
    }

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
     * Checks the login credentials and logs the user in if they are valid.
     *
     * @param event event triggered by the "Login" button click
     */
    public void login(ActionEvent event) {
        if (areFieldsEmpty()) {
            return;
        }
        new Thread(() -> {
            try {
                UserCredential credential = FrontEndAPIUtils.login(this.usernameField.getText(), this.passwordField.getText(), MainController.getId());
                this.promptAutoLogin(credential.getId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                this.loginFailed();
            }
        }).start();
    }

    private void loginFailed() {
        this.warningLabel.setText("Login Failed. Incorrect user credentials.");
    }
    private void createAccountFailed() {
        this.warningLabel.setText("Username already exists. Try again.");
    }

    /**
     * Creates a new account with the given credentials (if valid)
     *
     * @param event event triggered by the "Create Account" button click
     */
    public void createAccount(ActionEvent event) throws IOException {
        if (areFieldsEmpty()) {
            return;
        }
        // add username and password to database
        new Thread(() -> {
            try {
                UserCredential credential = FrontEndAPIUtils.createAccount(this.usernameField.getText(), this.passwordField.getText());
                this.promptAutoLogin(credential.getId());
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                this.createAccountFailed();
            }
        }).start();
    }

    /**
     * Switches to the main UI. (logs user in)
     *
     * @throws IOException
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
