package com.oberasoftware.iot.core.exceptions;

/**
 * @author renarj
 */
public class RuntimeIOTException extends RuntimeException {
    public RuntimeIOTException(String message, Throwable e) {
        super(message, e);
    }

    public RuntimeIOTException(String message) {
        super(message);
    }
}
