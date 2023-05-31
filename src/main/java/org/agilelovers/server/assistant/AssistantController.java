package org.agilelovers.server.assistant;

import org.agilelovers.server.command.CommandDocument;
import org.agilelovers.server.command.CommandRepository;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.agilelovers.server.question.QuestionDocument;
import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.assistant.errors.NoAudioError;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
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

    @PostMapping("/api/assistant/{uid}")
    public AssistantData save(@RequestParam("file") MultipartFile file, @PathVariable String uid) {
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
