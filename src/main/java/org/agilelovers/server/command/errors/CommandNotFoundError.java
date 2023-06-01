package org.agilelovers.server.command.errors;

public class CommandNotFoundError extends RuntimeException {
    public CommandNotFoundError(String id, boolean isUserId) {
        super("Could not find command " + (isUserId ? "<user_id> " : "") + id);
    }
}

