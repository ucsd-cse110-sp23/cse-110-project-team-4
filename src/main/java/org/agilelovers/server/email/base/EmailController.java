package org.agilelovers.server.email.base;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.EmailUtil;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.common.errors.NoEmailFound;
import org.agilelovers.server.email.returned.ReturnedEmailDocument;
import org.agilelovers.server.email.returned.ReturnedEmailRepository;
import org.agilelovers.server.user.models.UserDocument;
import org.agilelovers.server.email.config.UserEmailConfigDocument;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.*;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.List;
import java.util.Properties;

import static org.agilelovers.common.CommandType.CREATE_EMAIL;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final UserRepository users;
    private final EmailRepository emails;
    private final ReturnedEmailRepository emailsSent;
    private final OpenAIClient client;

    public EmailController(EmailRepository emails, UserRepository users, ReturnedEmailRepository emailsSent) {
        this.emails = emails;
        this.users = users;
        this.emailsSent = emailsSent;
        this.client = new OpenAIClient();
    }

    @GetMapping("/get/all/{uid}")
    public List<EmailDocument> getAllEmailsFromUserId(@PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return emails.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @GetMapping("/get/{uid}")
    public EmailDocument getEmailByID(@PathVariable @ApiParam(name = "id", value = "User ID") String uid,
                                      String emailID) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        return emails.findById(emailID)
                .orElseThrow(() -> new NoEmailFound(emailID));

    }

    @PostMapping("/post/{uid}")
    public EmailDocument createEmail(@PathVariable @ApiParam(name = "id", value = "User ID") String uid,
                                     @RequestBody @ApiParam(name = "prompt",
                                             value = "Email prompt used to generate the email body") String prompt) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        UserDocument user = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
        String body = this.client.getAnswer(prompt);

        return emails.save(EmailDocument.builder()
                .entirePrompt(prompt)
                .body(body + "\n " + user.getEmailInformation().getDisplayName())
                .userId(uid)
                .build()
        );
    }

    @ApiOperation(value = "Delete an email", notes = "Deletes an email")
    @DeleteMapping("/delete/{id}")
    public void deleteEmail(@PathVariable @ApiParam(name = "id", value = "Email ID") String id) {
        emails.deleteById(id);
    }

    @ApiOperation(value = "Delete all emails", notes = "Delete all emails by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all emails by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/delete/all/{uid}")
    public void deleteAllEmailsFromUser(@PathVariable String uid) {
        emails.deleteAll(emails.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }

    @ApiOperation(value = "Send email", notes = "Sends email towards a specified user")
    @PostMapping("/send/{uid}")
    public ReturnedEmailDocument sendEmail(@PathVariable String uid,
                                           @RequestBody @ApiParam(name = "email information",
                                               value = "information required to send an email")
                                           EmailData emailInfo) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        if (!emailInfo.getCommand().equals(CREATE_EMAIL)){
            return emailsSent.save(ReturnedEmailDocument.builder()
                    .userId(emailInfo.getUserId())
                    .entirePrompt(emailInfo.getEntirePrompt())
                    .confirmationOfEmailSent("Please select an email draft to send")
                    .build()
            );
        }

        EmailDocument email = emails.findById(emailInfo.getSentId())
                .orElseThrow(() -> new NoEmailFound(emailInfo.getSentId()));
        UserDocument currentUser = this.users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
        UserEmailConfigDocument emailConfig = currentUser.getEmailInformation();

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

        return  emailsSent.save(EmailUtil.sendEmail(session, emailInfo.getRecipient(),  email.getBody(),
                                emailConfig, emailInfo.getEntirePrompt()));

    }
}
