package org.agilelovers.ui.object;

public class Question {
    private String question = "";
    private String answer = "";
    private String title = "";

    public Question(String title, String question, String answer) {
        this.title = title;
        this.question = question;
        this.answer = answer;
    }

    public String question() {
        return this.question;
    }

    public String answer() {
        return this.answer;
    }

    public void setAnswer(String newAnswer) {
        this.answer = newAnswer;
    }

    public void setQuestion(String newQuestion) {
        this.question = newQuestion;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
