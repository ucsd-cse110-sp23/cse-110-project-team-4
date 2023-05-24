package org.agilelovers.server.question;

public class QuestionNotFoundError extends RuntimeException{
    QuestionNotFoundError(String id) {
        super("Could not find question " + id);
    }
}
