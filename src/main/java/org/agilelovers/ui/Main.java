package org.agilelovers.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.agilelovers.ui.controller.MainController;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/Login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, Color.WHITE);

        //MainController.instance = fxmlLoader.getController();

        stage.setTitle("SayIt Assistant");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/icon.jpg"))));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
