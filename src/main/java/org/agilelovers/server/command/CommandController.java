package org.agilelovers.server.command;

import org.agilelovers.server.command.errors.CommandNotFoundError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommandController {

    private final CommandRepository commands;

    public CommandController(CommandRepository commands) {
        this.commands = commands;
    }

    @GetMapping("/api/commands/{uid}")
    public List<CommandDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return commands.findAllByUserId(uid)
                .orElseThrow(() -> new CommandNotFoundError(uid, true));
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
