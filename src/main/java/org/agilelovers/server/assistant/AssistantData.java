package org.agilelovers.server.assistant;

import lombok.Builder;
import lombok.Data;
import org.agilelovers.server.command.CommandDocument;
import org.agilelovers.server.question.QuestionDocument;

@Data
@Builder
public class AssistantData {
    private boolean isCommand;
    private CommandDocument command;
    private QuestionDocument question;
}
