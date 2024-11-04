package ru.artem.alaverdyan.injections;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ /* No targets allowed */ })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnTo {
    public String to();
}