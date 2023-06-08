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
        switch (type) {
            case MAIN_UI -> {
                fxmlLoader.setLocation(getClass().getResource("/MainExperimental.fxml"));
                Parent root = fxmlLoader.load();
                MainController.instance = fxmlLoader.getController();
                stage.setTitle("SayItAssistant");
                stage.setScene(new Scene(root));
                stage.show();
            }
            case EMAIL_SETUP_UI -> {
                fxmlLoader.setLocation(getClass().getResource("/EmailSetup.fxml"));
                Stage newStage = new Stage();
                Parent root = fxmlLoader.load();
                newStage.setTitle("SayItAssistant");
                newStage.setScene(new Scene(root));
                newStage.show();
            }

        }
    }
}
