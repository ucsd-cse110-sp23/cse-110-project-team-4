package org.agilelovers.server.common.errors;

public class EmailAuthenticationError extends RuntimeException {
    public EmailAuthenticationError(String email, String password) {
        super("Error authentication to SMTP!\n email: " + email + " password: " + password);
    }
}
