package org.agilelovers.server.question.common.errors;

public class UserNotFoundError extends RuntimeException{
    public UserNotFoundError(String email) {
        super("could not find user " + email);
    }
}
