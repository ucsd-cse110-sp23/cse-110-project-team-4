package org.agilelovers.server.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.user.models.UserEmailConfigDocument;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

@RestController
@ApiOperation("Email Configuration API")
public class UserEmailConfigController {

    private final UserRepository users;
    private final UserEmailRepository emailConfigurations;

    public UserEmailConfigController(UserRepository users, UserEmailRepository emailConfigurations){
        this.users = users;
        this.emailConfigurations = emailConfigurations;
    }

    @ApiOperation(value = "Get email configuration", notes = "Get email configuration linked to user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got user's email configuration"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/api/emailconfig/{uid}")
    public UserEmailConfigDocument getEmailConfig(@PathVariable @ApiParam(name = "id", value = "User ID") String uid){
        return emailConfigurations.findByUserID(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }


    @ApiOperation(value = "Email configuration", notes = "Set up email configurations to be able to send emails")
    @PostMapping("/api/emailconfig/{uid}")
    public UserEmailConfigDocument saveEmailConfig(@RequestBody @ApiParam(name = "User Email Configuration",
                                                value = "information relating to user email config") UserEmailConfigDocument emailConfig,
                                                   @PathVariable @ApiParam(name = "id", value = "User ID") String uid){
        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        return emailConfigurations.save(UserEmailConfigDocument.builder()
                .userID(emailConfig.getUserID())
                .firstName(emailConfig.getFirstName())
                .lastName(emailConfig.getLastName())
                .email(emailConfig.getEmail())
                .emailPassword(emailConfig.getEmailPassword())
                .displayName(emailConfig.getDisplayName())
                .smtpHost(emailConfig.getSmtpHost())
                .tlsPort(emailConfig.getTlsPort())
                .build()
        );

    }

}