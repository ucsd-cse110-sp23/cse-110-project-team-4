package org.agilelovers.server.user.errors;

import org.agilelovers.server.question.common.errors.UserNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class UserExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(UserNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String invalidDataHandler(ConstraintViolationException err) {
        return err.getMessage();
    }
}
