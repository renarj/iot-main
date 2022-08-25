package com.oberasoftware.home.agent.core.handlers.converters;

/**
 * @author renarj
 */
public interface CommandConverter<S, T> {
    T map(S source);
}
