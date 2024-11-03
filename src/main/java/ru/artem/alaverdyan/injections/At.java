package ru.artem.alaverdyan.injections;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ /* No targets allowed */ })
@Retention(RetentionPolicy.RUNTIME)
public @interface At {
    public String value();

    public int by() default 0;

}