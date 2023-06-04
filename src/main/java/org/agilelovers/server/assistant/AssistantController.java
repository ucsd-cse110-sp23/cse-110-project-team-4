package org.agilelovers.server.assistant;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.common.CommandIdentifier;
import org.agilelovers.common.CommandType;
import org.agilelovers.server.assistant.errors.NoAudioError;
import org.agilelovers.server.common.OpenAIClient;
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
    private final OpenAIClient client;

    public AssistantController(UserRepository users) {
        this.users = users;
        this.client = new OpenAIClient();
    }

    // Question -> return "QUESTION" and the question in command_arguments
    // Delete Prompt -> return "DELETE_PROMPT"
    // Clear all -> return "CLEAR_ALL"

    // Setup email is done purely on the frontend -> return command "SETUP_EMAIL"
    // Create email -> Send command "CREATE_EMAIL" & return the command_arguments after the command in the transcription,
    // then combine it back in the Email creation endpoint.
    // Send email -> Send command "SEND_EMAIL" & return the email it provided (use email regex)

    private String getCommand(String transcription) {
        if (transcription.startsWith(CommandIdentifier.QUESTION_COMMAND))
            return CommandType.ASK_QUESTION;
        else if (transcription.equals(CommandIdentifier.DELETE_PROMPT_COMMAND))
            return CommandType.DELETE_PROMPT;
        else if (transcription.equals(CommandIdentifier.CLEAR_ALL_COMMAND))
            return CommandType.CLEAR_ALL;
        else if (transcription.equals(CommandIdentifier.SETUP_EMAIL_COMMAND))
            return CommandType.SETUP_EMAIL;
        else if (transcription.startsWith(CommandIdentifier.CREATE_EMAIL_COMMAND))
            return CommandType.CREATE_EMAIL;
        else if (transcription.startsWith(CommandIdentifier.SEND_EMAIL_COMMAND))
            return CommandType.SEND_EMAIL;
        else return null;
    }

    private String getCommandArguments(String command, String transcription) {
        if (command == null ||
                command.equals(CommandType.SETUP_EMAIL) ||
                command.equals(CommandType.DELETE_PROMPT) ||
                command.equals(CommandType.CLEAR_ALL)) return null;

        String result;
        int starting_index = switch (command) {
            case CommandType.ASK_QUESTION -> CommandIdentifier.QUESTION_COMMAND.length() - 1;
            case CommandType.SEND_EMAIL -> CommandIdentifier.SEND_EMAIL_COMMAND.length() - 1;
            case CommandType.CREATE_EMAIL -> CommandIdentifier.CREATE_EMAIL_COMMAND.length() - 1;
            default -> 0;
        };

        if (starting_index >= transcription.length() - 1) return null;

        while (transcription.charAt(starting_index + 1) != ' ') starting_index++;

        result = transcription.substring(starting_index + 1).strip();

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

            String command = getCommand(transcription);
            String command_arguments = getCommandArguments(command, transcription);

            return AssistantData.builder()
                    .transcribed(transcription)
                    .command(command)
                    .command_arguments(command_arguments)
                    .build();
        }
        else
            throw new NoAudioError();
    }
}
