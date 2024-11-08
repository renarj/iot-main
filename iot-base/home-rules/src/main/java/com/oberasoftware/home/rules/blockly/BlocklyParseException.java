package com.oberasoftware.home.rules.blockly;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;

/**
 * @author Renze de Vries
 */
public class BlocklyParseException extends RuntimeIOTException {
    public BlocklyParseException(String message, Throwable e) {
        super(message, e);
    }

    public BlocklyParseException(String message) {
        super(message);
    }
}
