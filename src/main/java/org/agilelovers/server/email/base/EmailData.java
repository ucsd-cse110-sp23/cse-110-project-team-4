package org.agilelovers.server.email.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class EmailData {

    @ApiModelProperty(notes = "User ID associated with this email", required = true)
    @NotNull
    @NotBlank
    private String userId;

    @ApiModelProperty(notes = "ID of original query passed on sendEmail method")
    @NotNull
    @NotBlank
    private String sentId;

    @ApiModelProperty(notes = "Timestamp this question was created at")
    @CreatedDate
    private Date createdDate;

    @ApiModelProperty(notes = "Email of person we are sending draft to")
    @Email
    private String recipient;

    @ApiModelProperty(notes = "Checking for 'Create Email' command")
    @NotNull
    @NotBlank
    private String command;

    @ApiModelProperty(notes = "The email prompt used to generate the body")
    @NotBlank
    private String entirePrompt;
}
