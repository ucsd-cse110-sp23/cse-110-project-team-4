package org.agilelovers.server.assistant.errors;

import org.agilelovers.server.user.errors.UserNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AssistantExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(UserNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundError(UserNotFoundError err) {
        return err.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoAudioError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String noAudioError(NoAudioError err) {
        return err.getMessage();
    }
}