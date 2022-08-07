package com.oberasoftware.iot.core.exceptions;

/**
 * @author renarj
 */
public class IOTException extends Exception {
    public IOTException(String message, Throwable e) {
        super(message, e);
    }

    public IOTException(String message) {
        super(message);
    }
}
