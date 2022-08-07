package com.oberasoftware.iot.core.commands.converters;

/**
 * @author renarj
 */
public interface CommandConverter<S, T> {
    T map(S source);
}
