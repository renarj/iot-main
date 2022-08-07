package com.oberasoftware.iot.core.exceptions;

/**
 * @author renarj
 */
public class DataStoreException extends IOTException {
    public DataStoreException(String message, Throwable e) {
        super(message, e);
    }

    public DataStoreException(String message) {
        super(message);
    }
}
