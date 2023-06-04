package org.agilelovers.ui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.agilelovers.ui.enums.SceneType;

import java.io.IOException;

public class SceneChanger {
    private static SceneChanger instance = new SceneChanger();

    public static SceneChanger getInstance() {
        return instance;
    }

    public void switchScene(Stage stage, SceneType type) throws IOException {
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MainExperimental.fxml"));
        Parent root = fxmlLoader.load();
        MainController.instance = fxmlLoader.getController();
        stage.setTitle("SayItAssistant");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
