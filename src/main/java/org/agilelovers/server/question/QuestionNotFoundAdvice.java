package org.agilelovers.server.question;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class QuestionNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(QuestionNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String questionNotFoundError(QuestionNotFoundError err) {
        return err.getMessage();
    }
}
