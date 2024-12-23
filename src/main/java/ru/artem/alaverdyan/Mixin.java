package ru.artem.alaverdyan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mixin {
    String[] value() default {};

    String[] targets() default {};

    int priority() default 1000;

    boolean remap() default true;
}

