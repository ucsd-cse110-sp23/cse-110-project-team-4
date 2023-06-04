package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.agilelovers.server.command.CommandDocument;
import org.agilelovers.server.question.QuestionDocument;

@Data
@Builder
public class AssistantData {

    private String transcribed;

    private String command;

    private String command_arguments;
}
