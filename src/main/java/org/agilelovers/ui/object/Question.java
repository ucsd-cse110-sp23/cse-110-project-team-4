package org.agilelovers.ui.object;

import org.agilelovers.ui.controller.MainController;

public class Question {
    private String id = "";
    private String userId = "";
    private String question = "";
    private String answer = "";

    public Question() {
    }

    public Question(String id, String userId, String question, String answer) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuestion(String newQuestion) {
        MainController.instance.refreshLabels();
        this.question = newQuestion;
    }

    public void setAnswer(String newAnswer) {
        MainController.instance.refreshLabels();
        this.answer = newAnswer;
    }

    @Override
    public String toString() {
        return this.question;
    }
}
