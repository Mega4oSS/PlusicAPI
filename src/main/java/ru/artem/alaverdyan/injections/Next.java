package ru.artem.alaverdyan.injections;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ })
@Retention(RetentionPolicy.RUNTIME)
public @interface Next {

    public String name() default "";

    public Class<?> ret() default void.class;

    public Class<?>[] args() default { };

    public int min() default 0;

    public int max() default Integer.MAX_VALUE;

}