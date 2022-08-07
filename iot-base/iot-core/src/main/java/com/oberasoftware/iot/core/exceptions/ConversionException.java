package com.oberasoftware.iot.core.exceptions;

/**
 * @author Renze de Vries
 */
public class ConversionException extends RuntimeIOTException {
    public ConversionException(String message, Throwable e) {
        super(message, e);
    }

    public ConversionException(String message) {
        super(message);
    }
}
