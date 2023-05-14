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

import java.io.IOException;

/**
 * Controller class for the UI.
 * This class is responsible for handling user input and updating the UI accordingly. It also handles the interaction
 * between the UI and the backend. The UI utilizes the JavaFX framework to create visual objects on the application's
 * window using labels and buttons defined in this class. The UI is defined in the Main.fxml file.
 * The backend is defined in the SayItAssistant.java file.
 * The Question object is defined in the Question.java file.
 */
public class Controller {
    /**
     * The History list. This is the list of past questions.
     */
    @FXML
    ListView historyList;
    /**
     * The Question label. This label displays the most recently asked or selected question.
     */
    @FXML
    Label questionLabel;
    /**
     * The Answer label. This label displays the answer to the most recently asked or selected question.
     */
    @FXML
    TextArea answerTextArea;

    @FXML
    private void initialize() throws IOException {
        System.out.println("Initializing Controller");
        answerTextArea.setEditable(false);
        pastQuestions.addAll(SayItAssistant.assistant.getDatabaseQuestions());
        for (Question question : pastQuestions) {
            System.out.println(question);
        }
        initHistoryList();
    }

    /**
     * The Record button.
     */
    @FXML
    Button recordButton;
    /**
     * The Delete button.
     */
    @FXML
    Button deleteButton;
    /**
     * The Clear all button.
     */
    @FXML
    Button clearAllButton;

    ObservableList<Question> pastQuestions = FXCollections.observableArrayList();
    private boolean isInitialized = false;

    public static Controller instance;

    boolean isRecording = false;

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
        this.historyList.setItems(this.pastQuestions);
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
            Question currentQuestion = this.pastQuestions.get(index);
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
     *
     * @param event event triggered by the "clear all" button click
     */
    public void clearAll(ActionEvent event) throws IOException {
        System.out.println("Clear All");
        for (Question pastQuestion : this.pastQuestions) {
            SayItAssistant.assistant.deleteDatabaseQuestion(pastQuestion);
        }
        this.pastQuestions.clear();
        this.historyList.getSelectionModel().select(null);
    }

    /**
     * Deletes the current selected question.
     *
     * After deleting, no question will be selected from the history list. If no
     * question is selected or in the list, nothing will happen.
     * TODO: implement this
     *
     * @param event event triggered by the "delete" button click
     */
    public void deleteQuestion(ActionEvent event) throws IOException {
        System.out.println("Delete Question");
        if (!this.pastQuestions.isEmpty()) {
            SayItAssistant.assistant.deleteDatabaseQuestion(this.pastQuestions.get(
                    this.historyList.getFocusModel().getFocusedIndex())
            );
            this.pastQuestions.remove(this.historyList.getFocusModel().getFocusedIndex());
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
     * TODO: move initialization to different method
     *
     * @param event event triggered by the "new question" button click
     */
    public void newQuestion(ActionEvent event) {
        if (!this.isInitialized) {
            this.isInitialized = true;
            this.initHistoryList();
        }

        if (this.isRecording) {
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
            this.pastQuestions.add(new Question("", "RECORDING", ""));
            // call a method that starts recording
            SayItAssistant.assistant.startRecording();
            this.recordButton.setText("Stop Recording");
            this.deleteButton.setDisable(true);
            this.clearAllButton.setDisable(true);
        }
        this.isRecording = !this.isRecording;
    }
}
