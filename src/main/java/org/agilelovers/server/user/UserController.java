package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.NotAuthorizedError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.user.models.ReducedUser;
import org.agilelovers.server.user.models.SecureUser;
import org.agilelovers.server.user.models.UserDocument;
import org.agilelovers.server.email.config.UserEmailConfigDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
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
    @GetMapping("/sign_in")
    public ReducedUser getUser(@RequestBody UserDocument user) {
        UserDocument foundUser = users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));

        return ReducedUser.builder()
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .id(foundUser.getId())
                .build();
    }

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/sign_up")
    public ReducedUser createUser(@RequestBody SecureUser user) {
        if (user.getApiPassword() != null && user.getApiPassword().equals(this.apiPassword)) {
            UserDocument savedUser = users.saveUsernameAndPassword(user.getUsername(), user.getPassword());
            return ReducedUser.builder()
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
    @PutMapping("/update/email/{uid}")
    public ReducedUser updateEmail(@RequestBody @ApiParam(name = "emailDocument", value = "User email information") UserEmailConfigDocument emailDoc,
                                   @PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return users.findById(uid)
                .map(user -> {
                    user.setEmail(emailDoc.getEmail());
                    user.setEmailInformation(emailDoc);
                    users.save(user);
                    return ReducedUser.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .id(user.getId())
                            .build();
                }).orElseThrow(() -> new UserNotFoundError(uid));
    }
}
