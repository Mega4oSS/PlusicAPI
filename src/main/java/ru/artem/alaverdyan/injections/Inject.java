package ru.artem.alaverdyan.injections;

import ru.artem.alaverdyan.LocalCapture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    public String[] method() default {};
    public Class<?>[] constructorParameters() default {};
    public Class<?>[] methodParameters() default {};
    public At[] at();
    public ReturnTo returnTo() default @ReturnTo(to = "");
    public boolean cancellable() default false;
    public LocalCapture locals() default LocalCapture.NO_CAPTURE;
    public boolean remap() default true;
    public int require() default -1;
    public int expect() default 1;
    public int allow() default -1;
    public String constraints() default "";
    public int order() default 1000;
    AfterCall afterCall() default AfterCall.NONE;
}