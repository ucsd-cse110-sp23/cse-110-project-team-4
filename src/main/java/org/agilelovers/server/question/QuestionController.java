package org.agilelovers.server.question;

import org.agilelovers.server.user.UserDocument;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.ui.object.Question;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {
    private final UserRepository users;
    private final QuestionRepository questions;
    public QuestionController(QuestionRepository questions,
                              UserRepository users) {
        this.users = users;
        this.questions = questions;
    }

    @GetMapping("/api/questions")
    public List<QuestionDocument> getAllQuestionsFromUserId(@RequestBody String userId) {
        return questions.findAllByUserId(userId)
                .orElseThrow(() -> new QuestionNotFoundError(userId));
    }

    @PostMapping("/api/questions")
    public QuestionDocument createQuestion(@RequestBody QuestionDocument question) {
        if (users.existsById(question.getUserId()))
            return questions.save(question);
        else
            throw new QuestionNotFoundError(question.getUserId());
    }

    @DeleteMapping("/api/questions/")
    public void deleteQuestion(@RequestBody String questionId) {
        questions.deleteById(questionId);
    }
}
