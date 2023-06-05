package org.agilelovers.server.user.errors;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() {
        super("not authorized to use this application.");
    }
}
