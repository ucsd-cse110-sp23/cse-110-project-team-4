package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

@RestController
@ApiOperation("Email Configuration API")
public class UserEmailController {

    private final UserRepository users;
    private final UserEmailRepository emailConfigurations;

    public UserEmailController(UserRepository users, UserEmailRepository emailConfigurations){
        this.users = users;
        this.emailConfigurations = emailConfigurations;
    }

    @ApiOperation(value = "Get email configuration", notes = "Get email configuration linked to user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got user's email configuration"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/api/emailconfig/{uid}")
    public UserEmailDocument getEmailConfig(@PathVariable @ApiParam(name = "id", value = "User ID") String uid){
        return emailConfigurations.findEmailConfig(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }


    @ApiOperation(value = "Email configuration", notes = "Set up email configurations to be able to send emails")
    @PostMapping("/api/emailconfig/{uid}")
    public UserEmailDocument saveEmailConfig(@RequestBody @ApiParam(name = "firstName", value = "New first Name") String firstName,
                                           @RequestBody @ApiParam(name = "lastName", value = "New last Name") String lastName,
                                           @RequestBody @ApiParam(name = "email", value = "New email") String email,
                                           @RequestBody @ApiParam(name = "emailPassword", value = "New email password") String emailPassword,
                                           @RequestBody @ApiParam(name = "displayName", value = "New display name") String displayName,
                                           @RequestBody @ApiParam(name = "smtpHost", value = "New smtp host") String smtpHost,
                                           @RequestBody @ApiParam(name = "tlsPort", value = "New tls port") String tlsPort,
                                           @PathVariable @ApiParam(name = "id", value = "User ID") String uid){
        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        return emailConfigurations.save(UserEmailDocument.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .emailPassword(emailPassword)
                .displayName(displayName)
                .smtpHost(smtpHost)
                .tlsPort(tlsPort)
                .build()
        );

    }

}
