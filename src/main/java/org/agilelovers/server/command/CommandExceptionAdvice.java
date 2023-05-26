package org.agilelovers.server.command;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class CommandExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(CommandNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String commandNotFoundError(CommandNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String invalidDataHandler(ConstraintViolationException err) {
        return err.getMessage();
    }
}
