package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;

public class ReturnedEmail extends Prompt {
    private String entirePrompt;
    private String confirmationOfEmailSent;

    public ReturnedEmail(String transcribed) {
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
