package org.agilelovers.ui.object;


import org.agilelovers.ui.controller.MainController;

import java.util.Date;

public class Prompt {
    private Command command = null;
    private String id = "";
    private String title = "RECORDING";
    private String body = "";
    private Date createdDate = null;

    public Prompt() {
    }

    public Prompt(Command command) {
        this.command = command;
    }

    public Prompt(Command command, String id, String title, String body) {
        this.command = command;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String newTitle) {
        if (MainController.instance != null) MainController.instance.refreshLabels();
        this.title = newTitle;
    }

    public void setBody(String newAnswer) {
        if (MainController.instance != null) { MainController.instance.refreshLabels(); }
        this.body = newAnswer;
    }
    public String getTitle() {
        return this.title;
    }

    public Command getCommand() {
        return command;
    }

    public String getBody() {
        return this.body;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.command.getCommand_arguments();
    }

}
