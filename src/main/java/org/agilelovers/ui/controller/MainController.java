package org.agilelovers.ui.controller;

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

import java.io.IOException;

/**
 * Controller class for the UI.
 * This class is responsible for handling user input and updating the UI accordingly. It also handles the interaction
 * between the UI and the backend. The UI utilizes the JavaFX framework to create visual objects on the application's
 * window using labels and buttons defined in this class. The UI is defined in the Main.fxml file.
 * The backend is defined in the SayItAssistant.java file.
 * The Question object is defined in the Question.java file.
 */
public class MainController {
    /**
     * The History list. This is the list of past questions.
     */
    @FXML
    protected ListView historyList;
    /**
     * The Question label. This label displays the most recently asked or selected question.
     */
    @FXML
    protected Label questionLabel;
    /**
     * The Answer label. This label displays the answer to the most recently asked or selected question.
     */
    @FXML
    protected TextArea answerTextArea;

    /**
     * The Record button.
     */
    @FXML
    protected Button startButton;

    /**
     * A list that contains all the question objects.
     */
    protected ObservableList<Question> pastQueries = FXCollections.observableArrayList();

    public static MainController instance;

    protected boolean isRecording = false;

    @FXML
    private void initialize() throws IOException {
        System.out.println("Initializing Controller");
        answerTextArea.setEditable(false);
        pastQueries.addAll(SayItAssistant.assistant.getDatabaseQuestions());
        for (Question question : pastQueries) {
            System.out.println(question);
        }
        initHistoryList();
    }


    /**
     * Initiates the history list.
     * If there are any questions from a previous session, they will be loaded into the list.
     * If not, the list will be empty.
     *
     * Specifies the behavior of the display when a question is selected.
     * If a question is selected, the question and answer labels will be updated to reflect the selected question.
     * If not, the question and answer labels will be empty.
     */
    public void initHistoryList() {
        this.historyList.setItems(this.pastQueries);
        this.historyList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                questionLabel.setText("");
                answerTextArea.setText("");
                return;
            }
            Question currentQuestion = (Question) newValue;
            questionLabel.setText(currentQuestion.question());
            answerTextArea.setText(currentQuestion.answer());
        });
    }

    /**
     * Refreshes the answer and question labels. This method is called when the history list
     * is updated. It updates the question and answer labels to reflect the currently selected question.
     */
    public void refreshLabels() {
        Platform.runLater(() -> {
            var index = this.historyList.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                questionLabel.setText("");
                answerTextArea.setText("");
                return;
            }
            Question currentQuestion = this.pastQueries.get(index);
            questionLabel.setText(currentQuestion.question());
            answerTextArea.setText(currentQuestion.answer());
        });
    }

    /**
     * Gets history list.
     *
     * @return the history list
     */
    public ListView getHistoryList() {
        return this.historyList;
    }

    /**
     * Gets question label.
     *
     * @return the question label
     */
    public Label getQuestionLabel() {
        return this.questionLabel;
    }

    /**
     * Gets answer label.
     *
     * @return the answer label
     */
    public TextArea getAnswerTextArea() {
        return this.answerTextArea;
    }

    /**
     * Removes all questions from the history list.
     *
     * If no questions are in the list, nothing will happen.
     */
    public void clearAll() throws IOException {
        System.out.println("Clear All");
        for (Question pastQuestion : this.pastQueries) {
            SayItAssistant.assistant.deleteDatabaseQuestion(pastQuestion);
        }
        this.pastQueries.clear();
        this.historyList.getSelectionModel().select(null);
    }

    /**
     * Deletes the current selected question.
     *
     * After deleting, no question will be selected from the history list. If no
     * question is selected or in the list, nothing will happen.
     */
    public void deleteQuestion() throws IOException {
        System.out.println("Delete Question");
        if (!this.pastQueries.isEmpty()) {
            SayItAssistant.assistant.deleteDatabaseQuestion(this.pastQueries.get(
                    this.historyList.getFocusModel().getFocusedIndex())
            );
            this.pastQueries.remove(this.historyList.getFocusModel().getFocusedIndex());
            this.historyList.getSelectionModel().select(null);
            System.out.println("Successfully deleted question");
        }
    }

    /**
     * Allows the user to ask a question to SayIt Assistant.
     *
     * When "new question" button is clicked, starts a new recording for
     * the user question and changes the button label to "stop recording".
     * The "delete" and "clear all" buttons are disabled while recording.
     *
     * Clicking the "stop recording" button will stop the recording and change
     * the button label to "new question". Adds new question to history list and
     * sets as current. Re-enables the "delete" and "clear all" buttons.
     *
     * @param event event triggered by the "new question" button click
     */
    public void newQuery(ActionEvent event) {
        if (this.isRecording) {
            // wait for chatgpt to response
            // Question stopRecording()
            var currentQuestion = SayItAssistant.assistant.endRecording();
            this.pastQueries.remove(this.pastQueries.size() - 1);
            this.pastQueries.add(currentQuestion);
            this.historyList.getSelectionModel().select(this.pastQueries.size() - 1);
            // change back to new question
            this.startButton.setText("New Question");
        } else {
            this.pastQueries.add(new Question("", "RECORDING", ""));
            // call a method that starts recording
            SayItAssistant.assistant.startRecording();
            this.startButton.setText("Stop Recording");
        }
        this.isRecording = !this.isRecording;
    }

    /**
     * Runs the command specified by the user.
     * @param command string that specifies which command to run
     */
    public void runCommand(String command) {
        // logic for determining which command to run
    }
}
