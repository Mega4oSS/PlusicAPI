package ru.artem.alaverdyan.injections;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ /* No targets allowed */ })
@Retention(RetentionPolicy.RUNTIME)
public @interface Slice {
    public String id() default "";

    public At from() default @At("HEAD");

    public At to() default @At("TAIL");

}
