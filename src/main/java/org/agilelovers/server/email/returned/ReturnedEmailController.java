package org.agilelovers.server.email.returned;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.common.errors.NoEmailFound;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email/returned")
@ApiOperation("Returned Emails API")
public class ReturnedEmailController {

    ReturnedEmailRepository emailRepository;
    public ReturnedEmailController(ReturnedEmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @ApiOperation(value = "Get all returned emails", notes = "Get all the returned emails corresponding to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got all returned emails"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/get/all/{uid}")
    public List<ReturnedEmailDocument> getAllReturnedEmails(@PathVariable @ApiParam(name = "uid",
                                                            value = "User ID") String uid) {

        return emailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @ApiOperation(value = "Get specified returned email", notes = "Get the email corresponding to its ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got specified returned email"),
            @ApiResponse(code = 404, message = "Email not found")
    })
    @GetMapping("/get/{uid}")
    public ReturnedEmailDocument getReturnedEmail(@PathVariable @ApiParam(name = "uid", value = "User ID") String uid) {
        return emailRepository.findById(uid)
                .orElseThrow(() -> new NoEmailFound(uid));
    }

    @ApiOperation(value = "Delete a returned email", notes = "Deletes a returned Email")
    @DeleteMapping("/delete/{id}")
    public void deleteReturnedEmail(@PathVariable @ApiParam(name = "id", value = "Returned Email ID") String id) {
        emailRepository.deleteById(id);
    }

    @ApiOperation(value = "Delete all returned emails", notes = "Delete all returned emails by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all returned emails by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/delete/all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        emailRepository.deleteAll(emailRepository.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }



}
