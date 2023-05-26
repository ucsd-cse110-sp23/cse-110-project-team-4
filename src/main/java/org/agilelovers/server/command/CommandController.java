package org.agilelovers.server.command;

import org.agilelovers.server.question.QuestionDocument;
import org.agilelovers.server.question.QuestionNotFoundError;
import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommandController {
    private final UserRepository users;

    private final CommandRepository commands;

    public CommandController(UserRepository users, CommandRepository commands) {
        this.users = users;
        this.commands = commands;
    }

    @GetMapping("/api/commands/{uid}")
    public List<CommandDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return commands.findAllByUserId(uid)
                .orElseThrow(() -> new CommandNotFoundError(uid, true));
    }

    @PostMapping("/api/commands")
    public CommandDocument createQuestion(@RequestBody CommandDocument command) {
        if (users.existsById(command.getUserId()))
            return commands.save(command);
        else
            throw new CommandNotFoundError(command.getUserId(), true);
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
