package org.agilelovers.server.question;

import org.agilelovers.server.user.UserRepository;
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

    @GetMapping("/api/questions/{uid}")
    public List<QuestionDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return questions.findAllByUserId(uid)
                .orElseThrow(() -> new QuestionNotFoundError(uid, true));
    }

    @PostMapping("/api/questions")
    public QuestionDocument createQuestion(@RequestBody QuestionDocument question) {
        if (users.existsById(question.getUserId()))
            return questions.save(question);
        else
            throw new QuestionNotFoundError(question.getUserId(), true);
    }

    @DeleteMapping("/api/questions/delete/{id}")
    public void deleteQuestion(@PathVariable String id) {
        questions.deleteById(id);
    }

    @DeleteMapping("/api/questions/delete-all/{uid}")
    public void deleteAllQuestionsFromUser(@PathVariable String uid) {
        questions.deleteAll(questions.findAllByUserId(uid)
                .orElseThrow(() -> new QuestionNotFoundError(uid, true)));
    }
}
