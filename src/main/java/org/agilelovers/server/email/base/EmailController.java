package org.agilelovers.server.email.base;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.common.documents.EmailDocument;
import org.agilelovers.common.models.EmailModel;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.common.errors.NoEmailFound;
import org.agilelovers.server.email.returned.ReturnedEmailRepository;
import org.agilelovers.common.documents.UserDocument;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final UserRepository users;
    private final EmailRepository emails;
    private final OpenAIClient client;

    public EmailController(EmailRepository emails, UserRepository users, ReturnedEmailRepository emailsSent) {
        this.emails = emails;
        this.users = users;
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
                                             value = "Email prompt used to generate the email body") EmailModel emailModel) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        UserDocument user = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
        String body = this.client.getAnswer(emailModel.getPrompt());

        return emails.save(EmailDocument.builder()
                .entirePrompt(emailModel.getPrompt())
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
}
