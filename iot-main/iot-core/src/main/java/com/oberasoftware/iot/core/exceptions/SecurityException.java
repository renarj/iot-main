package com.oberasoftware.iot.core.exceptions;

/**
 * @author Renze de Vries
 */
public class SecurityException extends RuntimeIOTException {
    public SecurityException(String message, Throwable e) {
        super(message, e);
    }

    public SecurityException(String message) {
        super(message);
    }
}
