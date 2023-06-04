package org.agilelovers.ui.object;


import org.agilelovers.ui.controller.MainController;

public class Question {
    private String id = "";
    private String userId = "";
    private String queryType = "";
    private String prompt = "";
    private String response = "";

    public Question() {
    }

    public Question(String id, String userId, String prompt, String response) {
        this.id = id;
        this.userId = userId;
        this.prompt = prompt;
        this.response = response;
    }

    public String getPrompt() {
        return this.prompt;
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

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPrompt(String newQuestion) {
        if (MainController.instance != null) MainController.instance.refreshLabels();
        this.prompt = newQuestion;
    }

    public void setResponse(String newAnswer) {
        if (MainController.instance != null) { MainController.instance.refreshLabels(); }
        this.response = newAnswer;
    }

    @Override
    public String toString() {
        return this.prompt;
    }
}
