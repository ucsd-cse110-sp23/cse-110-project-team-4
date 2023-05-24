package org.agilelovers.server.user;

public class UserNotFoundException extends RuntimeException{
    UserNotFoundException(String email) {
        super("Could not find user " + email);
    }
}
