package org.agilelovers.server.email;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.common.errors.NoEmailFound;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@ApiOperation("ReturnedEmails API")
public class ReturnedEmailController {

    ReturnedEmailRepository emailRepository;
    private final UserRepository users;
    public ReturnedEmailController(ReturnedEmailRepository emailRepository, UserRepository users) {
        this.emailRepository = emailRepository;
        this.users = users;
    }

    @ApiOperation(value = "Get all returned Emails", notes = "Get all the returned Emails corresponding to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got all returned_Email"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/api/all-returnedEmails/{uid}")
    public List<ReturnedEmailDocument> getAllReturnedEmails(@PathVariable @ApiParam(name = "uid",
                                                            value = "User ID") String uid) {

        return emailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @ApiOperation(value = "Get specified returned_Email", notes = "Get the email corresponding to its mongo ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got specified returned_Email"),
            @ApiResponse(code = 404, message = "Email not found")
    })
    @GetMapping("/api/returnedEmails")
    public ReturnedEmailDocument getReturnedEmail(String id){
        return emailRepository.findById(id)
                .orElseThrow(() -> new NoEmailFound(id));
    }

    @ApiOperation(value = "Delete a returned email", notes = "Deletes a returned Email")
    @DeleteMapping("/api/returnedEmail/delete/{id}")
    public void deleteReturnedEmail(@PathVariable @ApiParam(name = "id", value = "Returned Email ID") String id) {
        emailRepository.deleteById(id);
    }

    @ApiOperation(value = "Delete all returned emails", notes = "Delete all returned emails by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all returned emails by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/api/returnedEmails/delete-all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        emailRepository.deleteAll(emailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }



}
