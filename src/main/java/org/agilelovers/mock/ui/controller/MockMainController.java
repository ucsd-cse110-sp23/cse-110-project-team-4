package org.agilelovers.mock.ui.controller;

import javafx.event.ActionEvent;
import org.agilelovers.ui.controller.MainController;
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
        this.pastQueries.add(question);
    }

    @Override
    public void newQuery(ActionEvent event) {
        System.out.println("isRecording: " + this.isRecording);
        System.out.println("pastQuestions: " + this.pastQueries);
        if (this.isRecording) {
            Question currentQuestion = pastQueries.get(pastQueries.size() - 1);
            currentQuestion.setTitle("title");
            this.pastQueries.remove(this.pastQueries.size() - 1);
            currentQuestion.setQuestion("question");
            this.pastQueries.add(currentQuestion);
            this.historyList.getSelectionModel().select(this.pastQueries.size() - 1);
            // change back to new question
            this.startButton.setText("New Question");
        } else {
            this.pastQueries.add(new Question("", "RECORDING", ""));
            // call a method that starts recording
            this.startButton.setText("Stop Recording");
        }
        this.isRecording = !this.isRecording;
    }
}
