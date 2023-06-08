package org.agilelovers.ui.object;

public class UserCredentialWithKey extends UserCredential {
    private String apiPassword;

    public UserCredentialWithKey(String username, String password, String email, String id, String apiPassword) {
        super(username, password, email, id);
        this.apiPassword = apiPassword;
    }
}
