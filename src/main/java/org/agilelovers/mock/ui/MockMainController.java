package org.agilelovers.mock.ui;

import javafx.event.ActionEvent;
import org.agilelovers.ui.MainController;
import org.agilelovers.ui.object.Question;

/*
 * This class is used to test the Controller class; more specifically, it is used to test the purpose of the newQuestion() method.
 * We don't want to test the newQuestion() method directly, because it calls the endRecording() method which calls
 * the getAnswer() method within the backend. This would call the OpenAI API, which we don't want to do since it would consume
 * tokens every time we want to test our class(es).
 */
public class MockMainController extends MainController {

    public boolean getAnswer = false;

    public void addQuestion(Question question) {
        this.pastQuestions.add(question);
    }

    @Override
    public void newQuestion(ActionEvent event) {
        System.out.println("isRecording: " + this.isRecording);
        System.out.println("pastQuestions: " + this.pastQuestions);
        if (this.isRecording) {
            Question currentQuestion = pastQuestions.get(pastQuestions.size() - 1);
            currentQuestion.setTitle("title");
            this.pastQuestions.remove(this.pastQuestions.size() - 1);
            currentQuestion.setQuestion("question");
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
}
