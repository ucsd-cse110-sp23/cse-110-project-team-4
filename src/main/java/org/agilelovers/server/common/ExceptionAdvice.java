package org.agilelovers.server.common;

import com.mongodb.MongoWriteException;
import org.agilelovers.server.common.errors.NoAudioError;
import org.agilelovers.server.common.errors.NotAuthorizedError;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(UserNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(MongoWriteException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String duplicateUser(MongoWriteException err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NotAuthorizedError.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String unauthorizedAccess(NotAuthorizedError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String invalidDataHandler(ConstraintViolationException err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoAudioError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String noAudioError(NoAudioError err) {
        return err.getMessage();
    }
}
