package org.agilelovers.server.question;

import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.question.errors.NoQuestionError;
import org.agilelovers.server.question.errors.QuestionNotFoundError;
import org.agilelovers.server.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {
    private final UserRepository users;
    private final QuestionRepository questions;
    private final OpenAIClient client;

    @Autowired
    public QuestionController(QuestionRepository questions,
                              UserRepository users,
                              OpenAIClient client) {
        this.users = users;
        this.questions = questions;
        this.client = client;
    }

    public QuestionController(QuestionRepository questions,
                              UserRepository users) {
        this.users = users;
        this.questions = questions;
        this.client = new OpenAIClient();
    }

    @GetMapping("/api/questions/{uid}")
    public List<QuestionDocument> getAllQuestionsFromUserId(@PathVariable String uid) {
        return questions.findAllByUserId(uid)
                .orElseThrow(() -> new QuestionNotFoundError(uid, true));
    }

    @PostMapping("/api/questions/{uid}")
    public QuestionDocument createQuestion(@RequestBody String question, @PathVariable String uid) {

        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        if (question != null && !question.isEmpty()) {

            String answer = this.client.getAnswer(question);

            QuestionDocument questionDocument = QuestionDocument.builder()
                    .question(question)
                    .answer(answer)
                    .userId(uid)
                    .build();

            return questions.save(questionDocument);
        } else {
            throw new NoQuestionError();
        }
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
