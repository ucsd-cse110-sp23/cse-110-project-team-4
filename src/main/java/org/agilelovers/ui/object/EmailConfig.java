package org.agilelovers.ui.object;

public class EmailConfig {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String password;
    private String smtpHost;
    private String tlsPort;

    public EmailConfig(String firstName, String lastName, String displayName, String email, String password, String smtpHost, String tlsPort) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.smtpHost = smtpHost;
        this.tlsPort = tlsPort;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getTlsPort() {
        return tlsPort;
    }
}
