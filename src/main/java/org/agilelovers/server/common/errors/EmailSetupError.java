package org.agilelovers.server.common.errors;

public class EmailSetupError extends RuntimeException {
    public EmailSetupError(String host, String tls, String email, String pass) {
        super("Error setting up SMTP!\nhost: " + host + " tls: " + tls + " email: " + email + " pass: " + pass);
    }
}
