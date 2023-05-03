package org.agilelovers.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller class for the UI
 */
public class Controller {
    @FXML
    ListView historyList;
    @FXML
    Label questionLabel;
    @FXML
    Label answerLabel;

    private ObservableList<String> pastQuestions = FXCollections.observableArrayList();
    private boolean isInitialized = false;

    public void initHistoryList() {
        historyList.setItems(this.pastQuestions);
    }


    /**
     * Removes all the old questions.
     * TODO: implement this
     *
     * @param event action event
     */
    public void clearAll(ActionEvent event) {
        System.out.println("Clear All");
    }

    /**
     * Delete question.
     *
     * TODO: implement this
     * @param event the event
     */
    public void deleteQuestion(ActionEvent event) {
        System.out.println("Delete Question");
    }

    /**
     * New question.
     *
     * TODO: implement this
     *
     * @param event the event
     */
    public void newQuestion(ActionEvent event) {
        if (!this.isInitialized) {
            this.isInitialized = true;
            this.initHistoryList();
        }

        this.pastQuestions.add("New Question");
    }
}
