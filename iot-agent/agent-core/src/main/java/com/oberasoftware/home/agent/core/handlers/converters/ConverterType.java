package com.oberasoftware.home.agent.core.handlers.converters;

import com.oberasoftware.iot.core.commands.impl.CommandType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author renarj
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterType {
    CommandType commandType();
}
