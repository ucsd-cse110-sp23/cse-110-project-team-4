package org.agilelovers.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.agilelovers.ui.object.Question;

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
    @FXML
    Button recordButton;
    @FXML
    Button deleteButton;
    @FXML
    Button clearAllButton;


    private ObservableList<Question> pastQuestions = FXCollections.observableArrayList();
    private boolean isInitialized = false;
    private int i = 0;
    private boolean isRecording = false;

    public void initHistoryList() {
        this.historyList.setItems(this.pastQuestions);
        this.historyList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                questionLabel.setText("");
                answerLabel.setText("");
                return;
            }
            Question currentQuestion = (Question) newValue;
            questionLabel.setText(currentQuestion.question());
            answerLabel.setText(currentQuestion.answer());
        });
    }

    /**
     * Removes all the old questions.
     * TODO: implement this
     *
     * @param event action event
     */
    public void clearAll(ActionEvent event) {
        System.out.println("Clear All");
        this.pastQuestions.clear();
    }

    /**
     * Delete question.
     *
     * TODO: implement this
     * @param event the event
     */
    public void deleteQuestion(ActionEvent event) {
        System.out.println("Delete Question");
        if (!this.pastQuestions.isEmpty())
            this.pastQuestions.remove(this.historyList.getFocusModel().getFocusedIndex());
    }

    /**
     * New question.
     * TODO: implement this
     *
     * @param event the event
     */
    public void newQuestion(ActionEvent event) {
        if (!this.isInitialized) {
            this.isInitialized = true;
            this.initHistoryList();
        }

        if (this.isRecording) {
            this.deleteButton.setDisable(false);
            this.clearAllButton.setDisable(false);
            // change back to new question
            // wait for chatgpt to response
            // Question stopRecording()
            this.recordButton.setText("New Question");
        } else {
            this.pastQuestions.add(new Question("title" + (++i), "Question" + i, "answer" + i));
            this.historyList.getSelectionModel().select(this.pastQuestions.size() - 1);
            // call a method that starts recording
            this.recordButton.setText("Stop Recording");
            this.deleteButton.setDisable(true);
            this.clearAllButton.setDisable(true);
        }
        this.isRecording = !this.isRecording;
    }
}
