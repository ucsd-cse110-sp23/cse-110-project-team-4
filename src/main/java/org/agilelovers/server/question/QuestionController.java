package org.agilelovers.server.question;

import org.agilelovers.server.question.errors.QuestionNotFoundError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {
    private final QuestionRepository questions;
    public QuestionController(QuestionRepository questions) {
        this.questions = questions;
    }

    @GetMapping("/api/questions/{uid}")
    public List<QuestionDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return questions.findAllByUserId(uid)
                .orElseThrow(() -> new QuestionNotFoundError(uid, true));
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
