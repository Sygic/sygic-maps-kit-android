package com.sygic.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoFactory {
    /**
     * A list of interfaces that the generated factory is required to implement.
     */
    Class<?>[] implementing() default { };
}
