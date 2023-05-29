package org.agilelovers.server.question.errors;

public class NoQuestionError extends RuntimeException{
    public NoQuestionError() {
        super("Question was empty or null!");
    }
}
