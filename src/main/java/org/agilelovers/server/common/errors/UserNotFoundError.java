package org.agilelovers.server.common.errors;

public class UserNotFoundError extends IllegalArgumentException{
    public UserNotFoundError(String email) {
        super("could not find user " + email);
    }
}
