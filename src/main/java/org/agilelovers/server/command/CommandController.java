package org.agilelovers.server.command;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.command.errors.CommandNotFoundError;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiOperation("Commands API")
public class CommandController {

    private final CommandRepository commands;

    public CommandController(CommandRepository commands) {
        this.commands = commands;
    }

    @ApiOperation(value = "Get all commands", notes = "Get all the commands corresponding to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got all commands"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @GetMapping("/api/commands/{uid}")
    public List<CommandDocument> getAllQuestionsFromUserId(@PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return commands.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @ApiOperation(value = "Update a command", notes = "Update the output of a command in the database for persistence")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the command"),
            @ApiResponse(code = 404, message = "Command not found")
    })
    @PutMapping("/api/commands/{id}")
    public CommandDocument updateCommandResponse(@PathVariable @ApiParam(name = "id", value = "Command ID") String id,
                                                 @RequestBody @ApiParam(name = "output", value = "Command Output") String output) {
        return commands.findById(id).map(command -> {
            command.setOutput(output);
            return commands.save(command);
        }).orElseThrow(() -> new CommandNotFoundError(id, false));
    }

    @ApiOperation(value = "Delete a command", notes = "Delete a command")
    @DeleteMapping("/api/commands/delete/{id}")
    public void deleteQuestion(@PathVariable @ApiParam(name = "id", value = "Command ID") String id) {
        commands.deleteById(id);
    }

    @ApiOperation(value = "Delete all commands", notes = "Delete all commands by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all commands by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/api/commands/delete-all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        commands.deleteAll(commands.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }
}
