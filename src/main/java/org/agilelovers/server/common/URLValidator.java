package org.agilelovers.server.common;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;

public class URLValidator implements ConstraintValidator<URLConstraint, String> {
    @Override
    public void initialize(URLConstraint constraint) {

    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext cxt) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
