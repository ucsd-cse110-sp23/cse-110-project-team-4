package org.agilelovers.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.agilelovers.backend.MockDatabase;
import org.agilelovers.ui.object.Question;

import java.io.IOException;

/**
 * This class is used to test the Controller class; more specifically, it is used to test the purpose of the newQuestion() method.
 * We don't want to test the newQuestion() method directly, because it calls the endRecording() method which calls
 * the getAnswer() method within the backend. This would call the OpenAI API, which we don't want to do since it would consume
 * tokens every time we want to test our class(es).
 */
public class MockController extends Controller {

    private static final MockDatabase mockDatabase;

    static {
        try {
            mockDatabase = new MockDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getAnswer = false;

    /**
     * addQuestion(...) differs from superclass because we need some way to actually add to pastQuestions(...)
     * outside of the controller. To achieve this, we create a method so that we can invoke this to happen.
     *
     * @param question : the question to add to "pastQuestions" list
     */
    public void addQuestion(Question question) {
        Platform.runLater(() -> this.pastQuestions.add(question));
    }

    /**
     * Overriding newQuestion(...) method from superclass. We invoke a mock "newQuestion" method so that we can add fake
     * titles and questions and answers to the controller and adjust the UI accordingly.
     *
     * @param event : event triggered by the "new question" button click
     */
    @Override
    public void newQuestion(ActionEvent event) {
        System.out.println("isRecording: " + this.isRecording);
        System.out.println("pastQuestions: " + this.pastQuestions);
        if (this.isRecording) {
            Question currentQuestion = pastQuestions.get(pastQuestions.size() - 1);
            currentQuestion.setTitle("title");
            this.pastQuestions.remove(this.pastQuestions.size() - 1);
            currentQuestion.setQuestion("question");
            currentQuestion.setAnswer("answer");
            this.pastQuestions.add(currentQuestion);
            this.historyList.getSelectionModel().select(this.pastQuestions.size() - 1);
            // change back to new question
            this.recordButton.setText("New Question");
            this.deleteButton.setDisable(false);
            this.clearAllButton.setDisable(false);
        } else {
            this.pastQuestions.add(new Question("", "RECORDING", ""));
            // call a method that starts recording
            this.recordButton.setText("Stop Recording");
            this.deleteButton.setDisable(true);
            this.clearAllButton.setDisable(true);
        }
        this.isRecording = !this.isRecording;
    }


    /**
     * mock controller gets its own mockdatabase in order to test persistence
     */
    @FXML
    void initialize() {
        System.out.println("Initializing Controller");
        answerTextArea.setEditable(false);
        pastQuestions.addAll(mockDatabase.obtainQuestions());
        for (Question question : pastQuestions) {
            System.out.println(question);
        }
        initHistoryList();
    }
}
