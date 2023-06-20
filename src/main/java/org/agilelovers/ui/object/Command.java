package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;
import org.agilelovers.ui.enums.CommandType;

public class Command {
    private String transcribed;
    private String command;
    private String command_arguments;

    public Command(String transcribed, String command, String command_arguments) {
        this.transcribed = transcribed;
        this.command = command;
        this.command_arguments = command_arguments;
    }

    public String getTranscribed() {
        return transcribed;
    }

    public String getCommand() {
        return command;
    }

    public String getCommand_arguments() {
        return command_arguments;
    }

    public CommandType getQueryType() {
        if (this.command == null) {
            return CommandType.INVALID;
        }
        if (this.command.equals(Constants.QUESTION_COMMAND)) {
            return CommandType.QUESTION;
        }
        if (this.command.equals(Constants.DELETE_PROMPT_COMMAND)) {
            return CommandType.DELETE_PROMPT;
        }
        if (this.command.equals(Constants.CLEAR_ALL_COMMAND)) {
            return CommandType.CLEAR_ALL;
        }
        if (this.command.equals(Constants.SETUP_EMAIL_COMMAND)) {
            return CommandType.SETUP_EMAIL;
        }
        if (this.command.equals(Constants.CREATE_EMAIL_COMMAND)) {
            return CommandType.CREATE_EMAIL;
        }
        if (this.command.equals(Constants.SEND_EMAIL_COMMAND)) {
            return CommandType.SEND_EMAIL;
        }
        return CommandType.INVALID;
    }
}