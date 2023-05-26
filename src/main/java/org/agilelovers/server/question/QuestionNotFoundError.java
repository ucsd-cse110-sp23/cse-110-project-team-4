package org.agilelovers.server.question;

public class QuestionNotFoundError extends RuntimeException{
    public QuestionNotFoundError(String id, boolean isUserId) {
        super("Could not find question " + (isUserId ? "<user_id> " : "") + id);
    }
}
