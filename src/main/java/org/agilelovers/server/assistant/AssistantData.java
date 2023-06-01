package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.agilelovers.server.command.CommandDocument;
import org.agilelovers.server.question.QuestionDocument;

@Data
@Builder
public class AssistantData {
    @ApiModelProperty(notes = "If the response is a command or a question.")
    private boolean isCommand;

    @ApiModelProperty(notes = "Command data if the audio file contains a command, null otherwise")
    private CommandDocument command;

    @ApiModelProperty(notes = "Question data if the audio file contains a question, null otherwise")
    private QuestionDocument question;
}
