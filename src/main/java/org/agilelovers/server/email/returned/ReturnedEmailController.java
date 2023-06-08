package org.agilelovers.server.email.returned;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.common.documents.ReturnedEmailDocument;
import org.agilelovers.server.common.EmailUtil;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.common.errors.NoEmailFound;
import org.agilelovers.common.models.ReturnedEmailModel;
import org.agilelovers.common.documents.EmailDocument;
import org.agilelovers.server.email.base.EmailRepository;
import org.agilelovers.common.documents.EmailConfigDocument;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.common.documents.UserDocument;
import org.springframework.web.bind.annotation.*;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.List;
import java.util.Properties;

import static org.agilelovers.common.CommandType.CREATE_EMAIL;

@RestController
@RequestMapping("/api/email/returned")
@ApiOperation("Returned Emails API")
public class ReturnedEmailController {

    private final UserRepository users;

    private final EmailRepository emails;
    private final ReturnedEmailRepository returnedEmailRepository;
    public ReturnedEmailController(UserRepository users,
                                   EmailRepository emails, ReturnedEmailRepository returnedEmailRepository) {
        this.users = users;
        this.emails = emails;
        this.returnedEmailRepository = returnedEmailRepository;
    }

    @ApiOperation(value = "Get all returned emails", notes = "Get all the returned emails corresponding to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got all returned emails"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/get/all/{uid}")
    public List<ReturnedEmailDocument> getAllReturnedEmails(@PathVariable @ApiParam(name = "uid",
                                                            value = "User ID") String uid) {

        return returnedEmailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @ApiOperation(value = "Get specified returned email", notes = "Get the email corresponding to its ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got specified returned email"),
            @ApiResponse(code = 404, message = "Email not found")
    })
    @GetMapping("/get/{uid}")
    public ReturnedEmailDocument getReturnedEmail(@PathVariable @ApiParam(name = "uid", value = "User ID") String uid,
                                                  @ApiParam(name = "id", value = "ID of returned email document") String id) {
        return returnedEmailRepository.findById(id)
                .orElseThrow(() -> new NoEmailFound(id));
    }

    @ApiOperation(value = "Delete a returned email", notes = "Deletes a returned Email")
    @DeleteMapping("/delete/{id}")
    public void deleteReturnedEmail(@PathVariable @ApiParam(name = "id", value = "Returned Email ID") String id) {
        returnedEmailRepository.deleteById(id);
    }

    @ApiOperation(value = "Delete all returned emails", notes = "Delete all returned emails by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all returned emails by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/delete/all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        returnedEmailRepository.deleteAll(returnedEmailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }

    @ApiOperation(value = "Send email", notes = "Sends email towards a specified user")
    @PostMapping("/send/{uid}")
    public ReturnedEmailDocument sendEmail(@PathVariable String uid,
                                           @RequestBody @ApiParam(name = "email information",
                                                   value = "information required to send an email")
                                           ReturnedEmailModel emailInfo) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        if (!emailInfo.getCommand().equals(CREATE_EMAIL)){
            return returnedEmailRepository.save(ReturnedEmailDocument.builder()
                    .userId(uid)
                    .entirePrompt(emailInfo.getEntirePrompt())
                    .confirmationOfEmailSent("Please select an email draft to send")
                    .build()
            );
        }

        EmailDocument email = emails.findById(emailInfo.getSentId())
                .orElseThrow(() -> new NoEmailFound(emailInfo.getSentId()));
        UserDocument currentUser = this.users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
        EmailConfigDocument emailConfig = currentUser.getEmailInformation();

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

        return returnedEmailRepository.save(EmailUtil.sendEmail(session, emailInfo.getRecipient(),  email.getBody(),
                emailConfig, emailInfo.getEntirePrompt()));

    }

}
