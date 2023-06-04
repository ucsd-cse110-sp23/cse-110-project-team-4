package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssistantData {

    @ApiModelProperty(notes = "The transcribed string returned by whisper", required = true)
    private String transcribed;

    @ApiModelProperty(notes = "The command to be run, if any")
    private String command;

    @ApiModelProperty(notes = "The arguments for the command, if any")
    private String command_arguments;
}
