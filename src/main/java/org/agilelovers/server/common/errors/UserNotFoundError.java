package org.agilelovers.server.common.errors;

public class UserNotFoundError extends RuntimeException{
    public UserNotFoundError(String email) {
        super("could not find user " + email);
    }
}
