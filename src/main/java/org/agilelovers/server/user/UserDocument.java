package org.agilelovers.server.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document("sayit-users")
@Data
@Builder
@AllArgsConstructor
public class UserDocument {
    @Id
    private String id;

    @NotNull
    @NotBlank
    @Email
    @Indexed(name = "username", unique = true)
    private String username;

    @Email
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
