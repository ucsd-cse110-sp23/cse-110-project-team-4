package org.agilelovers.server.email.config;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.common.documents.EmailConfigDocument;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.server.common.errors.EmailAuthenticationError;
import org.agilelovers.server.common.errors.EmailSetupError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import java.util.Properties;

@RestController
@RequestMapping("/api/email/config")
@ApiOperation("Email Configuration API")
public class EmailConfigController {

    private final UserRepository users;
    private final EmailConfigRepository emailConfigurations;

    public EmailConfigController(UserRepository users, EmailConfigRepository emailConfigurations){
        this.users = users;
        this.emailConfigurations = emailConfigurations;
    }

    @ApiOperation(value = "Get email configuration", notes = "Get email configuration linked to user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got user's email configuration"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/get/{uid}")
    public EmailConfigDocument getEmailConfig(@PathVariable @ApiParam(name = "uid", value = "User ID") String uid) {
        return emailConfigurations.findByUserID(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }


    @ApiOperation(value = "Email configuration", notes = "Set up email configurations to be able to send emails")
    @PostMapping("/save/{uid}")
    @PutMapping("/update/{uid}")
    public EmailConfigDocument saveEmailConfig(@RequestBody @ApiParam(name = "User Email Configuration",
                                                value = "information relating to user email config") EmailConfigModel emailConfig,
                                               @PathVariable @ApiParam(name = "uid", value = "User ID") String uid){
        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        try {
            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect(
                    emailConfig.getSmtpHost(),
                    Integer.parseInt(emailConfig.getTlsPort()),
                    emailConfig.getEmail(),
                    emailConfig.getEmailPassword());
            transport.close();

            return emailConfigurations.findByUserID(uid).map(
                    emailConfigDocument -> {
                        emailConfigDocument.setFirstName(emailConfig.getFirstName());
                        emailConfigDocument.setLastName(emailConfig.getLastName());
                        emailConfigDocument.setEmail(emailConfig.getEmail());
                        emailConfigDocument.setEmailPassword(emailConfig.getEmailPassword());
                        emailConfigDocument.setDisplayName(emailConfig.getDisplayName());
                        emailConfigDocument.setSmtpHost(emailConfig.getSmtpHost());
                        emailConfigDocument.setTlsPort(emailConfig.getTlsPort());
                        return emailConfigurations.save(emailConfigDocument);
                    }
            ).orElse(
                emailConfigurations.save(EmailConfigDocument.builder()
                        .userID(uid)
                        .firstName(emailConfig.getFirstName())
                        .lastName(emailConfig.getLastName())
                        .email(emailConfig.getEmail())
                        .emailPassword(emailConfig.getEmailPassword())
                        .displayName(emailConfig.getDisplayName())
                        .smtpHost(emailConfig.getSmtpHost())
                        .tlsPort(emailConfig.getTlsPort())
                        .build()
                )
            );


        } catch (AuthenticationFailedException e) {
            throw new EmailAuthenticationError(
                    emailConfig.getEmail(),
                    emailConfig.getEmailPassword()
            );
        } catch (NumberFormatException | MessagingException e) {
            throw new EmailSetupError(
                    emailConfig.getSmtpHost(),
                    emailConfig.getTlsPort(),
                    emailConfig.getEmail(),
                    emailConfig.getEmailPassword()
            );
        }
    }
}
