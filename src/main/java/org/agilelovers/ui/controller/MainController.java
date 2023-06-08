package org.agilelovers.ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.MainApplication;
import org.agilelovers.ui.enums.SceneType;
import org.agilelovers.ui.object.*;
import org.agilelovers.ui.util.FrontEndAPIUtils;
import org.agilelovers.ui.util.RecordingUtils;

import java.io.IOException;

// TODO: redo documentation
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

    @FXML
    protected Label recordingLabel;

    private static String uid;

    /**
     * A list that contains all the prompt objects.
     */
    protected ObservableList<Prompt> pastPrompts = FXCollections.observableArrayList();

    public static MainController instance;

    protected boolean isRecording = false;

    private boolean areQuestionsLoaded = false;
    private boolean areEmailsLoaded = false;
    private boolean areSentEmailsLoaded = false;

    @FXML
    private void initialize() {
        instance = this;
        System.out.println("Initializing Main Controller");
        if (MainController.uid == null) {
            throw new NullPointerException("MainController.uid is null");
        }
        answerTextArea.setEditable(false);
        Platform.runLater(() -> {
            try {
                pastPrompts.addAll(FrontEndAPIUtils.fetchPromptHistory(Constants.QUESTION_COMMAND, MainController.uid));
                this.areQuestionsLoaded = true;
                if (this.areEmailsLoaded && this.areSentEmailsLoaded) {
                    initHistoryList();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Platform.runLater(() -> {
            try {
                pastPrompts.addAll(FrontEndAPIUtils.fetchPromptHistory(Constants.CREATE_EMAIL_COMMAND, MainController.uid));
                this.areEmailsLoaded = true;
                if (this.areQuestionsLoaded && this.areSentEmailsLoaded) {
                    initHistoryList();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Platform.runLater(() -> {
            try {
                pastPrompts.addAll(FrontEndAPIUtils.fetchPromptHistory(Constants.SEND_EMAIL_COMMAND, MainController.uid));
                this.areSentEmailsLoaded = true;
                if (this.areQuestionsLoaded && this.areEmailsLoaded) {
                    initHistoryList();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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
        pastPrompts.forEach((prompt) -> {
            System.out.print(prompt.getTitle() + " ");
            System.out.println(prompt.getCreatedDate());
        });
        this.pastPrompts.sort(null);
        this.historyList.setItems(this.pastPrompts);
        this.historyList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                questionLabel.setText("");
                answerTextArea.setText("");
                return;
            }
            Prompt currentPrompt = (Prompt) newValue;
            questionLabel.setText(currentPrompt.getTitle());
            answerTextArea.setText(currentPrompt.getBody());
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
            Prompt currentPrompt = this.pastPrompts.get(index);
            questionLabel.setText(currentPrompt.getTitle());
            answerTextArea.setText(currentPrompt.getBody());
        });
    }

    public void refreshHistoryList() {
        Platform.runLater(() -> this.historyList.setItems(this.pastPrompts));
    }

    public void setRecordingLabel(boolean isRecording) {
        this.recordingLabel.setText(isRecording ? "Recording..." : "");
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

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        MainController.uid = uid;
    }

    @FXML
    private void newQuery(ActionEvent event) {
        if (this.isRecording) {
            // wait for ChatGPT to respond
            // Question stopRecording()
            Platform.runLater(() -> {
                try {
                    RecordingUtils.endRecording(this, MainController.uid);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Command currentCommand;
                try {
                    currentCommand = FrontEndAPIUtils.sendAudio(MainController.uid);
                    this.runCommand(currentCommand);
                    this.historyList.setDisable(false);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    this.startButton.setText("Start");
                }
            });
        } else {
            this.historyList.setDisable(true);
            // call a method that starts recording
            RecordingUtils.startRecording(this);
            this.startButton.setText("Stop Recording");
        }
        this.isRecording = !this.isRecording;
    }

    private void runCommand(Command command) throws IOException, InterruptedException {
        switch (command.getQueryType()) {
            case QUESTION -> newQuestion(command);
            case DELETE_PROMPT -> deletePrompt();
            case CLEAR_ALL -> clearAll();
            case SETUP_EMAIL -> setupEmail();
            case CREATE_EMAIL -> createEmail(command);
            case SEND_EMAIL -> sendEmail(command);
            default -> invalidCommand();
        }
    }

    private void invalidCommand() {
        System.err.println("Invalid command");
        this.historyList.getSelectionModel().select(this.pastPrompts.size() - 1);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid Command");
        alert.setContentText("""
                Please give a valid command in the form of one of the following:
                1. "Question: [question prompt]"
                2. "Delete prompt"
                3. "Clear all"
                4. "Setup email"
                5. "Create email [email prompt]"
                6. "Send email to [email address]"
                """);
        alert.show();
    }

    /**
     * Adds a new question to the history list.
     * This method is called when the user stops recording a question.
     * The new question is added to the history list and set as the current question.
     * The "new question" button label is changed to "new question".
     * The "delete" and "clear all" buttons are re-enabled.
     * The question and answer labels updated to reflect the new question.
     * The new question sent to the backend to be processed.
     *
     * @param command the command containing question to be answered and added to prompt history
     */
    private void newQuestion(Command command) throws IOException, InterruptedException {
        System.out.println("New Question");
        Prompt currentPrompt = new Question(command.getTranscribed());

        createPrompt(command, currentPrompt);
    }

    /**
     * Deletes the current selected question.
     * <p>
     * After deleting, no question will be selected from the history list. If no
     * question is selected or in the list, nothing will happen.
     */
    private void deletePrompt() throws IOException, InterruptedException {
        System.out.println("Delete Prompt");
        System.out.println("Current selection: " + this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()).getBody());
        if (this.pastPrompts.isEmpty()) return;
        if (this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()) == null) {
            noPromptSelectedError();
            return;
        }

        Platform.runLater(() -> {
            try {
                System.out.println("Current prompt to delete: " + this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()).getBody());
                FrontEndAPIUtils.deletePrompt(this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()));
                this.pastPrompts.remove(this.historyList.getFocusModel().getFocusedIndex());
                this.historyList.getSelectionModel().select(null);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Successfully deleted question");
    }

    private void noPromptSelectedError() {
        System.err.println("No prompt selected");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("No prompt Selected");
        alert.setContentText("Given command requires you to select a prompt");
        alert.show();
    }
    /**
     * Removes all questions from the history list.
     * <p>
     * If no questions are in the list, nothing will happen.
     */
    private void clearAll() throws IOException, InterruptedException {
        System.out.println("Clear All");

        Platform.runLater(() -> {
            try {
                FrontEndAPIUtils.clearAll(Constants.QUESTION_COMMAND, MainController.uid);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Platform.runLater(() -> {
            try {
                FrontEndAPIUtils.clearAll(Constants.CREATE_EMAIL_COMMAND, MainController.uid);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Platform.runLater(() -> {
            try {
                FrontEndAPIUtils.clearAll(Constants.SEND_EMAIL_COMMAND, MainController.uid);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.pastPrompts.clear();
        this.historyList.getSelectionModel().select(null);
    }

    private void setupEmail() throws IOException {
        System.out.println("Setup Email");
        SceneChanger.getInstance().switchScene(MainApplication.getInstance().getCurrentStage(), SceneType.EMAIL_SETUP_UI);
    }

    private void createEmail(Command command) {
        System.out.println("New Question");
        Prompt currentPrompt = new EmailDraft(command.getTranscribed());

        createPrompt(command, currentPrompt);
    }

    private void createPrompt(Command command, Prompt currentPrompt) {
        System.out.println("Create Prompt");
        Platform.runLater(() -> {
            try {
                FrontEndAPIUtils.newPrompt(command, currentPrompt, MainController.uid);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.pastPrompts.add(currentPrompt);
        this.historyList.getSelectionModel().select(this.pastPrompts.size() - 1);
    }

    private void sendEmail(Command command) {
        System.out.println("Send Email");
        Prompt selectedPrompt = this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex());
        if (this.pastPrompts.isEmpty()) return;
        if (selectedPrompt == null) {
            noPromptSelectedError();
            return;
        }
        System.out.println(selectedPrompt.getCommand());
        Prompt currentPrompt = new ReturnedEmail(command.getTranscribed());

        Platform.runLater(() -> {
            try {
                System.out.println("Current selection: " + this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()).getCommand());
                FrontEndAPIUtils.sendEmail(currentPrompt, command, selectedPrompt.getCommand(),
                        this.pastPrompts.get(this.historyList.getFocusModel().getFocusedIndex()),
                        MainController.uid);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.pastPrompts.add(currentPrompt);
        this.historyList.getSelectionModel().select(this.pastPrompts.size() - 1);
    }
}

