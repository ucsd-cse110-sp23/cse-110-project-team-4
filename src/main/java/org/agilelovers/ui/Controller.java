package org.agilelovers.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.agilelovers.backend.SayItAssistant;
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
    //Label answerLabel;
    TextArea answerLabel;
    @FXML
    private void initialize() {
        answerLabel.setEditable(false);
    }
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

    boolean test = false;

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

    public void refreshLabels() {
        Platform.runLater(() -> {
            var index = this.historyList.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                questionLabel.setText("");
                answerLabel.setText("");
                return;
            }
            Question currentQuestion = this.pastQuestions.get(index);
            questionLabel.setText(currentQuestion.question());
            answerLabel.setText(currentQuestion.answer());
        });
    }

    public ListView getHistoryList() {
        return this.historyList;
    }

    public Label getQuestionLabel() {
        return this.questionLabel;
    }

    /*public Label getAnswerLabel() {
        return this.answerLabel;
    }*/
    public TextArea getAnswerLabel() {
        return this.answerLabel;
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
        if (!this.pastQuestions.isEmpty()) {
            this.pastQuestions.remove(this.historyList.getFocusModel().getFocusedIndex());
            System.out.println("Succesfully deleted question");
        }
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

        if (test) {
            this.newMockQuestion(event);
        } else if (this.isRecording) {
            // wait for chatgpt to response
            // Question stopRecording()
            var currentQuestion = SayItAssistant.assistant.endRecording();
            this.pastQuestions.remove(this.pastQuestions.size() - 1);
            this.pastQuestions.add(currentQuestion);
            this.historyList.getSelectionModel().select(this.pastQuestions.size() - 1);
            // change back to new question
            this.recordButton.setText("New Question");
            this.deleteButton.setDisable(false);
            this.clearAllButton.setDisable(false);
        } else {
            this.pastQuestions.add(new Question("title" + (++i), "RECORDING", ""));
            // call a method that starts recording
            SayItAssistant.assistant.startRecording();
            this.recordButton.setText("Stop Recording");
            this.deleteButton.setDisable(true);
            this.clearAllButton.setDisable(true);
        }
        if (!test) { this.isRecording = !this.isRecording; }
    }

    // for testing ----------------------------------
    void addQuestion(Question question) {
        this.pastQuestions.add(question);
    }

    void newMockQuestion(ActionEvent event) {
        if (!this.isInitialized) {
            this.isInitialized = true;
            this.initHistoryList();
        }

        if (this.isRecording) {
            System.out.println("isRecording: " + this.isRecording);
            // wait for chatgpt to response
            // Question stopRecording()
            //var currentQuestion = new Question("temp1", "temp2", "temp3");
            Question currentQuestion = pastQuestions.get(i-1);
            this.pastQuestions.remove(this.pastQuestions.size() - 1);
            currentQuestion.setQuestion("question"+i);
            this.pastQuestions.add(currentQuestion);
            this.historyList.getSelectionModel().select(this.pastQuestions.size() - 1);
            // change back to new question
            this.recordButton.setText("New Question");
            this.deleteButton.setDisable(false);
            this.clearAllButton.setDisable(false);
        } else {
            System.out.println("isRecording: " + this.isRecording);
            this.pastQuestions.add(new Question("title" + (++i), "RECORDING", ""));
            // call a method that starts recording
            this.recordButton.setText("Stop Recording");
            this.deleteButton.setDisable(true);
            this.clearAllButton.setDisable(true);
        }
        this.isRecording = !this.isRecording;
    }

}
