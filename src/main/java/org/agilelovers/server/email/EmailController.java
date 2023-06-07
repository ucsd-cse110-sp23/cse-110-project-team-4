package org.agilelovers.server.email;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.email.errors.NoEmailFound;
import org.agilelovers.server.user.models.UserDocument;
import org.agilelovers.server.user.models.UserEmailDocument;
import org.agilelovers.server.user.UserEmailRepository;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.*;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@RestController
public class EmailController {
    private final EmailRepository emails;
    private final UserRepository users;
    private final UserEmailRepository emailConfigurations;
    private final OpenAIClient client;

    public EmailController(EmailRepository emails, UserRepository users, UserEmailRepository emailConfigurations) {
        this.emails = emails;
        this.users = users;
        this.emailConfigurations = emailConfigurations;
        this.client = new OpenAIClient();
    }

    @GetMapping("/api/emails/all/{uid}")
    public List<EmailDocument> getAllEmailsFromUserId(@PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return emails.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @GetMapping("/api/emails/{uid}")
    public EmailDocument getEmailByID(@PathVariable @ApiParam(name = "id", value = "User ID") String uid,
                                      String emailID) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        return emails.findByid(emailID)
                .orElseThrow(() -> new NoEmailFound(emailID));

    }

    @PostMapping("/api/emails/{uid}")
    public EmailDocument createEmail(@PathVariable @ApiParam(name = "id", value = "User ID") String uid,
                                     @RequestBody @ApiParam(name = "prompt",
                                             value = "Email prompt used to generate the email body") String prompt) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        String body = this.client.getAnswer(prompt);

        return emails.save(EmailDocument.builder()
                .prompt(prompt)
                .body(body)
                .userId(uid)
                .build()
        );
    }

    @ApiOperation(value = "Delete an email", notes = "Deletes an email")
    @DeleteMapping("/api/emails/delete/{id}")
    public void deleteEmail(@PathVariable @ApiParam(name = "id", value = "Email ID") String id) {
        emails.deleteById(id);
    }

    @ApiOperation(value = "Delete all emails", notes = "Delete all emails by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all emails by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/api/emails/delete-all/{uid}")
    public void deleteAllEmailsFromUser(@PathVariable String uid) {
        emails.deleteAll(emails.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }

    @ApiOperation(value = "Send email", notes = "Sends email towards a specified user")
    @PostMapping("/api/emails/send/{uid}")
    public ReducedEmailDocument sendEmail(@PathVariable String uid,
                                          @RequestBody @ApiParam(name = "email information",
                                               value = "information required to send an email") EmailInformationDocument emailInfo) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        Optional<EmailDocument> email = emails.findByid(emailInfo.getId());
        Optional<UserDocument> currentUser = this.users.findById(uid);
        UserEmailDocument emailConfig = currentUser.get().getEmailInformation();

        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getSmtpHost());
        props.put("mail.smtp.port", emailConfig.getTlsPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        javax.mail.Authenticator auth = new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getEmail(), emailConfig.getEmailPassword());
            }
        };

        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, emailInfo.getEmailOfUserSentTowards(), email.get().getBody(), emailConfig);

        return ReducedEmailDocument.builder().build();

    }
}
