package dev.helight.odysseus.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Routine {

    String value();
    boolean singleton() default false;
    long delay() default 0;
    long repeat() default 0;

}
