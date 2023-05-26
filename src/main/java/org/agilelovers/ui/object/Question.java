package org.agilelovers.ui.object;

import org.agilelovers.ui.MainController;

public class Question {
     private String question = "";
     private String answer = "";
     private String title = "";

    public Question(String title, String question, String answer) {
        this.title = title;
        this.question = question;
        this.answer = answer;
    }

    public Question() {}

    public String question() {
        return this.question;
    }

    public String answer() {
        return this.answer;
    }

    public void setAnswer(String newAnswer) {
        MainController.instance.refreshLabels();
        this.answer = newAnswer;
    }

    public void setQuestion(String newQuestion) {
        MainController.instance.refreshLabels();
        this.question = newQuestion;
    }

    public void setTitle(String newTitle) {
        MainController.instance.getHistoryList().refresh();
        this.title = newTitle;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
