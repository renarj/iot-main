package com.oberasoftware.home.rules.blockly;

import com.oberasoftware.iot.core.exceptions.IOTException;

/**
 * @author Renze de Vries
 */
public class BlocklyParseException extends IOTException {
    public BlocklyParseException(String message, Throwable e) {
        super(message, e);
    }

    public BlocklyParseException(String message) {
        super(message);
    }
}
