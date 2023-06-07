package org.agilelovers.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.agilelovers.ui.controller.MainController;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MainApplication extends Application {

    private static MainApplication instance;

    private Stage currentStage;

    /**
     * Starts the application, loads the UI and initializes the controller.
     *
     * @param stage the stage
     * @throws IOException if any I/O error occurs
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.currentStage = stage;
        instance = this;

        if (this.isAutoLoginEnabled()) {
            MainController.setUid(new Scanner(new File(Constants.USER_TOKEN_PATH)).nextLine());
        }

        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(this.isAutoLoginEnabled() ? "/MainExperimental.fxml" : "/Login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, Color.WHITE);

        stage.setTitle("SayIt Assistant");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/icon.jpg"))));

        stage.setScene(scene);
        stage.show();
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    private boolean isAutoLoginEnabled() throws IOException {
        File f = new File(Constants.USER_TOKEN_PATH);
        System.out.println(f.exists());
        return f.exists() && !f.isDirectory();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
