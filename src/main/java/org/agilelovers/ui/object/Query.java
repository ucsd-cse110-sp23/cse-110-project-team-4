package org.agilelovers.ui.object;

import org.agilelovers.ui.Constants;
import org.agilelovers.ui.enums.CommandType;

public class Query {
    private Command command;
    private Question question;
    private boolean isCommand;

    public Query(Command command, Question question, boolean isCommand) {
        this.command = command;
        this.question = question;
        this.isCommand = isCommand;
    }

    public Command getCommand() {
        return command;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isCommand() {
        return isCommand;
    }

    public CommandType getQueryType() {
        if (this.command.getCommand().equals(Constants.QUESTION_COMMAND)) {
            return CommandType.QUESTION;
        }
        if (this.command.getCommand().equals(Constants.DELETE_PROMPT_COMMAND)) {
            return CommandType.DELETE_PROMPT;
        }
        if (this.command.getCommand().equals(Constants.CLEAR_ALL_COMMAND)) {
            return CommandType.CLEAR_ALL;
        }
        if (this.command.getCommand().equals(Constants.SETUP_EMAIL_COMMAND)) {
            return CommandType.SETUP_EMAIL;
        }
        if (this.command.getCommand().equals(Constants.CREATE_EMAIL_COMMAND)) {
            return CommandType.CREATE_EMAIL;
        }
        if (this.command.getCommand().equals(Constants.SEND_EMAIL_COMMAND)) {
            return CommandType.SEND_EMAIL;
        }
        return CommandType.INVALID;
    }

}