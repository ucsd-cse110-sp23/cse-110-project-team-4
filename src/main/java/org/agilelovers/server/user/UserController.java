package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.NotAuthorizedError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@ApiOperation("Users API")
public class UserController {

    private final UserRepository users;

    @Value("${sayit.API_SECRET}")
    private String apiPassword;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @ApiOperation(value = "Sign in", notes = "Get the User ID with a username and password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved User ID"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @GetMapping("/api/users")
    public ReducedUserDocument getUser(@RequestBody UserDocument user) {
        UserDocument foundUser = users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));

        return ReducedUserDocument.builder()
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .id(foundUser.getId())
                .build();

    }

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/api/users")
    public ReducedUserDocument createUser(@RequestBody UserDocumentSecured user) {
        if (user.getApiPassword() != null && user.getApiPassword().equals(this.apiPassword)) {
            UserDocument savedUser = users.saveUsernameAndPassword(user.getUsername(), user.getPassword());
            return ReducedUserDocument.builder()
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .id(savedUser.getId())
                    .build();
        } else throw new NotAuthorizedError();
    }

    @ApiOperation(value = "Update email", notes = "Update a user's email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a user's email"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @PutMapping("/api/users/{id}")
    public ReducedUserDocument updateEmail(@RequestBody @ApiParam(name = "emailDocument", value = "User email information") UserEmailDocument emailDoc,
                                           @PathVariable @ApiParam(name = "id", value = "User ID") String id) {
        return users.findById(id)
                .map(user -> {
                    user.setEmail(emailDoc.getEmail());
                    users.save(user);
                    return ReducedUserDocument.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .id(user.getId())
                            .build();
                }).orElseThrow(() -> new UserNotFoundError(id));
    }
}
