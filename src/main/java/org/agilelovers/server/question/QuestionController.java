package org.agilelovers.server.question;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiOperation("Questions API")
public class QuestionController {
    private final QuestionRepository questions;
    public QuestionController(QuestionRepository questions) {
        this.questions = questions;
    }

    @ApiOperation(value = "Get all questions", notes = "Get all the questions corresponding to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully got all questions"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/api/questions/{uid}")
    public List<QuestionDocument> getAllQuestionsFromUserId(@PathVariable @ApiParam(name = "id", value = "User ID") String uid) {
        return questions.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));
    }

    @ApiOperation(value = "Delete a question", notes = "Deletes a question")
    @DeleteMapping("/api/questions/delete/{id}")
    public void deleteQuestion(@PathVariable  @ApiParam(name = "id", value = "Question ID") String id) {
        questions.deleteById(id);
    }

    @ApiOperation(value = "Delete all questions", notes = "Delete all questions by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted all questions by a user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/api/questions/delete-all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        questions.deleteAll(questions.findAllByUserId(uid)
                .orElseThrow(() -> new UserNotFoundError(uid)));
    }
}
