package org.agilelovers.server.question;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sayit-questions")
public class QuestionDocument {
    @Id private String id;
    private String userId;
    private String question;
    private String answer;

    public QuestionDocument(String id, String userId, String question,
                            String answer) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}