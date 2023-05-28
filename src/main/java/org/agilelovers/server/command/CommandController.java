package org.agilelovers.server.command;

import org.agilelovers.server.command.errors.CommandNotFoundError;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.NoAudioError;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CommandController {
    private final UserRepository users;

    private final CommandRepository commands;
    private final OpenAIClient client;

    public CommandController(UserRepository users, CommandRepository commands) {
        this.users = users;
        this.commands = commands;
        this.client = new OpenAIClient();
    }

    @GetMapping("/api/commands/{uid}")
    public List<CommandDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return commands.findAllByUserId(uid)
                .orElseThrow(() -> new CommandNotFoundError(uid, true));
    }

    @PostMapping("/api/commands/{uid}")
    public CommandDocument createCommand(@RequestParam("file") MultipartFile file, @PathVariable String uid) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        String command = this.client.getTranscription(file);

        if (command != null && !command.isEmpty()) {

            CommandDocument commandDocument = CommandDocument.builder()
                    .command(command)
                    .userId(uid)
                    .build();

            return commands.save(commandDocument);
        } else {
            throw new NoAudioError();
        }
    }

    @PutMapping("/api/commands/{id}")
    public CommandDocument updateCommandResponse(@PathVariable String id, @RequestBody String output) {
        return commands.findById(id).map(command -> {
            command.setOutput(output);
            return commands.save(command);
        }).orElseThrow(() -> new CommandNotFoundError(id, false));
    }

    @DeleteMapping("/api/commands/delete/{id}")
    public void deleteQuestion(@PathVariable String id) {
        commands.deleteById(id);
    }

    @DeleteMapping("/api/commands/delete-all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        commands.deleteAll(commands.findAllByUserId(uid)
                .orElseThrow(() -> new CommandNotFoundError(uid, true)));
    }
}
