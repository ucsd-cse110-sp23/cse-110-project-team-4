package org.agilelovers.server.question.errors;

import org.agilelovers.server.common.errors.NoAudioError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class QuestionExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(QuestionNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String questionNotFoundError(QuestionNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundError(UserNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoAudioError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noAudioError(NoAudioError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String invalidDataHandler(ConstraintViolationException err) {
        return err.getMessage();
    }
}
