package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class EmailDraft extends Prompt {
    private String command = Constants.CREATE_EMAIL_COMMAND;
    private String entirePrompt;
    private String body;

    public EmailDraft(String transcribed) {
        super();
        entirePrompt = transcribed;
    }

    @Override
    public void setTitle(String newTitle) {
        this.entirePrompt = newTitle;
    }

    @Override
    public void setBody(String newBody) {
        this.body = newBody;
    }

    @Override
    public String getTitle() {
        return entirePrompt;
    }

    @Override
    public String getBody() {
        return body;
    }
}
