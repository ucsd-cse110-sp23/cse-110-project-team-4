package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;
@RestController
@ApiOperation("Users API")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @ApiOperation(value = "Sign in", notes = "Get the User ID with a username and password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved User ID"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @GetMapping("/api/users")
    public UserDocument getUser(@RequestBody UserDocument user) {
        return users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));
    }

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/api/users")
    public UserDocument createUser(@RequestBody UserDocument user) {
        return users.saveUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    @ApiOperation(value = "Update email", notes = "Update a user's email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a user's email"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @PutMapping("/api/users/{id}")
    public UserDocument updateEmail(@RequestBody @ApiParam(name = "email", value = "New email") String email,
                                    @PathVariable @ApiParam(name = "id", value = "User ID") String id) {
        return users.findById(id)
                .map(user -> {
                    user.setEmail(email);
                    return users.save(user);
                }).orElseThrow(() -> new UserNotFoundError(id));
    }
}
