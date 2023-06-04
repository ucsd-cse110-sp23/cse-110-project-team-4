package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssistantData {

    private String transcribed;

    private String command;

    private String command_arguments;
}
