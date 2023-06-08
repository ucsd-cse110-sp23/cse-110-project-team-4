package org.agilelovers.server.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = URLValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface URLConstraint {
    String message() default "invalid url";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
