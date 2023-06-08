package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class EmailDraft extends Prompt {
    private String entirePrompt;
    private String body;

    public EmailDraft(String transcribed) {
        this.command = Constants.SEND_EMAIL_COMMAND;
        entirePrompt = transcribed;
    }

    @Override
    public void setTitle(String newTitle) {
        super.setTitle(newTitle);
        this.entirePrompt = newTitle;
    }

    @Override
    public void setBody(String newBody) {
        super.setBody(newBody);
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
