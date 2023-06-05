package org.agilelovers.ui.object;

public abstract class Prompt {
    private String id;
    private String userId;
    private String promptCommand;
    private String response;

    public Prompt(String id, String userId, String promptCommand, String response) {
        this.id = id;
        this.userId = userId;
        this.promptCommand = promptCommand;
        this.response = response;
    }

    public String getId() {
        return id;
    }
    public String getUserId() {
        return userId;
    }
    public String getPromptCommand() {
        return promptCommand;
    }

    public String getResponse() {
        return response;
    }


}
