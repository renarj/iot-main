package com.oberasoftware.trainautomation.rocoz21;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Z21ResponseFilter {
    int xHeader() default -1;

    int packageHeader() default -1;

    int firstDataByte() default -1;
}
