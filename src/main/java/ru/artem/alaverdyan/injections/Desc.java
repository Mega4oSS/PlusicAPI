package ru.artem.alaverdyan.injections;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Descriptors.class)
public @interface Desc {

    public String id() default "";

    public Class<?> owner() default void.class;

    public String value();

    public Class<?> ret() default void.class;

    public Class<?>[] args() default { };

    public Next[] next() default { };

    public int min() default 0;

    public int max() default Integer.MAX_VALUE;

}