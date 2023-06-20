package org.agilelovers.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfigModel {
    @ApiModelProperty(notes = "first name of user during email setup")
    private String firstName;

    @ApiModelProperty(notes = "last name of user during email setup")
    private String lastName;

    @ApiModelProperty(notes = "Email of the user inputted during email setup")
    private String email;

    @ApiModelProperty(notes = "Email password of the email given by user in email setup")
    private String emailPassword;

    @ApiModelProperty(notes = "Display name to be shown on email sent, given in email setup")
    private String displayName;

    @ApiModelProperty(notes = "SmtpHost of email given during email setup")
    private String smtpHost;

    @ApiModelProperty(notes = "tlsPort of email given during email setup")
    private String tlsPort;
}
