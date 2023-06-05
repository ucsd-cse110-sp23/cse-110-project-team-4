package org.agilelovers.server.common.errors;

public class NotAuthorizedError extends RuntimeException {
    public NotAuthorizedError() {
        super("not authorized to use this application.");
    }
}
