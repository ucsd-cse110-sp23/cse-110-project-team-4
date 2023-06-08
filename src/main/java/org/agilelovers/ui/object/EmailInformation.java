package org.agilelovers.ui.object;

public class EmailInformation {
    private String command;
    private String entirePrompt;
    private String recipient;
    private String sentId;

    public EmailInformation(String command, String entirePrompt, String recipient, String sentId) {
        this.command = command;
        this.entirePrompt = entirePrompt;
        this.recipient = recipient;
        this.sentId = sentId;
    }
}
