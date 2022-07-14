package com.oberasoftware.home.rules.blockly;

/**
 * @author Renze de Vries
 */
public class BlocklyParseException extends Exception {
    public BlocklyParseException(String message, Throwable e) {
        super(message, e);
    }

    public BlocklyParseException(String message) {
        super(message);
    }
}
