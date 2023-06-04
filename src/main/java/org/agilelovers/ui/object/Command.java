package org.agilelovers.ui.object;

public class Command {
    private String id = "";
    private String userId = "";
    private String command = "";
    private String output = "";


    public Command(String id, String userId, String command, String output) {
        this.id = id;
        this.userId = userId;
        this.command = command;
        this.output = output;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
