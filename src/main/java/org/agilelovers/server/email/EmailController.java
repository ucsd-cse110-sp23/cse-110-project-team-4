package org.agilelovers.server.email;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailController {
    private final EmailRepository emails;
    private final UserRepository users;
    private final OpenAIClient client;

    public EmailController(EmailRepository emails, UserRepository users) {
        this.emails = emails;
        this.users = users;
        this.client = new OpenAIClient();
    }

    @GetMapping("/api/emails/{uid}")
    public List<EmailDocument> getAllEmailsFromUserId(@PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return emails.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @PostMapping("/api/emails/{uid}")
    public EmailDocument createEmail(@PathVariable String uid, @RequestBody String prompt) {

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

    @ApiOperation(value = "Delete a question", notes = "Deletes a question")
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
}
