package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.common.documents.EmailConfigDocument;
import org.agilelovers.common.documents.UserDocument;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.common.models.UserModel;
import org.agilelovers.server.common.errors.NotAuthorizedError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.common.models.ReducedUserModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.email.config.EmailConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@ApiOperation("Users API")
public class UserController {

    private final UserRepository users;
    private final EmailConfigRepository emailConfigs;

    @Value("${sayit.API_SECRET}")
    private String apiPassword;

    public UserController(UserRepository users, EmailConfigRepository configs) {
        this.users = users;
        this.emailConfigs = configs;
    }

    @ApiOperation(value = "Sign in", notes = "Get the User ID with a username and password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved User ID"),
            @ApiResponse(code = 404, message = "User not found"),
    })
    @GetMapping("/sign_in")
    public ReducedUserModel getUser(@RequestBody UserModel user) {
        UserDocument foundUser = users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));

        return ReducedUserModel.builder()
                .username(foundUser.getUsername())
                .id(foundUser.getId())
                .build();
    }

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/sign_up")
    public ReducedUserModel createUser(@RequestBody SecureUserModel user) {
        if (user.getApiPassword() != null && user.getApiPassword().equals(this.apiPassword)) {
            UserDocument savedUser = users.saveUsernameAndPassword(user.getUsername(), user.getPassword());

            EmailConfigDocument emailConfigDocument = EmailConfigDocument.builder()
                    .email("")
                    .emailPassword("")
                    .userID(savedUser.getId())
                    .firstName("")
                    .lastName("")
                    .displayName("")
                    .smtpHost("")
                    .tlsPort("")
                    .build();

            emailConfigs.save(emailConfigDocument);
            savedUser.setEmailInformation(emailConfigDocument);
            users.save(savedUser);

            return ReducedUserModel.builder()
                    .username(savedUser.getUsername())
                    .id(savedUser.getId())
                    .build();
        } else throw new NotAuthorizedError();
    }
}
