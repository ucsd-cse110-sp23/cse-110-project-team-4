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
import org.agilelovers.ui.object.Query;
import org.agilelovers.ui.object.Question;
import org.agilelovers.ui.util.FrontEndAPIUtils;
import org.agilelovers.ui.util.RecordingUtils;

import java.io.IOException;

// TODO: redo documentaiton
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

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        MainController.id = id;
    }

    private static String id;

    // TODO: deal with email draft type
    /**
     * A list that contains all the question objects.
     */
    protected ObservableList<Question> pastQueries = FXCollections.observableArrayList();

    public static MainController instance;

    protected boolean isRecording = false;

    // TODO: chronological ordering
    @FXML
    private void initialize() throws IOException {
        System.out.println("Initializing Controller");
        answerTextArea.setEditable(false);
        Platform.runLater(() -> {
            try {
                FrontEndAPIUtils.fetchHistory(MainController.id);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        for (Question question : pastQueries) {
            System.out.println(question);
        }
        initHistoryList();
    }


    /**
     * Initiates the history list.
     * If there are any questions from a previous session, they will be loaded into the list.
     * If not, the list will be empty.
     * <p>
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
            questionLabel.setText(currentQuestion.getPrompt());
            answerTextArea.setText(currentQuestion.getResponse());
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
            questionLabel.setText(currentQuestion.getPrompt());
            answerTextArea.setText(currentQuestion.getResponse());
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

    public void newQuery(ActionEvent event) throws IOException {
        if (this.isRecording) {
            // wait for chatgpt to response
            // Question stopRecording()
            Platform.runLater(() -> {
                this.startButton.setDisable(true);
                Query currentQuery = null;
                try {
                    currentQuery = RecordingUtils.endRecording(MainController.id, new Question());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.err.println(currentQuery);
                try {
                    this.runCommand(currentQuery);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.startButton.setDisable(false);
            });
            this.startButton.setText("Start");
        } else {
            this.pastQueries.add(new Question("", "", "RECORDING", ""));
            // call a method that starts recording
            RecordingUtils.startRecording();
            this.startButton.setText("Stop Recording");
        }
        this.isRecording = !this.isRecording;
    }

    public void runCommand(Query query) throws IOException, InterruptedException {
        switch (query.getQueryType()) {
            case QUESTION:
                newQuestion(query.getQuestion());
                break;
            case DELETE_PROMPT:
                deleteQuery();
                break;
            case CLEAR_ALL:
                clearAll();
                break;
            case SETUP_EMAIL:
                setupEmail();
                break;
            case CREATE_EMAIL:
                createEmail();
                break;
            case SEND_EMAIL:
                sendEmail();
                break;
            default:
                throw new IllegalStateException("Please give a valid command");
        }
    }

    /**
     * Adds a new question to the history list.
     * This method is called when the user stops recording a question.
     * The new question is added to the history list and set as the current question.
     * The "new question" button label is changed to "new question".
     * The "delete" and "clear all" buttons are re-enabled.
     * The question and answer labels are updated to reflect the new question.
     * The new question is sent to the backend to be processed.
     *
     * @param currentQuestion the question to be added to the history list
     */
    public void newQuestion(Question currentQuestion) {
        this.pastQueries.remove(this.pastQueries.size() - 1);
        this.pastQueries.add(currentQuestion);
        this.historyList.getSelectionModel().select(this.pastQueries.size() - 1);
    }

    /**
     * Deletes the current selected question.
     * <p>
     * After deleting, no question will be selected from the history list. If no
     * question is selected or in the list, nothing will happen.
     */
    public void deleteQuery() throws IOException, InterruptedException {
        System.out.println("Delete Question");
        if (!this.pastQueries.isEmpty()) {
            FrontEndAPIUtils.deleteQuestion(this.pastQueries.get(this.historyList.getFocusModel().getFocusedIndex()).getId());
            this.pastQueries.remove(this.historyList.getFocusModel().getFocusedIndex());
            this.historyList.getSelectionModel().select(null);
            System.out.println("Successfully deleted question");
        }
    }

    /**
     * Removes all questions from the history list.
     * <p>
     * If no questions are in the list, nothing will happen.
     */
    public void clearAll() throws IOException, InterruptedException {
        System.out.println("Clear All");
        FrontEndAPIUtils.clearAll(MainController.id);
        this.pastQueries.clear();
        this.historyList.getSelectionModel().select(null);
    }

    private void setupEmail() {
        System.out.println("Setup Email");

    }

    private void createEmail() {
        System.out.println("Create Email");
    }

    private void sendEmail() {
        System.out.println("Send Email");
    }
}

