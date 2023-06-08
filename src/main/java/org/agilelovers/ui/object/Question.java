package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class Question extends Prompt{
    private String command = Constants.QUESTION_COMMAND;
    private String entirePrompt = "RECORDING";
    private String answer = "";

    public Question() {
    }

    public Question(String entirePrompt) {
        this.entirePrompt = entirePrompt;
    }

    @Override
    public void setTitle(String newTitle) {
        this.entirePrompt = newTitle;
    }

    @Override
    public void setBody(String newBody) {
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
