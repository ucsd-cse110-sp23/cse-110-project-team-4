package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class Question extends Prompt {
    private String entirePrompt = "RECORDING";
    private String answer = "";

    public Question() {
        this.command = Constants.QUESTION_COMMAND;
    }

    public Question(String entirePrompt) {
        this();
        this.entirePrompt = entirePrompt;
    }

    @Override
    public void setTitle(String newTitle) {
        super.setTitle(newTitle);
        this.entirePrompt = newTitle;
    }

    @Override
    public void setBody(String newBody) {
        super.setBody(newBody);
        this.answer = newBody;
    }

    @Override
    public String getTitle() {
        return this.entirePrompt;
    }

    @Override
    public String getBody() {
        return this.answer;
    }
}
