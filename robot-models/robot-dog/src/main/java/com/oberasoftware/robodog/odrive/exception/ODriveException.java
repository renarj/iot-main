package com.oberasoftware.robodog.odrive.exception;

public class ODriveException extends RuntimeException {
    public ODriveException(String message) {
        super(message);
    }

    public ODriveException(String message, Throwable cause) {
        super(message, cause);
    }
}
