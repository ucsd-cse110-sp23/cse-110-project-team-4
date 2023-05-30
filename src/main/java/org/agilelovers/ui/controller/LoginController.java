package org.agilelovers.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
     * userToken for making calls to server
     */
    private static String userToken;


    /**
     * Initializes the login UI.
     * @throws IOException
     */
    @FXML
    private void initialize() throws IOException {
        // check if autologin is enabled
        File f = new File("verified.txt");
        if(f.exists() && !f.isDirectory()) {
            switchToMainUI();
        }

    }

    /**
     * Sets the user token.
     * @param userToken to be set
     */
    public static void setUserToken(String userToken) {
        LoginController.userToken = userToken;
    }

    /**
     * Gets the user token.
     * @return userToken
     */
    public static String getUserToken() {
        if (userToken == null || userToken.isEmpty()) {
            throw new NullPointerException("User token is null or empty.");
        }
        return userToken;
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

    private void saveAutoLogin() {

    }

    private void promptAutoLogin() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Auto Login");
        alert.setContentText("Do you want to enable auto login on this device?");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            System.out.println("yes");
        } else {
            System.out.println("no");
        }

        switchToMainUI();
    }

    /**
     * Checks the login credentials and logs the user in if they are valid.
     *
     * @param event event triggered by the "Login" button click
     */
    public void login(ActionEvent event) throws IOException {
        if (areFieldsEmpty()){
            return;
        }
        // TODO: something API
        promptAutoLogin();
    }

    /**
     * Creates a new account with the given credentials (if valid)
     *
     * @param event event triggered by the "Create Account" button click
     */
    public void createAccount(ActionEvent event) throws IOException {
        if (areFieldsEmpty()){
            return;
        }
        // TODO: something API
        // add username and password to database
        promptAutoLogin();
    }

    /**
     * Switches to the main UI. (logs user in)
     *
     * @throws IOException
     */
    public void switchToMainUI() throws IOException {
        Stage stage = (Stage) this.loginButton.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("/MainExperimental.fxml"));
        stage = new Stage();
        stage.setTitle("SayItAssistant");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }



}
