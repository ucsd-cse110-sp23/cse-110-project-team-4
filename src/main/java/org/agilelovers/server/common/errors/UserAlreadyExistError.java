package org.agilelovers.server.common.errors;

public class UserAlreadyExistError extends RuntimeException{
    public UserAlreadyExistError(String email) {
        super("user " + email + " already exists");
    }
}
