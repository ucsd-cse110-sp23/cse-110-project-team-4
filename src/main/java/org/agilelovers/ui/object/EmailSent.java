package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class EmailSent extends Prompt {
    private String command = Constants.SEND_EMAIL_COMMAND;
    private String entirePrompt;
    private String confirmationOfEmailSent;

    @Override
    public void setTitle(String newTitle) {
        this.entirePrompt = newTitle;
    }

    @Override
    public void setBody(String newBody) {
        this.confirmationOfEmailSent = newBody;
    }

    @Override
    public String getTitle() {
        return entirePrompt;
    }

    @Override
    public String getBody() {
        return confirmationOfEmailSent;
    }
}
