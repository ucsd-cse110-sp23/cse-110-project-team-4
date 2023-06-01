package org.agilelovers.server.common.errors;

public class UserDuplicateError extends Throwable {
    public UserDuplicateError(String email) {
        throw new RuntimeException("user " + email + " already exists");
    }
}
