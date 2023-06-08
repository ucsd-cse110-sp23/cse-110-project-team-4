package org.agilelovers.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReturnedEmailModel {
    @ApiModelProperty(notes = "ID of original query passed on sendEmail method")
    private String sentId;

    @ApiModelProperty(notes = "Email of person we are sending draft to")
    private String recipient;

    @ApiModelProperty(notes = "Checking for 'Create Email' command")
    private String command;

    @ApiModelProperty(notes = "The prompt the user gave to send the email")
    private String entirePrompt;
}
