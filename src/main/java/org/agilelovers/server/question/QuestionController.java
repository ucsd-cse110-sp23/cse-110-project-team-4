package org.agilelovers.server.question;

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
@ApiOperation("Questions API")
public class QuestionController {
    private final QuestionRepository questions;
    private final UserRepository users;
    private final OpenAIClient client;

    public QuestionController(QuestionRepository questions, UserRepository users) {
        this.users = users;
        this.questions = questions;
        this.client = new OpenAIClient();
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

    @PostMapping("/api/questions/{uid}")
    public QuestionDocument createQuestion(@PathVariable String uid, @RequestBody String question) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        String answer = this.client.getAnswer(question);

        return questions.save(QuestionDocument.builder()
                .question(question)
                .answer(answer)
                .userId(uid)
                .build()
        );
    }

    @ApiOperation(value = "Delete a question", notes = "Deletes a question")
    @DeleteMapping("/api/questions/delete/{id}")
    public void deleteQuestion(@PathVariable @ApiParam(name = "id", value = "Question ID") String id) {
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
