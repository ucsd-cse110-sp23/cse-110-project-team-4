package org.agilelovers.ui.object;


import org.agilelovers.ui.controller.MainController;

public class Question extends Prompt {
    private String id = "";
    private String userId = "";
    private String promptCommand = "";
    private String response = "";

    public Question() {
        super("", "", "", "");
    }

    public Question(String id, String userId, String promptCommand, String response) {
        super(id, userId, promptCommand, response);
    }

    public String getPromptCommand() {
        return this.promptCommand;
    }

    public String getResponse() {
        return this.response;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPromptCommand(String newQuestion) {
        if (MainController.instance != null) MainController.instance.refreshLabels();
        this.promptCommand = newQuestion;
    }

    public void setResponse(String newAnswer) {
        if (MainController.instance != null) { MainController.instance.refreshLabels(); }
        this.response = newAnswer;
    }

    @Override
    public String toString() {
        return this.promptCommand;
    }
}
