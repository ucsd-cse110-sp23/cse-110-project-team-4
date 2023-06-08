package org.agilelovers.ui.object;

public class EmailConfig {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String emailPassword;
    private String smtpHost;
    private String tlsPort;

    public EmailConfig(String firstName, String lastName, String displayName, String email, String password, String smtpHost, String tlsPort) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.emailPassword = password;
        this.smtpHost = smtpHost;
        this.tlsPort = tlsPort;
    }

    public EmailConfig() {
        this.firstName = "";
        this.lastName = "";
        this.displayName = "";
        this.email = "";
        this.emailPassword = "";
        this.smtpHost = "";
        this.tlsPort = "";
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

    public String getEmailPassword() {
        return emailPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getTlsPort() {
        return tlsPort;
    }
}
