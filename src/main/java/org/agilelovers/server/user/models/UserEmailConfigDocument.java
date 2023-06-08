package org.agilelovers.server.user.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Document("sayit-emailconfig")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailConfigDocument {

    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "ID Of User this email information relate to")
    private String userID;

    @ApiModelProperty(notes = "first name of user during email setup")
    @NotEmpty
    private String firstName;

    @ApiModelProperty(notes = "last name of user during email setup")
    @NotEmpty
    private String lastName;

    @ApiModelProperty(notes = "Email of the user inputted during email setup")
    @NotEmpty
    @Email
    private String email;

    @ApiModelProperty(notes = "Email password of the email given by user in email setup")
    @NotEmpty
    private String emailPassword;

    @ApiModelProperty(notes = "Display name to be shown on email sent, given in email setup")
    @Indexed(name = "display name")
    @NotNull
    private String displayName;

    @ApiModelProperty(notes = "SmtpHost of email given during email setup")
    @NotEmpty
    private String smtpHost;

    @ApiModelProperty(notes = "tlsPort of email given during email setup")
    @NotEmpty
    private String tlsPort;
}
