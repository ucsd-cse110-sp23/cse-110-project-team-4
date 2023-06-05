package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.assistant.errors.NoAudioError;
import org.agilelovers.server.command.CommandDocument;
import org.agilelovers.server.command.CommandRepository;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.question.QuestionDocument;
import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@ApiOperation("Assistant API")
public class AssistantController {
    private final UserRepository users;
    private final QuestionRepository questions;
    private final CommandRepository commands;
    private final OpenAIClient client;

    public AssistantController(UserRepository users, QuestionRepository questions, CommandRepository commands) {
        this.users = users;
        this.questions = questions;
        this.commands = commands;
        this.client = new OpenAIClient();
    }

    private boolean isValidCommand(String transcription) {
        boolean result;

        // TODO: Add proper logic to check if a string is a command or not.
        result = transcription.startsWith("command");

        return result;
    }

    @ApiOperation(value = "Ask the SayIt Assistant", notes = "Send an audio file to SayIt Assistant and it will save " +
            "it either as a command or as a question")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created question/command"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 406, message = "The audio file doesn't have any transcribe-able audio or its empty")
    })
    @PostMapping("/api/assistant/{uid}")
    public AssistantData transcribeAndSave(@RequestParam("file") @ApiParam(name = "audio file") MultipartFile file,
                                           @PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        String transcription = this.client.getTranscription(file);

        if (transcription != null && !transcription.isEmpty()) {
            transcription = transcription.toLowerCase();

            QuestionDocument question = null;
            CommandDocument command = null;

            boolean isCommand = isValidCommand(transcription);

            if (isCommand) {
                command = CommandDocument.builder()
                        .command(transcription)
                        .userId(uid)
                        .build();

                commands.save(command);
            } else {
                String answer = this.client.getAnswer(transcription);

                question = QuestionDocument.builder()
                        .question(transcription)
                        .answer(answer)
                        .userId(uid)
                        .build();

                questions.save(question);
            }

            return AssistantData.builder()
                    .command(command)
                    .question(question)
                    .isCommand(isCommand)
                    .build();
        }
        else
            throw new NoAudioError();
    }
}
